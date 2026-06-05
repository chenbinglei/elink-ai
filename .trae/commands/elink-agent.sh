#!/bin/bash
# ============================================================================
# Elink-AI 智能体命令接口
# Agent CLI Entry Point
# ============================================================================
# 用法: elink-agent.sh <command> [options]
# 这是用户与智能体系统交互的唯一入口
# ============================================================================

set -euo pipefail

AGENT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MCP_CORE="${AGENT_ROOT}/agent/mcp-core.sh"

# 颜色
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[0;33m'; BLUE='\033[0;34m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

# 版本
AGENT_VERSION="1.0.0"

# ---- Banner ----
show_banner() {
    cat <<EOF
${CYAN}
  ╔══════════════════════════════════════════╗
  ║     Elink-AI 智能体 v${AGENT_VERSION}            ║
  ║     IoT Platform Agent System           ║
  ╚══════════════════════════════════════════╝
${NC}
EOF
}

# ---- 前置检查 ----
preflight_check() {
    local errors=0

    # 检查MCP核心
    if [[ ! -x "$MCP_CORE" ]]; then
        echo -e "${RED}[错误] MCP核心不可执行: ${MCP_CORE}${NC}"
        ((errors++))
    fi

    # 检查Docker
    if ! command -v docker &>/dev/null; then
        echo -e "${YELLOW}[警告] Docker未安装，部分功能不可用${NC}"
    fi

    # 检查jq
    if ! command -v jq &>/dev/null; then
        echo -e "${YELLOW}[警告] jq未安装，记忆管理功能受限${NC}"
    fi

    return $errors
}

# ---- 命令路由 ----
route_command() {
    local cmd="$1"
    shift || true

    case "$cmd" in
        # === 技能命令 ===
        review|audit)
            # 代码审查
            "$MCP_CORE" review "$@"
            ;;
        security|sec)
            # 安全扫描
            "$MCP_CORE" security "$@"
            ;;
        docker|deploy)
            # Docker运维
            "$MCP_CORE" docker "$@"
            ;;
        refactor|upgrade)
            # 重构升级
            "$MCP_CORE" refactor "$@"
            ;;
        health|check)
            # 健康检查
            "$MCP_CORE" health "$@"
            ;;

        # === 上下文命令 ===
        context)
            # 构建AI上下文
            "$MCP_CORE" context "$@"
            ;;
        rules)
            # 查看规则
            "$MCP_CORE" rules
            ;;
        snapshot)
            # 生成快照
            "$MCP_CORE" snapshot
            ;;

        # === 记忆命令 ===
        remember)
            # 存储记忆
            "$MCP_CORE" remember "$@"
            ;;
        recall)
            # 查询记忆
            "$MCP_CORE" recall "$@"
            ;;

        # === 管理命令 ===
        skills)
            # 列出技能
            "$MCP_CORE" skills
            ;;
        status)
            # 服务状态
            "$MCP_CORE" status
            ;;
        init)
            # 初始化智能体
            init_agent
            ;;
        version|-v|--version)
            echo "Elink-AI Agent v${AGENT_VERSION}"
            ;;
        help|--help|-h)
            show_help
            ;;

        # === 快捷组合命令 ===
        check-all)
            # 全面检查：代码审查 + 安全扫描 + 健康检查
            echo -e "${BOLD}执行全面检查...${NC}"
            "$MCP_CORE" review all
            echo ""
            "$MCP_CORE" security fast
            echo ""
            "$MCP_CORE" health all
            ;;
        security-audit)
            # 安全审计：安全扫描 + 代码审查
            "$MCP_CORE" security full
            echo ""
            "$MCP_CORE" review all
            ;;
        deploy-check)
            # 部署前检查：Docker审查 + 健康检查
            "$MCP_CORE" review docker
            echo ""
            "$MCP_CORE" health all
            ;;

        *)
            echo -e "${RED}未知命令: ${cmd}${NC}"
            show_help
            return 1
            ;;
    esac
}

# ---- 初始化 ----
init_agent() {
    echo -e "${BOLD}初始化 Elink-AI 智能体...${NC}"

    # 设置执行权限
    chmod +x "${AGENT_ROOT}/agent/"*.sh 2>/dev/null || true
    chmod +x "${AGENT_ROOT}/skills/"*.sh 2>/dev/null || true
    chmod +x "${AGENT_ROOT}/commands/"*.sh 2>/dev/null || true

    # 初始化记忆
    mkdir -p "${AGENT_ROOT}/memory"
    mkdir -p "${AGENT_ROOT}/logs"

    # 初始化记忆索引
    source "${AGENT_ROOT}/agent/memory.sh" store init "agent_initialized" "v${AGENT_VERSION}" "P1"

    echo -e "${GREEN}初始化完成！${NC}"
    echo ""
    echo "快速开始:"
    echo "  elink-agent.sh check-all      # 全面检查"
    echo "  elink-agent.sh review backend # 后端代码审查"
    echo "  elink-agent.sh security fast  # 快速安全扫描"
    echo "  elink-agent.sh health all     # 全服务健康检查"
    echo "  elink-agent.sh refactor status# 重构状态"
}

# ---- 帮助 ----
show_help() {
    show_banner
    cat <<EOF
${BOLD}用法:${NC}
  elink-agent.sh <command> [options]

${BOLD}技能命令:${NC}
  ${CYAN}review${NC} [backend|frontend|docker|all]   代码审查
  ${CYAN}security${NC} [fast|full]                   安全扫描
  ${CYAN}docker${NC} <status|reload|rollback|logs>   Docker运维
  ${CYAN}refactor${NC} <status|phase1|phase2>        重构升级
  ${CYAN}health${NC} [all|infra|single <svc>]        健康检查

${BOLD}快捷命令:${NC}
  ${CYAN}check-all${NC}        全面检查（代码审查+安全扫描+健康检查）
  ${CYAN}security-audit${NC}   安全审计（深度安全扫描+代码审查）
  ${CYAN}deploy-check${NC}     部署前检查（Docker审查+健康检查）

${BOLD}上下文命令:${NC}
  ${CYAN}context${NC} [scene]   构建AI交互上下文
  ${CYAN}rules${NC}             查看规则体系
  ${CYAN}snapshot${NC}          生成项目快照

${BOLD}记忆命令:${NC}
  ${CYAN}remember${NC} <cat> <key> <value>   存储记忆
  ${CYAN}recall${NC} [category] [key]        查询记忆

${BOLD}管理命令:${NC}
  ${CYAN}skills${NC}            列出可用技能
  ${CYAN}status${NC}            查看服务状态
  ${CYAN}init${NC}              初始化智能体
  ${CYAN}version${NC}           查看版本

${BOLD}场景说明:${NC}
  scene: code_review | security | docker_ops | refactor_upgrade | general

${BOLD}目录结构:${NC}
  .trae/
  ├── agent/              MCP核心与记忆管理
  │   ├── mcp-core.sh     核心编排器
  │   └── memory.sh       记忆管理模块
  ├── skills/             技能模块
  │   ├── code-review.sh  代码审查
  │   ├── security-scan.sh安全扫描
  │   ├── docker-ops.sh   Docker运维
  │   ├── refactor.sh     重构升级
  │   └── health-check.sh 健康检查
  ├── commands/           扩展命令
  ├── memory/             记忆存储
  ├── rules/              规则体系
  └── logs/               日志目录
EOF
}

# ---- 主入口 ----

if [[ $# -eq 0 ]]; then
    show_help
    exit 0
fi

preflight_check || true
route_command "$@"
