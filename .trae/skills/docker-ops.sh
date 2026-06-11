#!/bin/bash
# ============================================================================
# Elink-AI 技能模块：Docker运维
# Docker Operations Skill
# ============================================================================
set -euo pipefail

AGENT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="$(cd "${AGENT_ROOT}/../.." && pwd)"
BACKEND_DIR="${PROJECT_ROOT}/elink-work"
HOT_RELOAD="${BACKEND_DIR}/hot-reload.sh"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[0;33m'; BLUE='\033[0;34m'; BOLD='\033[1m'; NC='\033[0m'

docker_status() {
    echo -e "${BOLD}===== Docker服务状态 =====${NC}"

    local services="redis nacos emqx1 emqx2 auth-service sunmax-gateway system-service device-service
                    data-service protocol-service crontab-service devops-service configure-service
                    together-service webapp-service"

    printf "%-20s %-10s %-10s %-15s %-10s\n" "CONTAINER" "STATUS" "HEALTH" "MEMORY" "UPTIME"
    printf "%-20s %-10s %-10s %-15s %-10s\n" "---------" "------" "------" "------" "------"

    for svc in $services; do
        local container_id=$(docker ps -q -f "name=^${svc}$" 2>/dev/null)
        if [[ -n "$container_id" ]]; then
            local status=$(docker inspect --format='{{.State.Status}}' "$svc" 2>/dev/null)
            local health=$(docker inspect --format='{{.State.Health.Status}}' "$svc" 2>/dev/null || echo "none")
            local mem=$(docker stats --no-stream --format "{{.MemUsage}}" "$svc" 2>/dev/null | awk '{print $1}' || echo "N/A")
            local uptime=$(docker inspect --format='{{.State.StartedAt}}' "$svc" 2>/dev/null | cut -d'.' -f1 || echo "N/A")
            printf "%-20s %-10s %-10s %-15s %-10s\n" "$svc" "$status" "$health" "$mem" "$uptime"
        else
            printf "%-20s %-10s %-10s %-15s %-10s\n" "$svc" "stopped" "-" "-" "-"
        fi
    done
}

docker_reload() {
    local service="$1"
    if [[ -z "$service" ]]; then
        echo -e "${RED}用法: docker-ops.sh reload <service> [skip]${NC}"
        return 1
    fi

    echo -e "${BOLD}===== 热更新: ${service} =====${NC}"
    if [[ -x "$HOT_RELOAD" ]]; then
        "$HOT_RELOAD" reload "$service" "${2:-}"
    else
        echo -e "${RED}hot-reload.sh 不可执行: ${HOT_RELOAD}${NC}"
        return 1
    fi
}

docker_rollback() {
    local service="$1"
    if [[ -z "$service" ]]; then
        echo -e "${RED}用法: docker-ops.sh rollback <service>${NC}"
        return 1
    fi

    echo -e "${BOLD}===== 回滚: ${service} =====${NC}"
    if [[ -x "$HOT_RELOAD" ]]; then
        "$HOT_RELOAD" rollback "$service"
    else
        echo -e "${RED}hot-reload.sh 不可执行: ${HOT_RELOAD}${NC}"
        return 1
    fi
}

docker_logs() {
    local service="$1"
    local lines="${2:-100}"

    if [[ -z "$service" ]]; then
        echo -e "${RED}用法: docker-ops.sh logs <service> [lines]${NC}"
        return 1
    fi

    docker logs --tail "$lines" "$service" 2>&1
}

docker_restart_all() {
    echo -e "${BOLD}===== 按依赖顺序重启所有服务 =====${NC}"
    local order="redis nacos emqx1 emqx2 auth-service sunmax-gateway system-service
                 device-service data-service configure-service protocol-service
                 together-service crontab-service devops-service webapp-service"

    for svc in $order; do
        echo -e "${BLUE}重启 ${svc}...${NC}"
        cd "$BACKEND_DIR"
        docker-compose restart "$svc" 2>/dev/null || true
        sleep 5
    done
    echo -e "${GREEN}所有服务已重启${NC}"
}

case "${1:-status}" in
    status)
        docker_status
        ;;
    reload)
        docker_reload "${2:-}" "${3:-}"
        ;;
    rollback)
        docker_rollback "${2:-}"
        ;;
    logs)
        docker_logs "${2:-}" "${3:-100}"
        ;;
    restart-all)
        docker_restart_all
        ;;
    *)
        echo "Elink-AI Docker运维技能"
        echo ""
        echo "用法: docker-ops.sh <command> [args]"
        echo ""
        echo "命令:"
        echo "  status              查看所有容器状态"
        echo "  reload <svc> [skip] 热更新服务"
        echo "  rollback <svc>      回滚服务"
        echo "  logs <svc> [lines]  查看服务日志"
        echo "  restart-all         按依赖顺序重启所有服务"
        ;;
esac
