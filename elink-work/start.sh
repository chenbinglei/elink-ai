#!/bin/bash
set -e

COMPOSE="/work/elink-ai/docker-compose"
PROJECT_DIR="/work/elink-ai/elink-work"
ENV_FILE="/work/elink-ai/elink-work/.env"

load_env_file() {
    if [ ! -f "$ENV_FILE" ]; then
        echo -e "\033[31m[ERROR] .env 文件不存在: ${ENV_FILE}\033[0m"
        exit 1
    fi
    while IFS='=' read -r key value; do
        key=$(echo "$key" | xargs)
        value=$(echo "$value" | xargs)
        case "$key" in
            ''|\#*) continue ;;
        esac
        export "$key=$value"
    done < "$ENV_FILE"
    echo -e "\033[32m[INFO] 已加载 .env 文件: ${ENV_FILE}\033[0m"
    return 0
}

load_env_file

NACOS_HOST="${NACOS_HOST:-127.0.0.1}"
NACOS_PORT="${NACOS_PORT:-8848}"
export NACOS_IP="$NACOS_HOST"
export NACOS_PORT="$NACOS_PORT"

WATCH_MODE=false

ALL_SERVICES=(
    auth-service
    sunmax-gateway
    system-service
    device-service
    data-service
    protocol-service
    crontab-service
    devops-service
    configure-service
    together-service
    webapp-service
)

NACOS_SERVICE_NAMES=(
    sauth-service
    sunmax-gateway
    system-service
    device-service
    sunos-data-service
    sunos-protocol-service
    crontab-service
    devops-service
    configure-service
    together-service
    swebapp-service
)

log_info()  { echo -e "\033[32m[INFO]  $(date '+%H:%M:%S') $1\033[0m"; }
log_warn()  { echo -e "\033[33m[WARN]  $(date '+%H:%M:%S') $1\033[0m"; }
log_error() { echo -e "\033[31m[ERROR] $(date '+%H:%M:%S') $1\033[0m"; }

check_service_registered() {
    local service_name=$1
    local max_retries=$2
    local retry=0
    while [ $retry -lt $max_retries ]; do
        local result=$(curl -s "http://${NACOS_HOST}:${NACOS_PORT}/nacos/v1/ns/instance/list?serviceName=${service_name}" 2>/dev/null)
        if echo "$result" | grep -q '"healthy":true'; then
            return 0
        fi
        retry=$((retry + 1))
        log_warn "等待 ${service_name} 注册到Nacos... (${retry}/${max_retries})"
        sleep 5
    done
    return 1
}

wait_for_port() {
    local host=$1
    local port=$2
    local max_retries=$3
    local retry=0
    while [ $retry -lt $max_retries ]; do
        if (echo > /dev/tcp/${host}/${port}) 2>/dev/null; then
            return 0
        fi
        retry=$((retry + 1))
        log_warn "等待端口 ${host}:${port} 可用... (${retry}/${max_retries})"
        sleep 3
    done
    return 1
}

for arg in "$@"; do
    case $arg in
        --watch) WATCH_MODE=true ;;
        --help)
            echo "用法: $0 [--watch] [--help]"
            echo "  --watch  启动后自动开启文件监控守护进程"
            echo "  --help   显示帮助信息"
            exit 0
            ;;
    esac
done

log_info "========== elink-work 后端服务部署启动 =========="
cd "$PROJECT_DIR"

mkdir -p backups logs

log_info "[1/8] 停止并清理旧容器..."
$COMPOSE --env-file "$ENV_FILE" down 2>/dev/null || true
docker rm -f redis emqx1 emqx2 nacos 2>/dev/null || true

log_info "[2/8] Maven 构建项目..."
mvn clean package -DskipTests -T 4
log_info "Maven 构建完成!"

log_info "[3/8] 构建自定义基础镜像..."
docker build -t elink-base:latest -f Dockerfile .
log_info "基础镜像构建完成!"

log_info "[4/8] 启动基础设施服务 (Redis + EMQX + Nacos)..."
$COMPOSE --env-file "$ENV_FILE" up -d redis emqx1 emqx2 nacos

log_info "等待基础设施服务就绪..."
sleep 15

wait_for_port "$NACOS_HOST" "8848" 30
if [ $? -ne 0 ]; then
    log_error "Nacos 启动失败!"
    exit 1
fi
log_info "Nacos 已就绪!"

wait_for_port "$NACOS_HOST" "6379" 20
if [ $? -ne 0 ]; then
    log_error "Redis 启动失败!"
    exit 1
fi
log_info "Redis 已就绪!"

wait_for_port "$NACOS_HOST" "1883" 20
if [ $? -ne 0 ]; then
    log_error "EMQX1 (1883) 启动失败!"
    exit 1
fi
log_info "EMQX1 (1883) 已就绪!"

wait_for_port "$NACOS_HOST" "2883" 20
if [ $? -ne 0 ]; then
    log_error "EMQX2 (2883) 启动失败!"
    exit 1
fi
log_info "EMQX2 (2883) 已就绪!"

log_info "[5/8] 启动认证网关服务 (auth-service, sunmax-gateway)..."
$COMPOSE --env-file "$ENV_FILE" up -d --no-build auth-service sunmax-gateway
log_info "等待 auth-service 注册到 Nacos..."
check_service_registered "sauth-service" 40
if [ $? -ne 0 ]; then
    log_warn "auth-service 尚未注册到Nacos, 继续等待..."
fi
log_info "auth-service 和 gateway 已启动!"

log_info "[6/8] 启动基础业务服务 (system, device, data, devops, configure, together, webapp)..."
$COMPOSE --env-file "$ENV_FILE" up -d --no-build system-service device-service data-service devops-service configure-service together-service webapp-service

log_info "等待 device-service 成功启动并注册到 Nacos..."
check_service_registered "device-service" 60
if [ $? -ne 0 ]; then
    log_error "device-service 未能成功注册到 Nacos, protocol-service 无法启动!"
    exit 1
fi
log_info "device-service 已成功注册到 Nacos!"

log_info "等待 together-service 成功启动并注册到 Nacos..."
check_service_registered "together-service" 60
if [ $? -ne 0 ]; then
    log_error "together-service 未能成功注册到 Nacos, crontab-service 无法启动!"
    exit 1
fi
log_info "together-service 已成功注册到 Nacos!"

log_info "[7/8] 启动 protocol-service 和 crontab-service (带 Nacos 依赖等待)..."
$COMPOSE --env-file "$ENV_FILE" up -d --no-build protocol-service crontab-service
log_info "protocol-service 和 crontab-service 已启动!"

log_info "[8/8] 等待所有服务完全启动..."
sleep 30

if [ "$WATCH_MODE" = true ]; then
    log_info "启动文件监控守护进程..."
    ./hot-reload.sh watch-start
fi

log_info "========== 服务状态 =========="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | sort

echo ""
log_info "========== 端口监听状态 =========="
ss -tlnp 2>/dev/null | grep -E '(5000|6379|8848|1883|2883|60001|60002|60003|60004|60005|60006|60007|60008|60009|60010)' | sort -t: -k2 -n || true

echo ""
log_info "========== Nacos 服务注册检查 =========="
for i in "${!NACOS_SERVICE_NAMES[@]}"; do
    svc="${NACOS_SERVICE_NAMES[$i]}"
    docker_svc="${ALL_SERVICES[$i]}"
    result=$(curl -s "http://${NACOS_HOST}:${NACOS_PORT}/nacos/v1/ns/instance/list?serviceName=${svc}" 2>/dev/null | grep -o '"healthy":true' | head -1)
    if [ -n "$result" ]; then
        log_info "  ${docker_svc} (${svc}): 已注册"
    else
        log_warn "  ${docker_svc} (${svc}): 未注册"
    fi
done

echo ""
log_info "========== 部署完成 =========="
log_info "网关地址: http://${NACOS_HOST}:5000"
log_info "Nacos控制台: http://${NACOS_HOST}:8848/nacos"
log_info "EMQX1控制台: http://${NACOS_HOST}:18083 (admin/public)"
log_info "EMQX2控制台: http://${NACOS_HOST}:28083 (admin/public)"
echo ""
log_info "热更新命令:"
log_info "  ./hot-reload.sh reload <service>        单服务热更新"
log_info "  ./hot-reload.sh all                      全部热更新"
log_info "  ./hot-reload.sh watch-start [services]   后台自动监控"
log_info "  ./hot-reload.sh status                   服务状态"
log_info "  ./hot-reload.sh monitor [service]        资源监控"
log_info "  ./hot-reload.sh rollback <service>       回滚服务"
log_info "  ./hot-reload.sh history [service]        更新历史"
