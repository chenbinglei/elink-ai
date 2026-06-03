#!/bin/bash

COMPOSE="/work/elink-ai/docker-compose"
PROJECT_DIR="/work/elink-ai/elink-work"
ENV_FILE="/work/elink-ai/.env"

load_env_file() {
    if [ ! -f "$ENV_FILE" ]; then
        echo -e "\033[33m[WARN] .env 文件不存在: ${ENV_FILE}\033[0m"
        return 1
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
BACKUP_DIR="${PROJECT_DIR}/backups"
LOG_DIR="${PROJECT_DIR}/logs"
HISTORY_FILE="${LOG_DIR}/reload-history.log"
HEALTH_TIMEOUT=180
MONITOR_DURATION=10
MAX_BACKUPS=5

SERVICES=(
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

SERVICE_PORTS=(
    "auth-service:60001"
    "sunmax-gateway:5000"
    "system-service:60002"
    "device-service:60003"
    "data-service:60004"
    "protocol-service:60005"
    "crontab-service:60006"
    "devops-service:60007"
    "configure-service:60008"
    "together-service:60009"
    "webapp-service:60010"
)

NACOS_NAMES=(
    "auth-service:sauth-service"
    "sunmax-gateway:sunmax-gateway"
    "system-service:system-service"
    "device-service:device-service"
    "data-service:sunos-data-service"
    "protocol-service:sunos-protocol-service"
    "crontab-service:scrontab-service"
    "devops-service:devops-service"
    "configure-service:configure-service"
    "together-service:together-service"
    "webapp-service:swebapp-service"
)

JAR_MAPPING=(
    "auth-service:auth-service-exec.jar"
    "sunmax-gateway:sunmax-gateway-exec.jar"
    "system-service:system-service-exec.jar"
    "device-service:device-service-exec.jar"
    "data-service:data-service-exec.jar"
    "protocol-service:protocol-service-exec.jar"
    "crontab-service:scrontab-service-exec.jar"
    "devops-service:devops-service-exec.jar"
    "configure-service:configure-service-exec.jar"
    "together-service:together-service-exec.jar"
    "webapp-service:webapp-service-exec.jar"
)

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

ts() { date '+%Y-%m-%d %H:%M:%S'; }

log_info()  { echo -e "${GREEN}[INFO]  $(ts) $1${NC}"; }
log_warn()  { echo -e "${YELLOW}[WARN]  $(ts) $1${NC}"; }
log_error() { echo -e "${RED}[ERROR] $(ts) $1${NC}"; }
log_debug() { echo -e "${CYAN}[DEBUG] $(ts) $1${NC}"; }

log_history() {
    local msg="[$(ts)] $1"
    echo "$msg" >> "$HISTORY_FILE"
}

get_port() {
    local service=$1
    for mapping in "${SERVICE_PORTS[@]}"; do
        if [ "${mapping%%:*}" = "$service" ]; then
            echo "${mapping##*:}"
            return
        fi
    done
    echo ""
}

get_jar_name() {
    local service=$1
    for mapping in "${JAR_MAPPING[@]}"; do
        if [ "${mapping%%:*}" = "$service" ]; then
            echo "${mapping##*:}"
            return
        fi
    done
    echo ""
}

get_nacos_name() {
    local service=$1
    for mapping in "${NACOS_NAMES[@]}"; do
        if [ "${mapping%%:*}" = "$service" ]; then
            echo "${mapping##*:}"
            return
        fi
    done
    echo ""
}

is_valid_service() {
    local service=$1
    for s in "${SERVICES[@]}"; do
        if [ "$s" = "$service" ]; then
            return 0
        fi
    done
    return 1
}

ensure_dirs() {
    mkdir -p "$BACKUP_DIR"
    mkdir -p "$LOG_DIR"
    touch "$HISTORY_FILE"
}

capture_resource_snapshot() {
    local service=$1
    local container_id=$(docker ps -q -f "name=^${service}$")
    if [ -n "$container_id" ]; then
        docker stats --no-stream --format "{{.MemUsage}}|{{.CPUPerc}}|{{.NetIO}}|{{.BlockIO}}" "$container_id" 2>/dev/null || echo "N/A"
    else
        echo "NOT_RUNNING"
    fi
}

check_container_health() {
    local service=$1
    local health=$(docker inspect --format='{{.State.Health.Status}}' "$service" 2>/dev/null)
    if [ "$health" = "healthy" ]; then
        return 0
    fi
    return 1
}

check_http_health() {
    local port=$1
    local response=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "http://localhost:${port}/" 2>/dev/null)
    if [ -n "$response" ] && [ "$response" != "000" ]; then
        return 0
    fi
    return 1
}

check_nacos_registration() {
    local nacos_name=$1
    local result=$(curl -s "http://${NACOS_HOST}:${NACOS_PORT}/nacos/v1/ns/instance/list?serviceName=${nacos_name}" 2>/dev/null)
    if echo "$result" | grep -q '"healthy":true'; then
        return 0
    fi
    return 1
}

wait_for_healthy() {
    local service=$1
    local port=$2
    local nacos_name=$3
    local max_wait=$4
    local elapsed=0

    while [ $elapsed -lt $max_wait ]; do
        sleep 5
        elapsed=$((elapsed + 5))

        local container_health=$(docker inspect --format='{{.State.Health.Status}}' "$service" 2>/dev/null)
        local http_ok=false
        local nacos_ok=false

        if check_http_health "$port"; then
            http_ok=true
        fi

        if [ -n "$nacos_name" ] && check_nacos_registration "$nacos_name"; then
            nacos_ok=true
        elif [ -z "$nacos_name" ]; then
            nacos_ok=true
        fi

        if [ "$container_health" = "healthy" ] && [ "$http_ok" = true ] && [ "$nacos_ok" = true ]; then
            log_info "Service ${service} is HEALTHY (took ${elapsed}s)"
            return 0
        fi

        local status_parts=()
        [ "$container_health" = "healthy" ] && status_parts+=("container:✓") || status_parts+=("container:✗")
        [ "$http_ok" = true ] && status_parts+=("http:✓") || status_parts+=("http:✗")
        [ "$nacos_ok" = true ] && status_parts+=("nacos:✓") || status_parts+=("nacos:✗")

        log_warn "Waiting for ${service} [${status_parts[*]}] (${elapsed}/${max_wait}s)"
    done

    log_error "Service ${service} did not become healthy within ${max_wait}s"
    return 1
}

backup_jar() {
    local service=$1
    local jar_name=$(get_jar_name "$service")
    local jar_path="${PROJECT_DIR}/${service}/target/${jar_name}"

    if [ ! -f "$jar_path" ]; then
        log_warn "JAR not found for backup: ${jar_path}"
        return 1
    fi

    local timestamp=$(date '+%Y%m%d_%H%M%S')
    local backup_path="${BACKUP_DIR}/${service}_${jar_name}.${timestamp}"

    cp "$jar_path" "$backup_path"
    log_info "Backup created: ${backup_path}"

    local count=$(ls -1 "${BACKUP_DIR}/${service}_${jar_name}."* 2>/dev/null | wc -l)
    if [ "$count" -gt "$MAX_BACKUPS" ]; then
        local to_delete=$((count - MAX_BACKUPS))
        ls -1t "${BACKUP_DIR}/${service}_${jar_name}."* | tail -n "$to_delete" | xargs rm -f
        log_debug "Cleaned ${to_delete} old backup(s) for ${service}"
    fi

    echo "$backup_path"
    return 0
}

restore_jar() {
    local service=$1
    local backup_path=$2
    local jar_name=$(get_jar_name "$service")
    local jar_path="${PROJECT_DIR}/${service}/target/${jar_name}"

    if [ ! -f "$backup_path" ]; then
        log_error "Backup file not found: ${backup_path}"
        return 1
    fi

    cp "$backup_path" "$jar_path"
    log_info "Restored from backup: ${backup_path}"
    return 0
}

find_latest_backup() {
    local service=$1
    local jar_name=$(get_jar_name "$service")
    ls -1t "${BACKUP_DIR}/${service}_${jar_name}."* 2>/dev/null | head -1
}

compare_resources() {
    local service=$1
    local before="$2"
    local after="$3"

    if [ "$before" = "NOT_RUNNING" ] || [ "$after" = "NOT_RUNNING" ] || [ "$before" = "N/A" ] || [ "$after" = "N/A" ]; then
        log_warn "Cannot compare resources - service was not running or stats unavailable"
        return
    fi

    local before_mem=$(echo "$before" | cut -d'|' -f1 | awk '{print $1}')
    local after_mem=$(echo "$after" | cut -d'|' -f1 | awk '{print $1}')

    log_info "Resource comparison for ${service}:"
    log_info "  Before: ${before}"
    log_info "  After:  ${after}"

    if [ -n "$before_mem" ] && [ -n "$after_mem" ]; then
        local before_mb=$(echo "$before_mem" | sed 's/MiB//;s/GiB/*1024/;s/KiB/\/1024/' | bc 2>/dev/null || echo "0")
        local after_mb=$(echo "$after_mem" | sed 's/MiB//;s/GiB/*1024/;s/KiB/\/1024/' | bc 2>/dev/null || echo "0")
        local diff=$(echo "${after_mb} - ${before_mb}" | bc 2>/dev/null || echo "0")

        if (( $(echo "$diff > 100" | bc -l 2>/dev/null || echo "0") )); then
            log_warn "Memory increased by ${diff}MiB - potential leak detected!"
        else
            log_info "Memory change: ${diff}MiB (within acceptable range)"
        fi
    fi
}

hot_reload_service() {
    local service=$1
    local skip_build=$2

    local jar_name=$(get_jar_name "$service")
    local port=$(get_port "$service")
    local nacos_name=$(get_nacos_name "$service")

    if [ -z "$jar_name" ]; then
        log_error "Cannot find JAR mapping for ${service}"
        return 1
    fi

    local jar_path="${PROJECT_DIR}/${service}/target/${jar_name}"

    log_info "=========================================="
    log_info "Hot reload: ${service}"
    log_info "  Port: ${port}"
    log_info "  JAR:  ${jar_name}"
    log_info "=========================================="

    ensure_dirs

    log_info "[1/7] Checking current service state..."
    local is_running=$(docker ps -q -f "name=^${service}$")
    local before_resources=$(capture_resource_snapshot "$service")

    if [ -n "$is_running" ]; then
        log_info "  Service is running. Current resources: ${before_resources}"
    else
        log_warn "  Service is NOT running"
    fi

    log_info "[2/7] Creating backup of current JAR..."
    local backup_path=""
    if [ -n "$is_running" ] && [ -f "$jar_path" ]; then
        backup_path=$(backup_jar "$service")
        if [ $? -ne 0 ]; then
            log_warn "Backup failed - proceeding without rollback capability"
        fi
    fi

    if [ "$skip_build" != "true" ]; then
        log_info "[3/7] Building ${service}..."
        cd "$PROJECT_DIR"
        if ! mvn package -pl "$service" -am -DskipTests -T 4 > "${LOG_DIR}/build-${service}.log" 2>&1; then
            log_error "Build failed for ${service}!"
            log_error "Build log: ${LOG_DIR}/build-${service}.log"
            tail -20 "${LOG_DIR}/build-${service}.log"
            log_history "FAIL | ${service} | Build failed"
            return 1
        fi
        log_info "  Build successful"
    else
        log_info "[3/7] Skipping build (using existing JAR)"
    fi

    if [ ! -f "$jar_path" ]; then
        log_error "JAR not found after build: ${jar_path}"
        log_history "FAIL | ${service} | JAR not found"
        return 1
    fi

    log_info "[4/7] Restarting container ${service}..."
    cd "$PROJECT_DIR"
    $COMPOSE --env-file "$ENV_FILE" up -d --no-build --force-recreate "$service"

    log_info "[5/7] Waiting for health check..."
    if ! wait_for_healthy "$service" "$port" "$nacos_name" "$HEALTH_TIMEOUT"; then
        log_error "Health check FAILED for ${service}!"

        if [ -n "$backup_path" ] && [ -f "$backup_path" ]; then
            log_error "Initiating AUTOMATIC ROLLBACK..."
            restore_jar "$service" "$backup_path"
            $COMPOSE --env-file "$ENV_FILE" up -d --no-build --force-recreate "$service"

            log_info "Waiting for rollback to complete..."
            sleep 15

            if check_http_health "$port"; then
                log_info "Rollback SUCCESSFUL - ${service} restored to previous version"
                log_history "ROLLBACK_OK | ${service} | Rolled back from failed reload"
            else
                log_error "Rollback FAILED - ${service} may require manual intervention!"
                log_history "ROLLBACK_FAIL | ${service} | Both new and old versions failed"
                return 1
            fi
        else
            log_error "No backup available for rollback!"
            log_history "FAIL | ${service} | No backup for rollback"
            return 1
        fi
        return 1
    fi

    log_info "[6/7] Monitoring resources for ${MONITOR_DURATION}s..."
    local monitor_elapsed=0
    while [ $monitor_elapsed -lt $MONITOR_DURATION ]; do
        sleep 3
        monitor_elapsed=$((monitor_elapsed + 3))
        local current_resources=$(capture_resource_snapshot "$service")
        log_debug "  Monitor [${monitor_elapsed}/${MONITOR_DURATION}s]: ${current_resources}"
    done

    local after_resources=$(capture_resource_snapshot "$service")
    compare_resources "$service" "$before_resources" "$after_resources"

    log_info "[7/7] Verifying Nacos registration..."
    if [ -n "$nacos_name" ] && check_nacos_registration "$nacos_name"; then
        log_info "  ${service} (${nacos_name}) is registered in Nacos ✓"
    elif [ -z "$nacos_name" ]; then
        log_info "  Nacos name not configured, skipping registration check"
    else
        log_warn "  ${service} (${nacos_name}) is NOT yet registered in Nacos"
    fi

    log_info "=========================================="
    log_info "Hot reload COMPLETE: ${service}"
    log_info "=========================================="
    log_history "OK | ${service} | Reload successful | Before: ${before_resources} | After: ${after_resources}"
    return 0
}

do_rollback() {
    local service=$1
    local port=$(get_port "$service")
    local nacos_name=$(get_nacos_name "$service")

    log_info "Rolling back ${service}..."
    ensure_dirs

    local backup_path=$(find_latest_backup "$service")
    if [ -z "$backup_path" ]; then
        log_error "No backup found for ${service}"
        return 1
    fi

    log_info "Using backup: ${backup_path}"
    restore_jar "$service" "$backup_path"

    cd "$PROJECT_DIR"
    $COMPOSE --env-file "$ENV_FILE" up -d --no-build --force-recreate "$service"

    log_info "Waiting for rollback to complete..."
    if wait_for_healthy "$service" "$port" "$nacos_name" "$HEALTH_TIMEOUT"; then
        log_info "Rollback successful for ${service}"
        log_history "ROLLBACK_MANUAL | ${service} | Manual rollback from ${backup_path}"
        return 0
    else
        log_error "Rollback failed for ${service}"
        log_history "ROLLBACK_FAIL | ${service} | Manual rollback failed"
        return 1
    fi
}

do_status() {
    local service=$1
    echo -e "${BLUE}========== Service Status ==========${NC}"

    if [ -n "$service" ]; then
        if ! is_valid_service "$service"; then
            log_error "Unknown service: ${service}"
            return 1
        fi
        local services=("$service")
    else
        local services=("${SERVICES[@]}")
    fi

    printf "%-20s %-10s %-10s %-15s %-10s\n" "SERVICE" "STATUS" "HEALTH" "MEMORY" "NACOS"
    printf "%-20s %-10s %-10s %-15s %-10s\n" "-------" "------" "------" "------" "-----"

    for svc in "${services[@]}"; do
        local port=$(get_port "$svc")
        local nacos_name=$(get_nacos_name "$svc")
        local container_id=$(docker ps -q -f "name=^${svc}$")

        if [ -n "$container_id" ]; then
            local status=$(docker inspect --format='{{.State.Status}}' "$svc" 2>/dev/null)
            local health=$(docker inspect --format='{{.State.Health.Status}}' "$svc" 2>/dev/null || echo "none")
            local mem=$(docker stats --no-stream --format "{{.MemUsage}}" "$svc" 2>/dev/null | awk '{print $1}' || echo "N/A")
            local nacos_status="✗"

            if [ -n "$nacos_name" ] && check_nacos_registration "$nacos_name"; then
                nacos_status="✓"
            fi

            printf "%-20s %-10s %-10s %-15s %-10s\n" "$svc" "$status" "$health" "$mem" "$nacos_status"
        else
            printf "%-20s %-10s %-10s %-15s %-10s\n" "$svc" "stopped" "-" "-" "-"
        fi
    done
}

do_monitor() {
    local service=$1
    echo -e "${BLUE}========== Resource Monitor ==========${NC}"
    echo "Press Ctrl+C to stop"
    echo ""

    if [ -n "$service" ]; then
        if ! is_valid_service "$service"; then
            log_error "Unknown service: ${service}"
            return 1
        fi
        docker stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}" "$service"
    else
        local container_names=""
        for svc in "${SERVICES[@]}"; do
            local cid=$(docker ps -q -f "name=^${svc}$")
            if [ -n "$cid" ]; then
                container_names="$container_names $svc"
            fi
        done
        if [ -n "$container_names" ]; then
            docker stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}" $container_names
        else
            log_warn "No running services to monitor"
        fi
    fi
}

do_list() {
    echo -e "${BLUE}========== Available Services ==========${NC}"
    printf "%-20s %-8s %-25s %-10s %-15s\n" "SERVICE" "PORT" "JAR" "STATUS" "NACOS"
    printf "%-20s %-8s %-25s %-10s %-15s\n" "-------" "----" "---" "------" "------"

    for svc in "${SERVICES[@]}"; do
        local port=$(get_port "$svc")
        local jar_name=$(get_jar_name "$svc")
        local nacos_name=$(get_nacos_name "$svc")
        local container_id=$(docker ps -q -f "name=^${svc}$")
        local status="stopped"
        local nacos_status="-"

        if [ -n "$container_id" ]; then
            status="running"
            local health=$(docker inspect --format='{{.State.Health.Status}}' "$svc" 2>/dev/null || echo "")
            [ "$health" = "healthy" ] && status="healthy"
        fi

        if [ -n "$nacos_name" ] && check_nacos_registration "$nacos_name"; then
            nacos_status="registered"
        elif [ -n "$nacos_name" ]; then
            nacos_status="unregistered"
        fi

        local jar_path="${PROJECT_DIR}/${svc}/target/${jar_name}"
        local jar_info="${jar_name}"
        if [ -f "$jar_path" ]; then
            local jar_size=$(du -h "$jar_path" | awk '{print $1}')
            jar_info="${jar_name} (${jar_size})"
        fi

        printf "%-20s %-8s %-25s %-10s %-15s\n" "$svc" "$port" "$jar_info" "$status" "$nacos_status"
    done

    echo ""
    echo "Backup directory: ${BACKUP_DIR}/"
    local backup_count=$(ls -1 "${BACKUP_DIR}/"*.jar* 2>/dev/null | wc -l)
    echo "Total backups: ${backup_count}"
}

do_history() {
    local service=$1

    if [ ! -f "$HISTORY_FILE" ]; then
        echo "No reload history found"
        return
    fi

    echo -e "${BLUE}========== Reload History ==========${NC}"
    if [ -n "$service" ]; then
        grep "$service" "$HISTORY_FILE" | tail -20
    else
        tail -30 "$HISTORY_FILE"
    fi
}

do_watch() {
    local watch_services=("$@")
    if [ ${#watch_services[@]} -eq 0 ]; then
        watch_services=("${SERVICES[@]}")
    fi

    for svc in "${watch_services[@]}"; do
        if ! is_valid_service "$svc"; then
            log_error "Unknown service: ${svc}"
            return 1
        fi
    done

    ensure_dirs

    # 检查是否安装了 inotify-tools
    if ! command -v inotifywait &>/dev/null; then
        log_warn "inotifywait 未安装, 使用轮询模式 (可 apt install inotify-tools 升级为实时监控)"
        _do_watch_polling "${watch_services[@]}"
        return $?
    fi

    local pid_file="${LOG_DIR}/watch-daemon.pid"

    if [ -f "$pid_file" ]; then
        local old_pid=$(cat "$pid_file")
        if kill -0 "$old_pid" 2>/dev/null; then
            log_warn "Watch daemon already running (PID: ${old_pid})"
            log_info "Stop it first: kill $old_pid"
            return 1
        fi
    fi

    log_info "Starting file watch daemon (inotify 实时模式)..."
    log_info "Monitoring services: ${watch_services[*]}"
    log_info "PID file: ${pid_file}"
    log_info "Watch log: ${LOG_DIR}/watch-daemon.log"
    log_info "Press Ctrl+C to stop"

    # 构建 target 目录列表和 JAR 映射
    declare -A jar_map
    local target_dirs=""
    for svc in "${watch_services[@]}"; do
        local jar_name=$(get_jar_name "$svc")
        local jar_path="${PROJECT_DIR}/${svc}/target/${jar_name}"
        jar_map["$jar_path"]="$svc"
        local target_dir="${PROJECT_DIR}/${svc}/target"
        if [ -d "$target_dir" ]; then
            target_dirs="$target_dirs $target_dir"
        fi
    done

    echo $$ > "$pid_file"
    trap 'rm -f "$pid_file"; log_info "Watch daemon stopped"; exit 0' SIGINT SIGTERM

    log_info "Watch daemon started (PID: $$)"

    # 使用 inotifywait 实时监听文件变化 (零延迟)
    inotifywait -m -r -e close_write --format '%w%f' $target_dirs 2>/dev/null | while read -r changed_file; do
        # 只处理 .jar 文件变化
        if [[ "$changed_file" == *.jar ]]; then
            local svc="${jar_map[$changed_file]}"
            if [ -n "$svc" ]; then
                # 短暂等待确保写入完成
                sleep 2

                # 二次验证文件稳定
                local mtime1=$(stat -c '%Y' "$changed_file" 2>/dev/null || echo "0")
                sleep 3
                local mtime2=$(stat -c '%Y' "$changed_file" 2>/dev/null || echo "0")

                if [ "$mtime1" = "$mtime2" ] && [ "$mtime1" != "0" ]; then
                    log_info "[inotify] 检测到 ${svc}: JAR 文件已更新"
                    echo "[$(ts)] [inotify] Detected change in ${svc}" >> "${LOG_DIR}/watch-daemon.log"

                    log_info "触发热更新: ${svc}..."
                    echo "[$(ts)] Triggering hot reload for ${svc}" >> "${LOG_DIR}/watch-daemon.log"

                    hot_reload_service "$svc" "true" >> "${LOG_DIR}/watch-daemon.log" 2>&1
                    local result=$?

                    if [ $result -eq 0 ]; then
                        log_info "自动热更新成功: ${svc}"
                        echo "[$(ts)] Auto-reload SUCCESS: ${svc}" >> "${LOG_DIR}/watch-daemon.log"
                    else
                        log_error "自动热更新失败: ${svc}"
                        echo "[$(ts)] Auto-reload FAILED: ${svc}" >> "${LOG_DIR}/watch-daemon.log"
                    fi
                else
                    log_debug "[inotify] ${svc}: 文件仍在写入中, 跳过本次"
                fi
            fi
        fi
    done
}

# 兜底轮询模式 (当 inotifywait 不可用时使用)
_do_watch_polling() {
    local watch_services=("$@")

    local pid_file="${LOG_DIR}/watch-daemon.pid"

    if [ -f "$pid_file" ]; then
        local old_pid=$(cat "$pid_file")
        if kill -0 "$old_pid" 2>/dev/null; then
            log_warn "Watch daemon already running (PID: ${old_pid})"
            log_info "Stop it first: kill $old_pid"
            return 1
        fi
    fi

    log_info "Starting file watch daemon (polling 轮询模式, 间隔2秒)..."
    log_info "Monitoring services: ${watch_services[*]}"
    log_info "建议: sudo apt install inotify-tools 启用实时监控模式"
    log_info "Press Ctrl+C to stop"

    declare -A file_mtimes
    for svc in "${watch_services[@]}"; do
        local jar_name=$(get_jar_name "$svc")
        local jar_path="${PROJECT_DIR}/${svc}/target/${jar_name}"
        if [ -f "$jar_path" ]; then
            file_mtimes["$svc"]=$(stat -c '%Y' "$jar_path" 2>/dev/null || echo "0")
        else
            file_mtimes["$svc"]="0"
        fi
    done

    echo $$ > "$pid_file"
    trap 'rm -f "$pid_file"; log_info "Watch daemon stopped"; exit 0' SIGINT SIGTERM

    log_info "Watch daemon started (PID: $$)"

    while true; do
        for svc in "${watch_services[@]}"; do
            local jar_name=$(get_jar_name "$svc")
            local jar_path="${PROJECT_DIR}/${svc}/target/${jar_name}"

            if [ -f "$jar_path" ]; then
                local current_mtime=$(stat -c '%Y' "$jar_path" 2>/dev/null || echo "0")
                local stored_mtime="${file_mtimes[$svc]}"

                if [ "$current_mtime" != "$stored_mtime" ] && [ "$current_mtime" != "0" ]; then
                    log_info "Detected change in ${svc}: JAR modified ($(date -d "@$current_mtime" '+%H:%M:%S'))"
                    echo "[$(ts)] Detected change in ${svc}" >> "${LOG_DIR}/watch-daemon.log"

                    file_mtimes["$svc"]="$current_mtime"

                    sleep 2

                    local verify_mtime=$(stat -c '%Y' "$jar_path" 2>/dev/null || echo "0")
                    if [ "$verify_mtime" != "$current_mtime" ]; then
                        log_warn "File still being written, waiting..."
                        sleep 3
                        file_mtimes["$svc"]=$(stat -c '%Y' "$jar_path" 2>/dev/null || echo "0")
                        continue
                    fi

                    log_info "Triggering hot reload for ${svc}..."
                    echo "[$(ts)] Triggering hot reload for ${svc}" >> "${LOG_DIR}/watch-daemon.log"

                    hot_reload_service "$svc" "true" >> "${LOG_DIR}/watch-daemon.log" 2>&1
                    local result=$?

                    if [ $result -eq 0 ]; then
                        log_info "Auto-reload SUCCESS: ${svc}"
                        echo "[$(ts)] Auto-reload SUCCESS: ${svc}" >> "${LOG_DIR}/watch-daemon.log"
                    else
                        log_error "Auto-reload FAILED: ${svc}"
                        echo "[$(ts)] Auto-reload FAILED: ${svc}" >> "${LOG_DIR}/watch-daemon.log"
                    fi
                fi
            fi
        done

        sleep 2
    done
}

do_watch_background() {
    local watch_services=("$@")
    if [ ${#watch_services[@]} -eq 0 ]; then
        watch_services=("${SERVICES[@]}")
    fi

    ensure_dirs

    local pid_file="${LOG_DIR}/watch-daemon.pid"

    if [ -f "$pid_file" ]; then
        local old_pid=$(cat "$pid_file")
        if kill -0 "$old_pid" 2>/dev/null; then
            log_info "Watch daemon already running (PID: ${old_pid})"
            return 0
        fi
    fi

    log_info "Starting watch daemon in background..."

    nohup "$0" watch "${watch_services[@]}" >> "${LOG_DIR}/watch-daemon.log" 2>&1 &
    local bg_pid=$!

    sleep 2

    if kill -0 "$bg_pid" 2>/dev/null; then
        log_info "Watch daemon started (PID: ${bg_pid})"
        log_info "Monitoring: ${watch_services[*]}"
        log_info "Log: ${LOG_DIR}/watch-daemon.log"
        log_info "Stop: kill ${bg_pid}"
    else
        log_error "Failed to start watch daemon"
        return 1
    fi
}

do_watch_stop() {
    local pid_file="${LOG_DIR}/watch-daemon.pid"

    if [ ! -f "$pid_file" ]; then
        log_warn "Watch daemon not running"
        return 0
    fi

    local pid=$(cat "$pid_file")
    if kill -0 "$pid" 2>/dev/null; then
        kill "$pid"
        rm -f "$pid_file"
        log_info "Watch daemon stopped (PID: ${pid})"
    else
        rm -f "$pid_file"
        log_warn "Watch daemon PID ${pid} not found, cleaned up PID file"
    fi
}

show_usage() {
    cat <<EOF
elink-work Hot Reload Tool v2.0

用法:
  $0 reload <service>        热更新单个服务 (自动构建 + 重启 + 验证 + 回滚)
  $0 reload <service> skip   热更新服务 (跳过构建, 使用已有JAR)
  $0 all                      热更新所有服务
  $0 watch [services...]      前台监控模式 (检测JAR变化自动热更新)
  $0 watch-start [services...] 后台启动监控守护进程
  $0 watch-stop               停止后台监控守护进程
  $0 rollback <service>       手动回滚到上次备份版本
  $0 status [service]         查看服务健康状态和Nacos注册
  $0 monitor [service]        实时资源监控 (CPU/内存/IO)
  $0 list                     列出所有服务及状态
  $0 history [service]        查看热更新历史

特性:
  ✓ 实时文件监听: 使用 inotifywait 零延迟检测JAR变化 (回退2秒轮询)
  ✓ 三层健康验证: Docker健康检查 + HTTP端口 + Nacos注册
  ✓ 自动回滚: 热更新失败时恢复到上一稳定版本
  ✓ 资源监控: 更新前后对比内存/CPU, 检测内存泄漏
  ✓ 优雅关闭: 确保服务完成进行中请求后才停止
  ✓ 多版本备份: 保留最近${MAX_BACKUPS}个备份版本

示例:
  $0 reload protocol-service          更新protocol-service
  $0 reload protocol-service skip     更新(不重新编译)
  $0 all                              更新所有服务
  $0 watch-start protocol-service     后台监控protocol-service变化
  $0 watch-start                      后台监控所有服务变化
  $0 rollback protocol-service        回滚protocol-service
  $0 status                           查看所有服务状态
  $0 monitor protocol-service         监控protocol-service资源

可用服务:
$(for s in "${SERVICES[@]}"; do echo "  - $s (port: $(get_port $s))"; done)
EOF
}

if [ $# -eq 0 ]; then
    show_usage
    exit 0
fi

cd "$PROJECT_DIR"

case "$1" in
    reload)
        if [ -z "$2" ]; then
            log_error "Missing service name"
            echo ""
            show_usage
            exit 1
        fi
        SERVICE_NAME="$2"
        if ! is_valid_service "$SERVICE_NAME"; then
            log_error "Unknown service: ${SERVICE_NAME}"
            exit 1
        fi
        SKIP_BUILD="${3:-false}"
        hot_reload_service "$SERVICE_NAME" "$SKIP_BUILD"
        ;;
    all)
        log_info "========== Hot Reload All Services =========="
        cd "$PROJECT_DIR"
        log_info "Building all services..."
        if ! mvn package -DskipTests -T 4 > "${LOG_DIR}/build-all.log" 2>&1; then
            log_error "Build failed! Check ${LOG_DIR}/build-all.log"
            tail -20 "${LOG_DIR}/build-all.log"
            exit 1
        fi
        log_info "Build complete. Starting reloads..."

        FAILED_SERVICES=()
        for svc in "${SERVICES[@]}"; do
            echo ""
            if ! hot_reload_service "$svc" "true"; then
                FAILED_SERVICES+=("$svc")
            fi
        done

        echo ""
        log_info "========== Reload Summary =========="
        if [ ${#FAILED_SERVICES[@]} -eq 0 ]; then
            log_info "All services reloaded successfully ✓"
        else
            log_error "Failed services: ${FAILED_SERVICES[*]}"
            log_info "Use '$0 rollback <service>' to rollback failed services"
        fi
        ;;
    watch)
        shift
        do_watch "$@"
        ;;
    watch-start)
        shift
        do_watch_background "$@"
        ;;
    watch-stop)
        do_watch_stop
        ;;
    rollback)
        if [ -z "$2" ]; then
            log_error "Missing service name"
            exit 1
        fi
        do_rollback "$2"
        ;;
    status)
        do_status "$2"
        ;;
    monitor)
        do_monitor "$2"
        ;;
    list)
        do_list
        ;;
    history)
        do_history "$2"
        ;;
    *)
        log_error "Unknown command: $1"
        echo ""
        show_usage
        exit 1
        ;;
esac
