#!/bin/bash
# ============================================================================
# Elink-AI 技能模块：安全扫描
# Security Scan Skill
# ============================================================================
set -euo pipefail

AGENT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="$(cd "${AGENT_ROOT}/../.." && pwd)"
BACKEND_DIR="${PROJECT_ROOT}/elink-work"
FRONTEND_DIR="${PROJECT_ROOT}/elink-web"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[0;33m'; BLUE='\033[0;34m'; BOLD='\033[1m'; NC='\033[0m'

scan_fast() {
    echo -e "${BOLD}===== 快速安全扫描 =====${NC}"
    local total_issues=0

    # P0: 硬编码凭证
    echo -e "\n${RED}[P0] 硬编码凭证检测...${NC}"
    local p0_count=0

    # 后端硬编码IP
    local backend_ips=$(grep -rn '47\.110\.235\.112\|192\.168\.2\.158\|1\.95\.55\.247' \
        "${BACKEND_DIR}"/*/src/ 2>/dev/null | grep -v 'target' | grep -v '.class' || true)
    [[ -n "$backend_ips" ]] && { echo "$backend_ips" | head -5; ((p0_count++)); }

    # 前端硬编码IP
    local frontend_ips=$(grep -rn '47\.110\.235\.112\|192\.168\.2\.158\|121\.41\.109\.130' \
        "${FRONTEND_DIR}"/*/src/ 2>/dev/null | grep -v 'node_modules' || true)
    [[ -n "$frontend_ips" ]] && { echo "$frontend_ips" | head -5; ((p0_count++)); }

    # CORS通配符
    local cors_wildcard=$(grep -rn 'allowed-origins: "\*"' "${BACKEND_DIR}"/*/src/ 2>/dev/null || true)
    [[ -n "$cors_wildcard" ]] && { echo "$cors_wildcard"; ((p0_count++)); }

    # Fastjson
    local fastjson=$(grep -rn 'fastjson.*1\.2\.0' "${BACKEND_DIR}/pom.xml" 2>/dev/null || true)
    [[ -n "$fastjson" ]] && { echo "$fastjson"; ((p0_count++)); }

    # 平台密钥
    local platform_secrets=$(grep -rn 'MACQKWDXI\|JbhI7olOTAKs2ZNU\|RVPxJ4aiZwMxnGri' \
        "${BACKEND_DIR}"/*/src/ 2>/dev/null | grep -v 'target' || true)
    [[ -n "$platform_secrets" ]] && { echo "$platform_secrets"; ((p0_count++)); }

    echo -e "  P0问题数: ${p0_count}"
    ((total_issues += p0_count))

    # P1: 配置安全
    echo -e "\n${YELLOW}[P1] 配置安全检测...${NC}"
    local p1_count=0

    # ddl-auto
    local ddl_count=$(grep -rn 'ddl-auto: update' "${BACKEND_DIR}"/*/src/main/resources/ 2>/dev/null | wc -l | tr -d ' 
')
    [[ "$ddl_count" -gt 0 ]] && { echo "  ddl-auto: update 在 ${ddl_count} 个服务中"; ((p1_count++)); }

    # 连接池过大
    local big_pool=$(grep -rn 'maximum-pool-size: 1000' "${BACKEND_DIR}"/*/src/ 2>/dev/null || true)
    [[ -n "$big_pool" ]] && { echo "  $big_pool"; ((p1_count++)); }

    # OAuth2硬编码
    local oauth_hard=$(grep -rn 'client-secret: sunos-client' "${BACKEND_DIR}"/*/src/ 2>/dev/null | wc -l | tr -d ' 
')
    [[ "$oauth_hard" -gt 0 ]] && { echo "  OAuth2 client-secret硬编码: ${oauth_hard}处"; ((p1_count++)); }

    # Redis默认密码
    local redis_default=$(grep -n 'REDIS_PASSWORD:-123456\|requirepass.*123456' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || true)
    [[ -n "$redis_default" ]] && { echo "  Redis默认密码: ${redis_default}"; ((p1_count++)); }

    # EMQX默认密码
    local emqx_default=$(grep -n 'DEFAULT_PASSWORD=public' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || true)
    [[ -n "$emqx_default" ]] && { echo "  EMQX默认密码: ${emqx_default}"; ((p1_count++)); }

    echo -e "  P1问题数: ${p1_count}"
    ((total_issues += p1_count))

    echo -e "\n${BOLD}快速扫描结果: 共 ${total_issues} 个安全问题${NC}"

    # 风险评级
    if [[ "$total_issues" -gt 5 ]]; then
        echo -e "${RED}风险等级: 高危 - 建议立即修复P0问题${NC}"
    elif [[ "$total_issues" -gt 2 ]]; then
        echo -e "${YELLOW}风险等级: 中等 - 建议尽快修复P0/P1问题${NC}"
    else
        echo -e "${GREEN}风险等级: 低 - 安全基线良好${NC}"
    fi
}

scan_full() {
    scan_fast

    echo -e "\n${BOLD}===== 深度安全扫描 =====${NC}"

    echo -e "\n${BLUE}[1] 依赖漏洞检测...${NC}"
    # 检查已知漏洞依赖
    local vuln_deps=0

    local fastjson_v=$(grep -c 'fastjson.*1\.2\.0' "${BACKEND_DIR}/pom.xml" 2>/dev/null || echo "0")
    [[ "$fastjson_v" -gt 0 ]] && { echo -e "  ${RED}Fastjson 1.2.0 - 反序列化RCE (CVE-2022-25845等)${NC}"; ((vuln_deps++)); }

    local swagger_v=$(grep -c 'springfox-swagger2.*2\.9\.2' "${BACKEND_DIR}/pom.xml" 2>/dev/null || echo "0")
    [[ "$swagger_v" -gt 0 ]] && { echo -e "  ${YELLOW}Swagger 2.9.2 - SSRF/XSS风险${NC}"; ((vuln_deps++)); }

    local release_v=$(grep -c 'annotations:RELEASE' "${BACKEND_DIR}/pom.xml" 2>/dev/null || echo "0")
    [[ "$release_v" -gt 0 ]] && { echo -e "  ${YELLOW}org.jetbrains:annotations:RELEASE - 供应链风险${NC}"; ((vuln_deps++)); }

    echo -e "  漏洞依赖数: ${vuln_deps}"

    echo -e "\n${BLUE}[2] 数据库安全检测...${NC}"
    local useSSL=$(grep -c 'useSSL=false' "${BACKEND_DIR}"/*/src/main/resources/application.yml 2>/dev/null || echo "0")
    echo "  useSSL=false 连接数: ${useSSL}"

    echo -e "\n${BLUE}[3] 端口暴露检测...${NC}"
    echo "  Redis 6379: $(grep -c '6379:6379' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || echo "0") 处暴露"
    echo "  Nacos 8848: $(grep -c '8848:8848' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || echo "0") 处暴露"
    echo "  EMQX管理端口: $(grep -c '18083:18083\|28083:28083' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || echo "0") 处暴露"

    echo -e "\n${BLUE}[4] 容器安全检测...${NC}"
    local root_user=$(grep -c 'USER ' "${BACKEND_DIR}/Dockerfile" 2>/dev/null || echo "0")
    [[ "$root_user" -eq 0 ]] && echo -e "  ${YELLOW}容器以root用户运行${NC}" || echo -e "  ${GREEN}已配置非root用户${NC}"

    local resource_limits=$(grep -c 'deploy:' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || echo "0")
    [[ "$resource_limits" -eq 0 ]] && echo -e "  ${YELLOW}未配置容器资源限制${NC}"

    echo -e "\n${BLUE}[5] .env文件泄露检测...${NC}"
    local env_in_git=$(grep -c '\.env$' "${PROJECT_ROOT}/.gitignore" 2>/dev/null || echo "0")
    [[ "$env_in_git" -gt 0 ]] && echo -e "  ${GREEN}.env已加入.gitignore${NC}" || echo -e "  ${RED}.env未加入.gitignore!${NC}"

    if [[ -f "${PROJECT_ROOT}/.env" ]]; then
        local env_perms=$(stat -c '%a' "${PROJECT_ROOT}/.env" 2>/dev/null || echo "644")
        echo "  .env权限: ${env_perms} (建议600)"
    fi
}

case "${1:-fast}" in
    fast)
        scan_fast
        ;;
    full)
        scan_full
        ;;
    *)
        echo "用法: security-scan.sh [fast|full]"
        ;;
esac
