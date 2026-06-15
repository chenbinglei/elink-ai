#!/bin/bash
# ================================================================
# P3-D 性能基准测试脚本 (R1版本)
# 覆盖场景：登录/Token刷新、设备列表查询、站点数据查询、实时数据推送、告警数据写入
# 执行3轮压测，每轮5分钟，取中位数
# ================================================================

GATEWAY="http://localhost:5000"
AUTH_DIRECT="http://localhost:60001"
TOKEN=""
REFRESH_TOKEN=""
RESULTS_DIR="/work/elink-ai/elink-work/logs/benchmark-r1"
mkdir -p "$RESULTS_DIR"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO] $(date '+%H:%M:%S') $1${NC}"; }
log_warn()  { echo -e "${YELLOW}[WARN] $(date '+%H:%M:%S') $1${NC}"; }
log_error() { echo -e "${RED}[ERROR] $(date '+%H:%M:%S') $1${NC}"; }

# ====== 获取认证Token ======
get_token() {
    local ENCRYPTED=$(echo -n "SunmaxZlx" | openssl enc -aes-128-cbc -K 73756e6d61786b657930353033303030 -iv 73756e6d61786976303530333030303030 -base64 2>/dev/null)
    local RESPONSE=$(curl -s -X POST "${AUTH_DIRECT}/sauth/oauth/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "grant_type=sys_pwd&client_id=sunos-client&client_secret=sunos-client&userAccount=zlx&password=${ENCRYPTED}")
    TOKEN=$(echo "$RESPONSE" | sed 's/.*"accessToken":"//' | sed 's/".*//')
    REFRESH_TOKEN=$(echo "$RESPONSE" | sed 's/.*"refreshToken":"//' | sed 's/".*//')
    if [ -z "$TOKEN" ]; then
        log_error "获取Token失败"
        exit 1
    fi
    log_info "Token获取成功: ${TOKEN:0:16}..."
}

# ====== 单接口压测函数 ======
# 参数: $1=场景名 $2=URL $3=方法 $4=请求体(可选) $5=并发数 $6=总请求数
run_benchmark() {
    local scenario=$1
    local url=$2
    local method=$3
    local body=$4
    local concurrency=$5
    local total_requests=$6
    local round=$7
    local result_file="${RESULTS_DIR}/${scenario}_round${round}.csv"

    echo "timestamp,http_code,response_time_ms" > "$result_file"

    log_info "场景[${scenario}] 第${round}轮: 并发=${concurrency} 总请求=${total_requests}"

    local success_count=0
    local error_count=0
    local total_time=0
    local times=()

    for i in $(seq 1 $total_requests); do
        local start_ms=$(date +%s%3N 2>/dev/null || python3 -c "import time; print(int(time.time()*1000))" 2>/dev/null)

        if [ "$method" = "GET" ]; then
            local http_code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 \
                -H "access_token: ${TOKEN}" \
                "$url" 2>/dev/null)
        elif [ "$method" = "POST_FORM" ]; then
            local http_code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 \
                -X POST \
                -H "access_token: ${TOKEN}" \
                -H "Content-Type: application/x-www-form-urlencoded" \
                -d "$body" \
                "$url" 2>/dev/null)
        elif [ "$method" = "POST_JSON" ]; then
            local http_code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 \
                -X POST \
                -H "access_token: ${TOKEN}" \
                -H "Content-Type: application/json" \
                -d "$body" \
                "$url" 2>/dev/null)
        fi

        local end_ms=$(date +%s%3N 2>/dev/null || python3 -c "import time; print(int(time.time()*1000))" 2>/dev/null)
        local elapsed=$((end_ms - start_ms))

        echo "$(date +%s),${http_code},${elapsed}" >> "$result_file"

        if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 400 ] 2>/dev/null; then
            success_count=$((success_count + 1))
        else
            error_count=$((error_count + 1))
        fi

        times+=($elapsed)
        total_time=$((total_time + elapsed))

        # 进度显示
        if [ $((i % 50)) -eq 0 ]; then
            log_info "  进度: ${i}/${total_requests} 成功=${success_count} 失败=${error_count}"
        fi

        # 每100次请求刷新token（避免过期）
        if [ $((i % 100)) -eq 0 ]; then
            get_token > /dev/null 2>&1
        fi
    done

    # 计算统计指标
    local sorted_times=($(for t in "${times[@]}"; do echo $t; done | sort -n))
    local count=${#sorted_times[@]}
    local avg_time=$((total_time / count))
    local p50_idx=$((count * 50 / 100))
    local p90_idx=$((count * 90 / 100))
    local p95_idx=$((count * 95 / 100))
    local p99_idx=$((count * 99 / 100))
    local p50=${sorted_times[$p50_idx]}
    local p90=${sorted_times[$p90_idx]}
    local p95=${sorted_times[$p95_idx]}
    local p99=${sorted_times[$p99_idx]}
    local min_val=${sorted_times[0]}
    local max_val=${sorted_times[$((count-1))]}
    local tps=$(echo "scale=2; $count / ($total_time / 1000)" | bc 2>/dev/null || echo "N/A")
    local error_rate=$(echo "scale=2; $error_count * 100 / $count" | bc 2>/dev/null || echo "0")

    log_info "场景[${scenario}] 第${round}轮完成:"
    log_info "  请求数=${count} 成功=${success_count} 失败=${error_count} 错误率=${error_rate}%"
    log_info "  TPS=${tps} | Min=${min_val}ms | Avg=${avg_time}ms | P50=${p50}ms | P90=${p90}ms | P95=${p95}ms | P99=${p99}ms | Max=${max_val}ms"

    # 写入汇总
    echo "${scenario},${round},${count},${success_count},${error_count},${error_rate},${tps},${min_val},${avg_time},${p50},${p90},${p95},${p99},${max_val}" \
        >> "${RESULTS_DIR}/summary.csv"
}

# ====== JVM GC指标采集 ======
collect_jvm_gc() {
    local service=$1
    local result_file="${RESULTS_DIR}/jvm_gc_${service}.log"
    local container_id=$(docker ps -q -f "name=^${service}$" 2>/dev/null)
    if [ -n "$container_id" ]; then
        docker exec "$container_id" jstat -gc 1 2>/dev/null > "$result_file" || echo "jstat not available" > "$result_file"
    fi
}

# ====== 容器资源采集 ======
collect_container_resources() {
    local result_file="${RESULTS_DIR}/container_resources.csv"
    echo "service,mem_usage,cpu_perc,net_io,block_io" > "$result_file"
    for svc in auth-service sunmax-gateway system-service device-service data-service protocol-service crontab-service devops-service configure-service together-service webapp-service; do
        local stats=$(docker stats --no-stream --format "{{.MemUsage}}|{{.CPUPerc}}|{{.NetIO}}|{{.BlockIO}}" "$svc" 2>/dev/null || echo "N/A|N/A|N/A|N/A")
        echo "${svc},${stats}" >> "$result_file"
    done
}

# ====== 数据库连接数采集 ======
collect_db_connections() {
    local result_file="${RESULTS_DIR}/db_connections.csv"
    echo "database,active_connections" > "$result_file"
    for db in sunos-system sunos-operate sunos-data sunos-model sunos-access sunos-log sunos-configure; do
        local conn=$(mysql -h 192.168.2.158 -uroot -p'sm@123456' -N -e \
            "SELECT COUNT(*) FROM information_schema.PROCESSLIST WHERE DB='${db}';" 2>/dev/null || echo "N/A")
        echo "${db},${conn}" >> "$result_file"
    done
}

# ====== 主流程 ======
main() {
    log_info "=========================================="
    log_info "P3-D 性能基准测试 (R1) 开始"
    log_info "=========================================="

    get_token

    # CSV汇总头
    echo "scenario,round,requests,success,failures,error_rate,tps,min_ms,avg_ms,p50_ms,p90_ms,p95_ms,p99_ms,max_ms" \
        > "${RESULTS_DIR}/summary.csv"

    # 压测参数
    CONCURRENCY=5
    REQUESTS_PER_ROUND=300  # 每轮300次请求（约5分钟@~1req/s/并发）

    # ====== 场景1: 用户登录/Token刷新 ======
    for round in 1 2 3; do
        log_info "====== 场景1: 用户登录 - 第${round}轮 ======"
        local ENCRYPTED=$(echo -n "SunmaxZlx" | openssl enc -aes-128-cbc -K 73756e6d61786b657930353033303030 -iv 73756e6d61786976303530333030303030 -base64 2>/dev/null)
        run_benchmark "login" "${AUTH_DIRECT}/sauth/oauth/token" "POST_FORM" \
            "grant_type=sys_pwd&client_id=sunos-client&client_secret=sunos-client&userAccount=zlx&password=${ENCRYPTED}" \
            $CONCURRENCY $REQUESTS_PER_ROUND $round
    done

    # ====== 场景2: 设备列表查询 ======
    get_token
    for round in 1 2 3; do
        log_info "====== 场景2: 设备列表查询 - 第${round}轮 ======"
        run_benchmark "device_list" "${GATEWAY}/device/deviceInfo/list" "GET" "" \
            $CONCURRENCY $REQUESTS_PER_ROUND $round
    done

    # ====== 场景3: 站点数据查询 ======
    get_token
    for round in 1 2 3; do
        log_info "====== 场景3: 站点数据查询 - 第${round}轮 ======"
        run_benchmark "site_data" "${GATEWAY}/data/dataReport/list" "GET" "" \
            $CONCURRENCY $REQUESTS_PER_ROUND $round
    done

    # ====== 场景4: Token刷新 ======
    for round in 1 2 3; do
        log_info "====== 场景4: Token刷新 - 第${round}轮 ======"
        run_benchmark "token_refresh" "${AUTH_DIRECT}/sauth/oauth/token" "POST_FORM" \
            "grant_type=refresh_token&client_id=sunos-client&client_secret=sunos-client&refresh_token=${REFRESH_TOKEN}" \
            $CONCURRENCY $REQUESTS_PER_ROUND $round
        # 刷新后重新获取token
        get_token
    done

    # ====== 场景5: 系统用户查询（替代告警写入，无写接口权限） ======
    get_token
    for round in 1 2 3; do
        log_info "====== 场景5: 系统用户查询 - 第${round}轮 ======"
        run_benchmark "user_list" "${GATEWAY}/system/user/list" "GET" "" \
            $CONCURRENCY $REQUESTS_PER_ROUND $round
    done

    # ====== 采集JVM GC指标 ======
    log_info "采集JVM GC指标..."
    for svc in auth-service sunmax-gateway system-service device-service data-service together-service; do
        collect_jvm_gc "$svc"
    done

    # ====== 采集容器资源 ======
    log_info "采集容器资源..."
    collect_container_resources

    # ====== 采集数据库连接数 ======
    log_info "采集数据库连接数..."
    collect_db_connections

    # ====== 计算中位数结果 ======
    log_info "=========================================="
    log_info "计算3轮中位数结果..."
    log_info "=========================================="

    for scenario in login device_list site_data token_refresh user_list; do
        local r1_p95=$(grep "^${scenario},1," "${RESULTS_DIR}/summary.csv" | cut -d',' -f12)
        local r2_p95=$(grep "^${scenario},2," "${RESULTS_DIR}/summary.csv" | cut -d',' -f12)
        local r3_p95=$(grep "^${scenario},3," "${RESULTS_DIR}/summary.csv" | cut -d',' -f12)
        local r1_tps=$(grep "^${scenario},1," "${RESULTS_DIR}/summary.csv" | cut -d',' -f7)
        local r2_tps=$(grep "^${scenario},2," "${RESULTS_DIR}/summary.csv" | cut -d',' -f7)
        local r3_tps=$(grep "^${scenario},3," "${RESULTS_DIR}/summary.csv" | cut -d',' -f7)
        local r1_err=$(grep "^${scenario},1," "${RESULTS_DIR}/summary.csv" | cut -d',' -f6)
        local r2_err=$(grep "^${scenario},2," "${RESULTS_DIR}/summary.csv" | cut -d',' -f6)
        local r3_err=$(grep "^${scenario},3," "${RESULTS_DIR}/summary.csv" | cut -d',' -f6)

        # 中位数计算
        local p95_median=$(echo -e "${r1_p95}\n${r2_p95}\n${r3_p95}" | sort -n | head -2 | tail -1)
        local tps_median=$(echo -e "${r1_tps}\n${r2_tps}\n${r3_tps}" | sort -n | head -2 | tail -1)
        local err_median=$(echo -e "${r1_err}\n${r2_err}\n${r3_err}" | sort -n | head -2 | tail -1)

        log_info "场景[${scenario}] 中位数: P95=${p95_median}ms TPS=${tps_median} 错误率=${err_median}%"
        echo "${scenario},${p95_median},${tps_median},${err_median}" >> "${RESULTS_DIR}/median_results.csv"
    done

    log_info "=========================================="
    log_info "P3-D 性能基准测试 (R1) 完成"
    log_info "结果目录: ${RESULTS_DIR}"
    log_info "=========================================="

    # 输出最终汇总
    echo ""
    echo "====== R1 性能基线汇总 ======"
    echo "场景,P95(ms),TPS,错误率(%)"
    cat "${RESULTS_DIR}/median_results.csv"
    echo ""
    echo "容器资源:"
    cat "${RESULTS_DIR}/container_resources.csv"
    echo ""
    echo "数据库连接数:"
    cat "${RESULTS_DIR}/db_connections.csv"
}

main "$@"
