#!/bin/bash
# ============================================================================
# Elink-AI 智能体记忆管理模块
# Memory Management Module
# ============================================================================
# 功能：短期记忆、长期记忆、决策记录、进度追踪、上下文缓存
# 存储格式：JSONL（每行一条记录，便于追加和查询）
# ============================================================================

set -euo pipefail

AGENT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="$(cd "${AGENT_ROOT}/../.." && pwd)"
MEMORY_DIR="${AGENT_ROOT}/memory"

# 记忆文件
MEMORY_INDEX="${MEMORY_DIR}/index.jsonl"
MEMORY_DECISIONS="${MEMORY_DIR}/decisions.jsonl"
MEMORY_PROGRESS="${MEMORY_DIR}/progress.jsonl"
MEMORY_ISSUES="${MEMORY_DIR}/issues.jsonl"
MEMORY_CONTEXT="${MEMORY_DIR}/context-cache.jsonl"
MEMORY_SESSION="${MEMORY_DIR}/session-$(date '+%Y%m%d').jsonl"

# 颜色
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# ---- 初始化 ----
memory_init() {
    mkdir -p "$MEMORY_DIR"
    for f in "$MEMORY_INDEX" "$MEMORY_DECISIONS" "$MEMORY_PROGRESS" "$MEMORY_ISSUES" "$MEMORY_CONTEXT" "$MEMORY_SESSION"; do
        [[ -f "$f" ]] || echo '{"version":"1.0","created":"'$(date -Iseconds)'"}' > "$f"
    done
}

# ---- 核心操作 ----

# 存储记忆
memory_store() {
    local file="$1"
    local category="$2"
    local key="$3"
    local value="$4"
    local priority="${5:-P2}"

    local entry=$(jq -n \
        --arg ts "$(date -Iseconds)" \
        --arg cat "$category" \
        --arg key "$key" \
        --arg val "$value" \
        --arg pri "$priority" \
        '{ts: $ts, category: $cat, key: $key, value: $val, priority: $pri}')

    echo "$entry" >> "$file"
}

# 查询记忆
memory_query() {
    local file="$1"
    local filter="${2:-}"
    local limit="${3:-20}"

    [[ -f "$file" ]] || return 0

    if [[ -n "$filter" ]]; then
        grep "$filter" "$file" | tail -"$limit"
    else
        tail -"$limit" "$file"
    fi
}

# 清理过期记忆（保留最近N天）
memory_gc() {
    local days="${1:-30}"
    local cutoff=$(date -d "-${days} days" '+%Y-%m-%dT%H:%M:%S' 2>/dev/null || date -v-${days}d '+%Y-%m-%dT%H:%M:%S')

    for f in "$MEMORY_DIR"/*.jsonl; do
        [[ -f "$f" ]] || continue
        local tmp_file="${f}.tmp"
        # 保留元数据行和最近记录
        head -1 "$f" > "$tmp_file"
        tail -n +2 "$f" | while IFS= read -r line; do
            local ts=$(echo "$line" | grep -oP '"ts":"\K[^"]+' | head -1)
            if [[ -n "$ts" && "$ts" > "$cutoff" ]]; then
                echo "$line" >> "$tmp_file"
            fi
        done
        mv "$tmp_file" "$f"
    done

    echo -e "${GREEN}[记忆GC] 已清理 ${days} 天前的过期记录${NC}"
}

# ---- 决策记录 ----

decision_record() {
    local decision="$1"
    local reason="$2"
    local impact="${3:-}"

    memory_store "$MEMORY_DECISIONS" "decision" "$decision" "reason=${reason};impact=${impact}" "P1"
    echo -e "${GREEN}[决策] 已记录: ${decision}${NC}"
}

decision_list() {
    echo -e "${BOLD}===== 决策记录 =====${NC}"
    memory_query "$MEMORY_DECISIONS" "decision" 50 | tail -n +2 | while IFS= read -r line; do
        local ts=$(echo "$line" | grep -oP '"ts":"\K[^"]+' | head -1)
        local val=$(echo "$line" | grep -oP '"value":"\K[^"]*' | head -1)
        echo "  [${ts}] ${val}"
    done
}

# ---- 进度追踪 ----

progress_update() {
    local phase="$1"
    local task="$2"
    local status="$3"  # pending|in_progress|completed|blocked
    local detail="${4:-}"

    memory_store "$MEMORY_PROGRESS" "progress" "${phase}/${task}" "status=${status};detail=${detail}" "P1"
    echo -e "${GREEN}[进度] ${phase}/${task}: ${status}${NC}"
}

progress_report() {
    echo -e "${BOLD}===== 重构进度报告 =====${NC}"
    printf "%-12s %-30s %-12s %s\n" "PHASE" "TASK" "STATUS" "DETAIL"
    printf "%-12s %-30s %-12s %s\n" "-----" "----" "------" "------"

    memory_query "$MEMORY_PROGRESS" "progress" 100 | tail -n +2 | while IFS= read -r line; do
        local key=$(echo "$line" | grep -oP '"key":"\K[^"]+' | head -1)
        local val=$(echo "$line" | grep -oP '"value":"\K[^"]*' | head -1)
        local phase=$(echo "$key" | cut -d'/' -f1)
        local task=$(echo "$key" | cut -d'/' -f2)
        local status=$(echo "$val" | grep -oP 'status=\K[^;]*' | head -1)
        local detail=$(echo "$val" | grep -oP 'detail=\K[^;]*' | head -1)
        printf "%-12s %-30s %-12s %s\n" "$phase" "$task" "$status" "$detail"
    done
}

# ---- 问题追踪 ----

issue_register() {
    local id="$1"
    local severity="$2"  # P0|P1|P2
    local description="$3"
    local location="$4"
    local fix="${5:-}"

    memory_store "$MEMORY_ISSUES" "issue" "$id" "severity=${severity};desc=${description};loc=${location};fix=${fix}" "$severity"
    echo -e "${GREEN}[问题] 已注册: [${severity}] ${id}: ${description}${NC}"
}

issue_list() {
    local severity_filter="${1:-}"

    echo -e "${BOLD}===== 问题清单 =====${NC}"
    printf "%-8s %-6s %-40s %-30s\n" "ID" "级别" "描述" "位置"
    printf "%-8s %-6s %-40s %-30s\n" "--" "----" "----" "----"

    local filter="issue"
    [[ -n "$severity_filter" ]] && filter="${severity_filter}"

    memory_query "$MEMORY_ISSUES" "$filter" 100 | tail -n +2 | while IFS= read -r line; do
        local key=$(echo "$line" | grep -oP '"key":"\K[^"]+' | head -1)
        local val=$(echo "$line" | grep -oP '"value":"\K[^"]*' | head -1)
        local sev=$(echo "$val" | grep -oP 'severity=\K[^;]*' | head -1)
        local desc=$(echo "$val" | grep -oP 'desc=\K[^;]*' | head -1)
        local loc=$(echo "$val" | grep -oP 'loc=\K[^;]*' | head -1)

        [[ -n "$severity_filter" && "$sev" != "$severity_filter" ]] && continue

        printf "%-8s %-6s %-40s %-30s\n" "$key" "$sev" "$desc" "$loc"
    done
}

# ---- 上下文缓存 ----

context_cache() {
    local key="$1"
    local value="$2"

    memory_store "$MEMORY_CONTEXT" "cache" "$key" "$value" "P2"
}

context_get() {
    local key="$1"
    memory_query "$MEMORY_CONTEXT" "\"key\":\"${key}\"" 1 | tail -1
}

# ---- 会话记忆 ----

session_log() {
    local action="$1"
    local detail="$2"

    memory_store "$MEMORY_SESSION" "session" "$action" "$detail" "P2"
}

session_summary() {
    echo -e "${BOLD}===== 今日会话摘要 =====${NC}"
    memory_query "$MEMORY_SESSION" "" 50 | tail -n +2 | while IFS= read -r line; do
        local ts=$(echo "$line" | grep -oP '"ts":"\K[^"]+' | head -1)
        local key=$(echo "$line" | grep -oP '"key":"\K[^"]+' | head -1)
        local val=$(echo "$line" | grep -oP '"value":"\K[^"]*' | head -1)
        echo "  [${ts}] ${key}: ${val}"
    done
}

# ---- 导出/导入 ----

memory_export() {
    local output="${1:-${MEMORY_DIR}/export-$(date '+%Y%m%d').json}"

    echo "{" > "$output"
    echo "  \"export_time\": \"$(date -Iseconds)\"," >> "$output"
    echo "  \"decisions\": [" >> "$output"
    tail -n +2 "$MEMORY_DECISIONS" | head -20 >> "$output"
    echo "  ]," >> "$output"
    echo "  \"progress\": [" >> "$output"
    tail -n +2 "$MEMORY_PROGRESS" | head -50 >> "$output"
    echo "  ]," >> "$output"
    echo "  \"issues\": [" >> "$output"
    tail -n +2 "$MEMORY_ISSUES" | head -50 >> "$output"
    echo "  ]" >> "$output"
    echo "}" >> "$output"

    echo -e "${GREEN}[导出] 记忆已导出至: ${output}${NC}"
}

# ---- 主入口 ----

memory_init

case "${1:-help}" in
    store)
        memory_store "$MEMORY_INDEX" "${2:-context}" "${3:-key}" "${4:-value}" "${5:-P2}"
        ;;
    query)
        memory_query "$MEMORY_INDEX" "${2:-}" "${3:-20}"
        ;;
    decision)
        decision_record "$2" "$3" "${4:-}"
        ;;
    decisions)
        decision_list
        ;;
    progress)
        progress_update "$2" "$3" "$4" "${5:-}"
        ;;
    report)
        progress_report
        ;;
    issue)
        issue_register "$2" "$3" "$4" "$5" "${6:-}"
        ;;
    issues)
        issue_list "${2:-}"
        ;;
    cache)
        context_cache "$2" "$3"
        ;;
    get)
        context_get "$2"
        ;;
    session)
        session_log "$2" "$3"
        ;;
    summary)
        session_summary
        ;;
    gc)
        memory_gc "${2:-30}"
        ;;
    export)
        memory_export "${2:-}"
        ;;
    help|*)
        echo "Elink-AI 记忆管理模块 v1.0"
        echo ""
        echo "用法: memory.sh <command> [args]"
        echo ""
        echo "核心操作:"
        echo "  store <category> <key> <value> [priority]   存储记忆"
        echo "  query [filter] [limit]                       查询记忆"
        echo ""
        echo "决策管理:"
        echo "  decision <decision> <reason> [impact]        记录决策"
        echo "  decisions                                     列出决策"
        echo ""
        echo "进度追踪:"
        echo "  progress <phase> <task> <status> [detail]    更新进度"
        echo "  report                                        进度报告"
        echo ""
        echo "问题追踪:"
        echo "  issue <id> <severity> <desc> <loc> [fix]     注册问题"
        echo "  issues [severity]                             列出问题"
        echo ""
        echo "上下文缓存:"
        echo "  cache <key> <value>                          缓存上下文"
        echo "  get <key>                                    获取缓存"
        echo ""
        echo "会话管理:"
        echo "  session <action> <detail>                    记录会话"
        echo "  summary                                      会话摘要"
        echo ""
        echo "维护:"
        echo "  gc [days]                                    清理过期记忆"
        echo "  export [file]                                导出记忆"
        ;;
esac
