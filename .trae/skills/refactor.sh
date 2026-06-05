#!/bin/bash
# ============================================================================
# Elink-AI 技能模块：重构升级
# Refactor & Upgrade Skill
# ============================================================================
set -euo pipefail

AGENT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="$(cd "${AGENT_ROOT}/../.." && pwd)"
BACKEND_DIR="${PROJECT_ROOT}/elink-work"
FRONTEND_DIR="${PROJECT_ROOT}/elink-web"
MEMORY_DIR="${AGENT_ROOT}/memory"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[0;33m'; BLUE='\033[0;34m'; BOLD='\033[1m'; NC='\033[0m'

refactor_status() {
    echo -e "${BOLD}===== 重构升级状态 =====${NC}"

    echo -e "\n${BLUE}[PHASE-1] 安全加固${NC}"
    local p1_items=0

    # Fastjson
    local fastjson_remaining=$(grep -rn 'import com\.alibaba\.fastjson\.' "${BACKEND_DIR}"/*/src/main/java/ 2>/dev/null | wc -l | tr -d ' 
')
    echo "  Fastjson替换: ${fastjson_remaining} 处剩余 (目标: 0)"
    [[ "$fastjson_remaining" -gt 0 ]] && ((p1_items++))

    # CORS
    local cors_wildcard=$(grep -c 'allowed-origins: "\*"' "${BACKEND_DIR}"/*/src/main/resources/application.yml 2>/dev/null || echo "0")
    echo "  CORS通配符: ${cors_wildcard} 处 (目标: 0)"
    [[ "$cors_wildcard" -gt 0 ]] && ((p1_items++))

    # ddl-auto
    local ddl_update=$(grep -c 'ddl-auto: update' "${BACKEND_DIR}"/*/src/main/resources/application.yml 2>/dev/null || echo "0")
    echo "  ddl-auto: update: ${ddl_update} 处 (目标: 0)"
    [[ "$ddl_update" -gt 0 ]] && ((p1_items++))

    # 硬编码IP
    local hardcoded_ips=$(grep -rn '47\.110\.235\.112\|192\.168\.2\.158' "${BACKEND_DIR}"/*/src/ "${FRONTEND_DIR}"/*/src/ 2>/dev/null | grep -v 'node_modules\|target' | wc -l | tr -d ' 
')
    echo "  硬编码IP: ${hardcoded_ips} 处 (目标: 0)"
    [[ "$hardcoded_ips" -gt 0 ]] && ((p1_items++))

    echo -e "  PHASE-1 待处理: ${p1_items} 项"

    echo -e "\n${BLUE}[PHASE-2] 框架升级${NC}"
    local current_boot=$(grep 'spring-boot-starter-parent' "${BACKEND_DIR}/pom.xml" 2>/dev/null | grep -oP '\d+\.\d+\.\d+' | head -1 || echo "unknown")
    echo "  Spring Boot: ${current_boot} → 2.7.x → 3.3.x"
    echo "  Java: 8 → 17"
    echo "  javax→jakarta: $(grep -rn 'import javax\.' "${BACKEND_DIR}"/*/src/main/java/ 2>/dev/null | wc -l | tr -d ' 
') 处待迁移"

    echo -e "\n${BLUE}[PHASE-3] 代码质量${NC}"
    local swagger_remaining=$(grep -rn '@Api\b\|@ApiOperation\|@ApiParam' "${BACKEND_DIR}"/*/src/main/java/ 2>/dev/null | wc -l | tr -d ' 
')
    echo "  Swagger 2注解: ${swagger_remaining} 处待迁移"
    local catch_exception=$(grep -rn 'catch(Exception' "${BACKEND_DIR}"/*/src/main/java/ 2>/dev/null | wc -l | tr -d ' 
')
    echo "  宽泛异常捕获: ${catch_exception} 处"

    echo -e "\n${BLUE}[PHASE-4] 前端现代化${NC}"
    echo "  linkos: Vue CLI → Vite, Vuex → Pinia"
    echo "  tycvs: Vue CLI → Vite, Vuex → Pinia"
    echo "  derms: Vite ✓, Vuex → Pinia"

    echo -e "\n${BLUE}[PHASE-5] 构建部署优化${NC}"
    local resource_limits=$(grep -c 'deploy:' "${BACKEND_DIR}/docker-compose.yml" 2>/dev/null || echo "0")
    echo "  容器资源限制: ${resource_limits} 处 (目标: 全部11个服务)"
}

refactor_phase1() {
    echo -e "${BOLD}===== 执行 PHASE-1: 安全加固 =====${NC}"
    local step="${1:-all}"

    case "$step" in
        fastjson|1)
            echo -e "${BLUE}[Step 1] Fastjson替换为fastjson2${NC}"
            echo "  影响范围: pom.xml + 150处引用/100个Java文件"
            echo "  执行步骤:"
            echo "    1. 修改父POM: fastjson 1.2.0 → fastjson2 2.0.52"
            echo "    2. 全局替换import: com.alibaba.fastjson → com.alibaba.fastjson2"
            echo "    3. API适配: JSON.parseObject/JSONObject保持兼容"
            echo "    4. 验证编译: mvn clean compile -DskipTests"
            echo ""
            echo -e "${YELLOW}  建议先在单个服务(如system-service)验证，再全量替换${NC}"
            ;;
        cors|2)
            echo -e "${BLUE}[Step 2] CORS配置收紧${NC}"
            echo "  影响范围: sunmax-gateway/src/main/resources/application.yml"
            echo "  修复: allowed-origins: \"*\" → 具体域名列表"
            ;;
        ddl|3)
            echo -e "${BLUE}[Step 3] JPA ddl-auto修改${NC}"
            echo "  影响范围: 全部10个业务服务application.yml"
            echo "  修复: ddl-auto: update → ddl-auto: validate"
            echo "  前提: 确保数据库schema已通过Flyway/Liquibase管理"
            ;;
        ip|4)
            echo -e "${BLUE}[Step 4] 硬编码IP清除${NC}"
            echo "  后端: application.yml中硬编码IP → 环境变量"
            echo "  前端: request.js/config.js/webSocket.js → 环境变量"
            ;;
        secrets|5)
            echo -e "${BLUE}[Step 5] 凭证外置${NC}"
            echo "  OAuth2 client-secret → 环境变量"
            echo "  平台密钥(MACQKWDXI等) → 环境变量"
            echo "  Redis/EMQX默认密码 → 强密码"
            ;;
        all)
            refactor_phase1 fastjson
            refactor_phase1 cors
            refactor_phase1 ddl
            refactor_phase1 ip
            refactor_phase1 secrets
            ;;
        *)
            echo "用法: refactor.sh phase1 [fastjson|cors|ddl|ip|secrets|all]"
            ;;
    esac
}

refactor_phase2() {
    echo -e "${BOLD}===== 执行 PHASE-2: 框架升级 =====${NC}"
    local step="${1:-plan}"

    case "$step" in
        plan)
            echo "升级路径（不可跳过）:"
            echo "  Step 2a: Spring Boot 2.3.0 → 2.7.x (Java 8)"
            echo "  Step 2b: Java 8 → Java 17 (Spring Boot 2.7.x)"
            echo "  Step 2c: Spring Boot 2.7.x → 3.3.x (Java 17)"
            echo ""
            echo "每个Step执行后必须:"
            echo "  1. mvn clean compile -DskipTests"
            echo "  2. mvn clean package -pl <service> -am -DskipTests"
            echo "  3. 启动服务验证Nacos注册"
            ;;
        2a)
            echo -e "${BLUE}[Step 2a] Spring Boot 2.3.0 → 2.7.x${NC}"
            echo "  修改: 父POM spring-boot-starter-parent版本"
            echo "  修改: Spring Cloud Hoxton.SR1 → 2021.0.x"
            echo "  修改: Spring Cloud Alibaba → 2021.0.9.0"
            echo "  注意: 部分废弃API需适配"
            ;;
        2b)
            echo -e "${BLUE}[Step 2b] Java 8 → Java 17${NC}"
            echo "  修改: Dockerfile基础镜像 openjdk:8-jre → eclipse-temurin:17-jre"
            echo "  修改: Maven compiler source/target 1.8 → 17"
            echo "  注意: 反射/Unsafe相关代码需适配"
            ;;
        2c)
            echo -e "${BLUE}[Step 2c] Spring Boot 2.7.x → 3.3.x${NC}"
            echo "  修改: javax.* → jakarta.* (317处/120文件)"
            echo "  修改: Swagger 2 → SpringDoc OpenAPI (1335处/100文件)"
            echo "  修改: OAuth2 → spring-authorization-server"
            echo "  推荐: 使用OpenRewrite自动迁移"
            ;;
        *)
            echo "用法: refactor.sh phase2 [plan|2a|2b|2c]"
            ;;
    esac
}

case "${1:-status}" in
    status)
        refactor_status
        ;;
    phase1)
        refactor_phase1 "${2:-all}"
        ;;
    phase2)
        refactor_phase2 "${2:-plan}"
        ;;
    phase3)
        echo -e "${BOLD}PHASE-3: 代码质量优化${NC}"
        echo "  Swagger 2 → SpringDoc OpenAPI"
        echo "  宽泛异常捕获修复"
        echo "  groupId: org.example → com.sunmax"
        ;;
    phase4)
        echo -e "${BOLD}PHASE-4: 前端现代化${NC}"
        echo "  linkos/tycvs: Vue CLI → Vite"
        echo "  全部项目: Vuex → Pinia"
        echo "  TypeScript迁移"
        ;;
    phase5)
        echo -e "${BOLD}PHASE-5: 构建部署优化${NC}"
        echo "  容器资源限制"
        echo "  健康检查参数优化"
        echo "  JVM参数规范化"
        ;;
    *)
        echo "Elink-AI 重构升级技能"
        echo ""
        echo "用法: refactor.sh <command> [args]"
        echo ""
        echo "命令:"
        echo "  status              查看重构状态"
        echo "  phase1 [step]       执行PHASE-1安全加固"
        echo "  phase2 [step]       执行PHASE-2框架升级"
        echo "  phase3              PHASE-3代码质量"
        echo "  phase4              PHASE-4前端现代化"
        echo "  phase5              PHASE-5构建部署"
        ;;
esac
