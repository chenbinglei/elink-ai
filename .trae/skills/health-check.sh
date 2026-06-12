#!/bin/bash
# ============================================================================
# Elink-AI 技能模块：健康检查
# Health Check Skill
# ============================================================================
set -euo pipefail

AGENT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="$(cd "${AGENT_ROOT}/../.." && pwd)"
BACKEND_DIR="${PROJECT_ROOT}/elink-work"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[0;33m'; BLUE='\033[0;34m'; BOLD='\033[1m'; NC='\033[0m'

# 服务端口映射
declare -A SERVICE_PORTS=(
    [auth-service]=60001
    [sunmax-gateway]=5000
    [system-service]=60002
    [device-service]=60003
    [data-service]=60004
    [protocol-service]=60005
    [crontab-service]=60006
    [devops-service]=60007
    [configure-service]=60008
    [together-service]=60009
    [webapp-service]=60010
)

# 服务Nacos名称映射
declare -A SERVICE_NACOS=(
    [auth-service]=sauth-service
    [sunmax-gateway]=sunmax-gateway
    [system-service]=system-service
    [device-service]=device-service
    [data-service]=sunos-data-service
    [protocol-service]=sunos-protocol-service
    [crontab-service]=scrontab-service
    [devops-service]=devops-service
    [configure-service]=configure-service
    [together-service]=together-service
    [webapp-service]=swebapp-service
)

health_single() {
    local service="$1"
    local port="${SERVICE_PORTS[$service]:-}"
    local nacos_name="${SERVICE_NACOS[$service]:-}"

    echo -e "${BOLD}===== 健康检查: ${service} =====${NC}"

    # 1. Docker容器状态
    echo -e "\n${BLUE}[1] Docker容器状态${NC}"
    local container_id=$(docker ps -q -f "name=^${service}$" 2>/dev/null)
    if [[ -n "$container_id" ]]; then
        local status=$(docker inspect --format='{{.State.Status}}' "$service" 2>/dev/null)
        local health=$(docker inspect --format='{{.State.Health.Status}}' "$service" 2>/dev/null || echo "none")
        local started=$(docker inspect --format='{{.State.StartedAt}}' "$service" 2>/dev/null)
        echo -e "  容器状态: ${GREEN}${status}${NC}"
        echo "  健康状态: ${health}"
        echo "  启动时间: ${started}"
    else
        echo -e "  容器状态: ${RED}未运行${NC}"
        return 1
    fi

    # 2. HTTP端口检查
    if [[ -n "$port" ]]; then
        echo -e "\n${BLUE}[2] HTTP端口检查 (${port})${NC}"
        local http_code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${port}" 2>/dev/null || echo "000")
        if [[ "$http_code" != "000" ]]; then
            echo -e "  HTTP状态: ${GREEN}${http_code}${NC}"
        else
            echo -e "  HTTP状态: ${RED}连接失败${NC}"
        fi
    fi

    # 3. Nacos注册检查
    if [[ -n "$nacos_name" ]]; then
        echo -e "\n${BLUE}[3] Nacos注册检查${NC}"
        local nacos_host=$(grep 'NACOS_HOST' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null | head -1 | grep -oP '\$\{[^}]+\}:-\K[^}]+' || echo "localhost")
        local nacos_registered=$(curl -s "http://${nacos_host}:8848/nacos/v1/ns/instance/list?serviceName=${nacos_name}" 2>/dev/null | grep -c '"healthy":true' || echo "0")
        if [[ "$nacos_registered" -gt 0 ]]; then
            echo -e "  Nacos注册: ${GREEN}已注册 (${nacos_registered}实例)${NC}"
        else
            echo -e "  Nacos注册: ${YELLOW}未注册或无健康实例${NC}"
        fi
    fi

    # 4. 资源使用
    echo -e "\n${BLUE}[4] 资源使用${NC}"
    local mem=$(docker stats --no-stream --format "{{.MemUsage}}" "$service" 2>/dev/null || echo "N/A")
    local cpu=$(docker stats --no-stream --format "{{.CPUPerc}}" "$service" 2>/dev/null || echo "N/A")
    local net=$(docker stats --no-stream --format "{{.NetIO}}" "$service" 2>/dev/null || echo "N/A")
    echo "  内存: ${mem}"
    echo "  CPU:  ${cpu}"
    echo "  网络: ${net}"
}

health_all() {
    echo -e "${BOLD}===== 全服务健康检查 =====${NC}"
    echo ""

    local healthy=0
    local unhealthy=0
    local stopped=0

    printf "%-20s %-10s %-10s %-10s %-15s\n" "SERVICE" "DOCKER" "HEALTH" "HTTP" "MEMORY"
    printf "%-20s %-10s %-10s %-10s %-15s\n" "-------" "------" "------" "----" "------"

    for service in auth-service sunmax-gateway system-service device-service data-service \
                   protocol-service crontab-service devops-service configure-service together-service webapp-service; do
        local container_id=$(docker ps -q -f "name=^${service}$" 2>/dev/null)
        local docker_status="stopped"
        local health_status="-"
        local http_status="-"
        local mem="N/A"

        if [[ -n "$container_id" ]]; then
            docker_status="running"
            health_status=$(docker inspect --format='{{.State.Health.Status}}' "$service" 2>/dev/null || echo "none")
            mem=$(docker stats --no-stream --format "{{.MemUsage}}" "$service" 2>/dev/null | awk '{print $1}' || echo "N/A")

            local port="${SERVICE_PORTS[$service]:-}"
            if [[ -n "$port" ]]; then
                local http_code=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 3 "http://localhost:${port}" 2>/dev/null || echo "000")
                http_status="${http_code}"
            fi

            ((healthy++))
        else
            ((stopped++))
        fi

        # 颜色标记
        local status_color=$GREEN
        [[ "$docker_status" == "stopped" ]] && status_color=$RED
        [[ "$health_status" == "unhealthy" ]] && status_color=$RED
        [[ "$health_status" == "starting" ]] && status_color=$YELLOW

        printf "${status_color}%-20s %-10s %-10s %-10s %-15s${NC}\n" \
            "$service" "$docker_status" "$health_status" "$http_status" "$mem"
    done

    echo ""
    echo -e "运行中: ${GREEN}${healthy}${NC} | 已停止: ${RED}${stopped}${NC}"
}

health_infra() {
    echo -e "${BOLD}===== 基础设施健康检查 =====${NC}"

    # Redis
    echo -e "\n${BLUE}[Redis]${NC}"
    local redis_ping=$(docker exec redis redis-cli -a "${REDIS_PASSWORD:-123456}" ping 2>/dev/null || echo "FAILED")
    echo "  PING: ${redis_ping}"
    local redis_info=$(docker exec redis redis-cli -a "${REDIS_PASSWORD:-123456}" info memory 2>/dev/null | grep 'used_memory_human' | head -1 || echo "N/A")
    echo "  内存: ${redis_info}"

    # Nacos
    echo -e "\n${BLUE}[Nacos]${NC}"
    local nacos_status=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8848/nacos/" 2>/dev/null || echo "000")
    echo "  HTTP状态: ${nacos_status}"

    # EMQX
    echo -e "\n${BLUE}[EMQX]${NC}"
    local emqx1_status=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:18083" 2>/dev/null || echo "000")
    local emqx2_status=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:28083" 2>/dev/null || echo "000")
    echo "  EMQX1(18083): ${emqx1_status}"
    echo "  EMQX2(28083): ${emqx2_status}"

    # 磁盘空间
    echo -e "\n${BLUE}[磁盘空间]${NC}"
    df -h / | tail -1 | awk '{printf "  总量: %s | 已用: %s | 可用: %s | 使用率: %s\n", $2, $3, $4, $5}'
}

case "${1:-all}" in
    all)
        health_infra
        echo ""
        health_all
        ;;
    infra)
        health_infra
        ;;
    single)
        health_single "${2:-auth-service}"
        ;;
    *)
        echo "Elink-AI 健康检查技能"
        echo ""
        echo "用法: health-check.sh <command> [args]"
        echo ""
        echo "命令:"
        echo "  all                 全服务+基础设施检查"
        echo "  infra               基础设施检查(Redis/Nacos/EMQX)"
        echo "  single <service>    单服务深度检查"
        ;;
esac
