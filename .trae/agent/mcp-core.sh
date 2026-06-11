#!/bin/bash
# ============================================================================
# Elink-AI 智能体 MCP 核心编排器
# Model Context Protocol Core Orchestrator
# ============================================================================
# 功能：规则加载、技能调度、记忆管理、上下文构建、命令路由
# 版本：v1.0.0
# ============================================================================

set -euo pipefail

# ---- 常量定义 ----
AGENT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="$(cd "${AGENT_ROOT}/../.." && pwd)"
AGENT_BIN="${AGENT_ROOT}/agent"
AGENT_SKILLS="${AGENT_ROOT}/skills"
AGENT_COMMANDS="${AGENT_ROOT}/commands"
AGENT_MEMORY="${AGENT_ROOT}/memory"
AGENT_RULES="${AGENT_ROOT}/rules"
AGENT_LOGS="${AGENT_ROOT}/logs"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# ---- 日志系统 ----
_ts() { date '+%Y-%m-%d %H:%M:%S'; }

log_info()  { echo -e "${GREEN}[MCP-INFO]  $(ts) $1${NC}"; }
log_warn()  { echo -e "${YELLOW}[MCP-WARN]  $(ts) $1${NC}"; }
log_error() { echo -e "${RED}[MCP-ERROR] $(ts) $1${NC}"; }
log_debug() { [[ "${MCP_DEBUG:-0}" == "1" ]] && echo -e "${CYAN}[MCP-DEBUG] $(ts) $1${NC}"; }
log_skill() { echo -e "${BLUE}[MCP-SKILL] $(ts) $1${NC}"; }

# ---- 规则引擎 ----

# 加载所有规则文件，构建上下文
mcp_load_rules() {
    local context=""
    local rule_count=0

    for rule_file in "${AGENT_RULES}"/*.md; do
        [[ -f "$rule_file" ]] || continue
        local rule_name=$(basename "$rule_file" .md)
        local scene=""
        # 提取scene元数据
        scene=$(grep -oP '^scene:\s*\K.*' "$rule_file" 2>/dev/null || echo "general")

        context+="[RULE:${rule_name}|scene:${scene}]\n"
        # 提取关键内容（标题和P0/P1项）
        context+=$(grep -E '^#+\s|^###\sP[012]|^- \[.*P[012]|^\|.*P[012]\|' "$rule_file" 2>/dev/null | head -30)
        context+="\n\n"
        ((rule_count++))
    done

    log_debug "已加载 ${rule_count} 条规则"
    echo -e "$context"
}

# 根据场景过滤规则
mcp_filter_rules() {
    local scene="$1"
    local filtered=""

    for rule_file in "${AGENT_RULES}"/*.md; do
        [[ -f "$rule_file" ]] || continue
        local rule_scene=$(grep -oP '^scene:\s*\K.*' "$rule_file" 2>/dev/null || echo "general")
        if [[ "$rule_scene" == "$scene" || "$rule_scene" == "general" || "$rule_scene" == "alwaysApply" ]]; then
            filtered+="$(cat "$rule_file")\n---RULE_SEPARATOR---\n"
        fi
    done

    echo -e "$filtered"
}

# ---- 记忆管理 ----

# 初始化记忆存储
mcp_memory_init() {
    mkdir -p "${AGENT_MEMORY}"
    local memory_index="${AGENT_MEMORY}/index.jsonl"
    [[ -f "$memory_index" ]] || echo '{"version":"1.0","created":"'$(date -Iseconds)'"}' > "$memory_index"
}

# 存储记忆条目
mcp_memory_store() {
    local category="$1"  # decision|progress|issue|context
    local key="$2"
    local value="$3"
    local memory_index="${AGENT_MEMORY}/index.jsonl"

    mcp_memory_init

    local entry=$(cat <<EOF
{"ts":"$(date -Iseconds)","category":"${category}","key":"${key}","value":"${value}"}
EOF
)
    echo "$entry" >> "$memory_index"
    log_debug "记忆已存储: [${category}] ${key}"
}

# 查询记忆
mcp_memory_query() {
    local category="${1:-}"
    local key="${2:-}"
    local memory_index="${AGENT_MEMORY}/index.jsonl"

    [[ -f "$memory_index" ]] || return 0

    if [[ -n "$category" && -n "$key" ]]; then
        grep "\"category\":\"${category}\"" "$memory_index" | grep "\"key\":\"${key}\"" | tail -5
    elif [[ -n "$category" ]]; then
        grep "\"category\":\"${category}\"" "$memory_index" | tail -10
    else
        tail -20 "$memory_index"
    fi
}

# 获取项目状态快照
mcp_memory_snapshot() {
    local snapshot_file="${AGENT_MEMORY}/snapshot-$(date '+%Y%m%d').json"
    local backend_services=0
    local frontend_projects=0
    local docker_containers=0
    local p0_issues=0
    local p1_issues=0

    # 统计后端服务
    for svc in auth-service sunmax-gateway system-service device-service data-service \
               protocol-service crontab-service devops-service configure-service together-service webapp-service; do
        [[ -d "${PROJECT_ROOT}/elink-work/${svc}" ]] && ((backend_services++))
    done

    # 统计前端项目
    for proj in linkos derms tycvs; do
        [[ -d "${PROJECT_ROOT}/elink-web/${proj}" ]] && ((frontend_projects++))
    done

    # 统计Docker容器
    docker_containers=$(docker ps -q -f "name=-service" 2>/dev/null | wc -l || echo "0")

    # 统计已知问题
    p0_issues=$(mcp_memory_query "issue" | grep -c '"P0"' 2>/dev/null || echo "0")
    p1_issues=$(mcp_memory_query "issue" | grep -c '"P1"' 2>/dev/null || echo "0")

    cat > "$snapshot_file" <<EOF
{
  "timestamp": "$(date -Iseconds)",
  "backend_services": ${backend_services},
  "frontend_projects": ${frontend_projects},
  "docker_containers_running": ${docker_containers},
  "known_p0_issues": ${p0_issues},
  "known_p1_issues": ${p1_issues},
  "refactor_phase": "PHASE-1",
  "refactor_status": "pending"
}
EOF

    echo "$snapshot_file"
}

# ---- 技能调度器 ----

# 注册的技能列表
MCP_SKILLS_REGISTRY=(
    "code-review:${AGENT_SKILLS}/code-review.sh"
    "security-scan:${AGENT_SKILLS}/security-scan.sh"
    "docker-ops:${AGENT_SKILLS}/docker-ops.sh"
    "refactor:${AGENT_SKILLS}/refactor.sh"
    "health-check:${AGENT_SKILLS}/health-check.sh"
)

# 获取技能路径
mcp_skill_path() {
    local skill_name="$1"
    for entry in "${MCP_SKILLS_REGISTRY[@]}"; do
        local name="${entry%%:*}"
        local path="${entry##*:}"
        if [[ "$name" == "$skill_name" ]]; then
            echo "$path"
            return 0
        fi
    done
    return 1
}

# 列出可用技能
mcp_skill_list() {
    echo -e "${BOLD}可用技能模块：${NC}"
    for entry in "${MCP_SKILLS_REGISTRY[@]}"; do
        local name="${entry%%:*}"
        local path="${entry##*:}"
        local status="未安装"
        [[ -x "$path" ]] && status="${GREEN}已就绪${NC}"
        printf "  %-18s %s [%s]\n" "$name" "$path" "$status"
    done
}

# 执行技能
mcp_skill_execute() {
    local skill_name="$1"
    shift
    local skill_args="$@"

    local skill_path=$(mcp_skill_path "$skill_name")
    if [[ $? -ne 0 ]]; then
        log_error "未知技能: ${skill_name}"
        mcp_skill_list
        return 1
    fi

    if [[ ! -x "$skill_path" ]]; then
        log_error "技能未安装或不可执行: ${skill_path}"
        return 1
    fi

    log_skill "执行技能: ${skill_name} ${skill_args}"

    # 记录执行历史
    mcp_memory_store "execution" "${skill_name}" "args=${skill_args}"

    # 执行技能
    "$skill_path" $skill_args
    local result=$?

    if [[ $result -eq 0 ]]; then
        mcp_memory_store "execution_result" "${skill_name}" "success"
    else
        mcp_memory_store "execution_result" "${skill_name}" "failed(code=${result})"
    fi

    return $result
}

# ---- 上下文构建器 ----

# 为AI交互构建完整上下文
mcp_build_context() {
    local scene="${1:-general}"
    local context=""

    context+="===== ELINK-AI 项目上下文 =====\n"
    context+="生成时间: $(date -Iseconds)\n"
    context+="场景: ${scene}\n\n"

    # 1. 项目概况
    context+="--- 项目结构 ---\n"
    context+="项目根目录: ${PROJECT_ROOT}\n"
    context+="后端目录: ${PROJECT_ROOT}/elink-work (Maven多模块, 11个服务)\n"
    context+="前端目录: ${PROJECT_ROOT}/elink-web (3个Vue项目)\n"
    context+="Docker Compose: ${PROJECT_ROOT}/elink-work/docker-compose.yml\n"
    context+="热更新脚本: ${PROJECT_ROOT}/elink-work/hot-reload.sh\n\n"

    # 2. 规则上下文
    context+="--- 规则约束 ---\n"
    context+="$(mcp_filter_rules "$scene")\n\n"

    # 3. 记忆上下文
    context+="--- 最近记忆 ---\n"
    context+="$(mcp_memory_query | tail -10)\n\n"

    # 4. 服务状态
    context+="--- 服务状态 ---\n"
    context+="$(mcp_get_service_status)\n\n"

    echo -e "$context"
}

# 获取服务状态摘要
mcp_get_service_status() {
    local services="auth-service sunmax-gateway system-service device-service data-service
                    protocol-service crontab-service devops-service configure-service together-service webapp-service"

    printf "%-20s %-10s %-10s\n" "SERVICE" "STATUS" "HEALTH"
    printf "%-20s %-10s %-10s\n" "-------" "------" "------"

    for svc in $services; do
        local container_id=$(docker ps -q -f "name=^${svc}$" 2>/dev/null)
        if [[ -n "$container_id" ]]; then
            local health=$(docker inspect --format='{{.State.Health.Status}}' "$svc" 2>/dev/null || echo "none")
            printf "%-20s %-10s %-10s\n" "$svc" "running" "$health"
        else
            printf "%-20s %-10s %-10s\n" "$svc" "stopped" "-"
        fi
    done
}

# ---- 命令路由 ----

mcp_route() {
    local command="$1"
    shift || true

    case "$command" in
        # 技能执行
        review|audit)
            mcp_skill_execute "code-review" "$@"
            ;;
        security|sec)
            mcp_skill_execute "security-scan" "$@"
            ;;
        docker|deploy)
            mcp_skill_execute "docker-ops" "$@"
            ;;
        refactor|upgrade)
            mcp_skill_execute "refactor" "$@"
            ;;
        health|check)
            mcp_skill_execute "health-check" "$@"
            ;;

        # 上下文管理
        context)
            mcp_build_context "${1:-general}"
            ;;
        rules)
            mcp_load_rules
            ;;
        snapshot)
            local snap_file=$(mcp_memory_snapshot)
            log_info "快照已生成: ${snap_file}"
            cat "$snap_file"
            ;;

        # 记忆管理
        remember)
            local category="${1:-context}"
            local key="${2:-manual}"
            local value="${3:-}"
            [[ -z "$value" ]] && { log_error "用法: mcp remember <category> <key> <value>"; return 1; }
            mcp_memory_store "$category" "$key" "$value"
            log_info "已记忆: [${category}] ${key}=${value}"
            ;;
        recall)
            mcp_memory_query "${1:-}" "${2:-}"
            ;;

        # 技能管理
        skills)
            mcp_skill_list
            ;;

        # 状态
        status)
            mcp_get_service_status
            ;;

        # 帮助
        help|--help|-h)
            mcp_show_help
            ;;

        *)
            log_error "未知命令: ${command}"
            mcp_show_help
            return 1
            ;;
    esac
}

# ---- 帮助信息 ----

mcp_show_help() {
    cat <<EOF
${BOLD}Elink-AI 智能体 MCP 核心 v1.0.0${NC}

${BOLD}用法:${NC}
  mcp-core.sh <command> [options]

${BOLD}技能命令:${NC}
  review [scope]       执行代码审查 (scope: backend|frontend|docker|all)
  security [scope]     执行安全扫描 (scope: fast|full)
  docker <action>      Docker运维 (action: status|reload|rollback|logs)
  refactor <phase>     执行重构阶段 (phase: 1|2|3|4|5|status)
  health [service]     健康检查

${BOLD}上下文命令:${NC}
  context [scene]      构建AI交互上下文 (scene: code_review|security|docker_ops|refactor_upgrade)
  rules                加载所有规则
  snapshot             生成项目状态快照

${BOLD}记忆命令:${NC}
  remember <cat> <key> <value>   存储记忆
  recall [category] [key]        查询记忆

${BOLD}管理命令:${NC}
  skills               列出可用技能
  status               查看服务状态
  help                  显示帮助

${BOLD}环境变量:${NC}
  MCP_DEBUG=1          启用调试日志

${BOLD}目录结构:${NC}
  ${AGENT_ROOT}/
  ├── agent/           MCP核心
  ├── skills/          技能模块
  ├── commands/        命令接口
  ├── memory/          记忆存储
  ├── rules/           规则体系
  └── logs/            日志目录
EOF
}

# ---- 主入口 ----

if [[ $# -eq 0 ]]; then
    mcp_show_help
    exit 0
fi

mcp_route "$@"
