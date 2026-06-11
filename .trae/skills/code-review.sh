#!/bin/bash
# ============================================================================
# Elink-AI 技能模块：代码审查
# Code Review Skill
# ============================================================================
set -euo pipefail

AGENT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="$(cd "${AGENT_ROOT}/../.." && pwd)"
BACKEND_DIR="${PROJECT_ROOT}/elink-work"
FRONTEND_DIR="${PROJECT_ROOT}/elink-web"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[0;33m'; BLUE='\033[0;34m'; BOLD='\033[1m'; NC='\033[0m'

# ---- 后端审查 ----
review_backend() {
    echo -e "${BOLD}===== 后端代码审查 =====${NC}"
    local issues=0

    echo -e "\n${BLUE}[1] 检查硬编码IP/密钥...${NC}"
    local hardcoded=$(grep -rn '47\.110\.235\.112\|192\.168\.2\.158\|1\.95\.55\.247' \
        "${BACKEND_DIR}"/*/src/main/resources/application.yml 2>/dev/null || true)
    if [[ -n "$hardcoded" ]]; then
        echo -e "${RED}  [P0] 发现硬编码IP:${NC}"
        echo "$hardcoded" | while read -r line; do echo "    $line"; done
        ((issues++))
    else
        echo -e "${GREEN}  通过 - 无硬编码IP${NC}"
    fi

    echo -e "\n${BLUE}[2] 检查JPA ddl-auto配置...${NC}"
    local ddl_auto=$(grep -rn 'ddl-auto: update' \
        "${BACKEND_DIR}"/*/src/main/resources/application.yml 2>/dev/null || true)
    if [[ -n "$ddl_auto" ]]; then
        echo -e "${RED}  [P1] 生产环境ddl-auto应为validate:${NC}"
        echo "$ddl_auto" | while read -r line; do echo "    $line"; done
        ((issues++))
    else
        echo -e "${GREEN}  通过 - ddl-auto配置正确${NC}"
    fi

    echo -e "\n${BLUE}[3] 检查HikariCP连接池大小...${NC}"
    local pool_size=$(grep -rn 'maximum-pool-size:' \
        "${BACKEND_DIR}"/*/src/main/resources/application.yml 2>/dev/null || true)
    while IFS= read -r line; do
        local size=$(echo "$line" | grep -oP '\d+' | tail -1)
        if [[ -n "$size" && "$size" -gt 50 ]]; then
            echo -e "${RED}  [P1] 连接池过大: $line${NC}"
            ((issues++))
        fi
    done <<< "$pool_size"

    echo -e "\n${BLUE}[4] 检查OAuth2 client-secret硬编码...${NC}"
    local oauth_secret=$(grep -rn 'client-secret: sunos-client' \
        "${BACKEND_DIR}"/*/src/main/resources/application.yml 2>/dev/null || true)
    if [[ -n "$oauth_secret" ]]; then
        local count=$(echo "$oauth_secret" | wc -l)
        echo -e "${YELLOW}  [P1] ${count}个服务硬编码OAuth2 client-secret${NC}"
        ((issues++))
    fi

    echo -e "\n${BLUE}[5] 检查javax.*命名空间使用...${NC}"
    local javax_count=$(grep -rn 'import javax\.' "${BACKEND_DIR}"/*/src/main/java/ 2>/dev/null | wc -l | tr -d ' \n')
    if [[ "$javax_count" -gt 0 ]]; then
        echo -e "${YELLOW}  [P2] 发现 ${javax_count} 处javax.* import（需迁移至jakarta.*）${NC}"
        ((issues++))
    fi

    echo -e "\n${BLUE}[6] 检查Fastjson使用...${NC}"
    local fastjson_count=$(grep -rn 'import com\.alibaba\.fastjson\.' "${BACKEND_DIR}"/*/src/main/java/ 2>/dev/null | wc -l | tr -d ' \n')
    if [[ "$fastjson_count" -gt 0 ]]; then
        echo -e "${RED}  [P0] 发现 ${fastjson_count} 处Fastjson import（存在反序列化漏洞）${NC}"
        ((issues++))
    fi

    echo -e "\n${BLUE}[7] 检查Swagger 2注解...${NC}"
    local swagger_count=$(grep -rn '@Api\b\|@ApiOperation\|@ApiParam' "${BACKEND_DIR}"/*/src/main/java/ 2>/dev/null | wc -l | tr -d ' \n')
    if [[ "$swagger_count" -gt 0 ]]; then
        echo -e "${YELLOW}  [P2] 发现 ${swagger_count} 处Swagger 2注解（需迁移至SpringDoc）${NC}"
        ((issues++))
    fi

    echo -e "\n${BLUE}[8] 检查宽泛异常捕获...${NC}"
    local catch_count=$(grep -rn 'catch(Exception' "${BACKEND_DIR}"/*/src/main/java/ 2>/dev/null | wc -l | tr -d ' \n')
    if [[ "$catch_count" -gt 0 ]]; then
        echo -e "${YELLOW}  [P2] 发现 ${catch_count} 处catch(Exception)${NC}"
    fi

    echo -e "\n${BOLD}后端审查结果: ${issues} 个问题${NC}"
}

# ---- 前端审查 ----
review_frontend() {
    echo -e "${BOLD}===== 前端代码审查 =====${NC}"
    local issues=0

    echo -e "\n${BLUE}[1] 检查硬编码IP地址...${NC}"
    local hardcoded=$(grep -rn '47\.110\.235\.112\|192\.168\.2\.158\|121\.41\.109\.130' \
        "${FRONTEND_DIR}"/*/src/ 2>/dev/null | grep -v 'node_modules' | grep -v '.git' || true)
    if [[ -n "$hardcoded" ]]; then
        local count=$(echo "$hardcoded" | wc -l)
        echo -e "${RED}  [P0] 发现 ${count} 处硬编码IP:${NC}"
        echo "$hardcoded" | head -10 | while read -r line; do echo "    $line"; done
        ((issues++))
    fi

    echo -e "\n${BLUE}[2] 检查Vuex使用（应迁移至Pinia）...${NC}"
    local vuex_count=$(grep -rn 'from.*vuex\|useStore\|Vuex' \
        "${FRONTEND_DIR}"/*/src/ 2>/dev/null | grep -v 'node_modules' | wc -l | tr -d ' \n')
    if [[ "$vuex_count" -gt 0 ]]; then
        echo -e "${YELLOW}  [P2] 发现 ${vuex_count} 处Vuex使用${NC}"
        ((issues++))
    fi

    echo -e "\n${BLUE}[3] 检查Vue CLI项目（应迁移至Vite）...${NC}"
    for proj in linkos tycvs; do
        if [[ -f "${FRONTEND_DIR}/${proj}/vue.config.js" ]]; then
            echo -e "${YELLOW}  [P2] ${proj} 仍使用Vue CLI（需迁移至Vite）${NC}"
            ((issues++))
        fi
    done

    echo -e "\n${BLUE}[4] 检查生产sourceMap配置...${NC}"
    # linkos
    local linkos_sourcemap=$(grep 'productionSourceMap' "${FRONTEND_DIR}/linkos/vue.config.js" 2>/dev/null | grep -c 'false' || echo "0")
    # tycvs
    local tycvs_sourcemap=$(grep 'productionSourceMap' "${FRONTEND_DIR}/tycvs/vue.config.js" 2>/dev/null | grep -c 'false' || echo "0")
    # derms
    local derms_sourcemap=$(grep 'sourcemap' "${FRONTEND_DIR}/derms/vite.config.js" 2>/dev/null | grep -c 'false' || echo "0")

    [[ "$linkos_sourcemap" -eq 1 ]] && echo -e "${GREEN}  linkos: sourceMap已关闭${NC}" || echo -e "${YELLOW}  linkos: sourceMap未关闭${NC}"
    [[ "$tycvs_sourcemap" -eq 1 ]] && echo -e "${GREEN}  tycvs: sourceMap已关闭${NC}" || echo -e "${YELLOW}  tycvs: sourceMap未关闭${NC}"
    [[ "$derms_sourcemap" -eq 1 ]] && echo -e "${GREEN}  derms: sourceMap已关闭${NC}" || echo -e "${YELLOW}  derms: sourceMap未关闭${NC}"

    echo -e "\n${BLUE}[5] 检查已知漏洞依赖...${NC}"
    local xlsx_vuln=$(grep -rn '"xlsx":' "${FRONTEND_DIR}"/*/package.json 2>/dev/null | grep '0.18.5' || true)
    if [[ -n "$xlsx_vuln" ]]; then
        echo -e "${YELLOW}  [P2] xlsx 0.18.5 存在已知漏洞${NC}"
        ((issues++))
    fi

    echo -e "\n${BOLD}前端审查结果: ${issues} 个问题${NC}"
}

# ---- Docker审查 ----
review_docker() {
    echo -e "${BOLD}===== Docker配置审查 =====${NC}"
    local issues=0

    echo -e "\n${BLUE}[1] 检查容器资源限制...${NC}"
    local resource_limits=$(grep -c 'deploy:' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || echo "0")
    if [[ "$resource_limits" -eq 0 ]]; then
        echo -e "${RED}  [P1] 所有服务均未设置资源限制${NC}"
        ((issues++))
    fi

    echo -e "\n${BLUE}[2] 检查JAR文件名...${NC}"
    local scrontab=$(grep 'scrontab-service-exec.jar' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || true)
    if [[ -n "$scrontab" ]]; then
        echo -e "${RED}  [P0] crontab-service JAR名拼写错误: scrontab-service-exec.jar${NC}"
        ((issues++))
    fi

    echo -e "\n${BLUE}[3] 检查健康检查配置...${NC}"
    local healthcheck_interval=$(grep 'interval: 5s' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null | wc -l | tr -d ' 
')
    if [[ "$healthcheck_interval" -gt 0 ]]; then
        echo -e "${YELLOW}  [P2] ${healthcheck_interval}个服务健康检查间隔过短(5s)${NC}"
    fi

    echo -e "\n${BLUE}[4] 检查基础镜像版本锁定...${NC}"
    local latest_tag=$(grep 'elink-base:latest' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null | wc -l | tr -d ' 
')
    if [[ "$latest_tag" -gt 0 ]]; then
        echo -e "${YELLOW}  [P2] ${latest_tag}个服务使用latest标签${NC}"
    fi

    echo -e "\n${BOLD}Docker审查结果: ${issues} 个问题${NC}"
}

# ---- 主入口 ----
case "${1:-all}" in
    backend)
        review_backend
        ;;
    frontend)
        review_frontend
        ;;
    docker)
        review_docker
        ;;
    all)
        review_backend
        echo ""
        review_frontend
        echo ""
        review_docker
        ;;
    *)
        echo "用法: code-review.sh [backend|frontend|docker|all]"
        ;;
esac
