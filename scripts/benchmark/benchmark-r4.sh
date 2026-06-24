#!/bin/bash
# ================================================================
# P5-13 R4 性能基线测试脚本
# 覆盖场景：用户登录/Token刷新/设备列表查询/站点数据查询/系统用户查询
# 执行3轮压测，取中位数，生成HTML报告
# 与R1基线（curl-based）保持一致方法学以便对比
# ================================================================

set -euo pipefail

GATEWAY="http://localhost:5000"
AUTH_DIRECT="http://localhost:60001"
TOKEN=""
REFRESH_TOKEN=""
RESULTS_DIR="/work/elink-ai/elink-work/logs/benchmark-r4"
REPORT_DIR="/work/elink-ai/logs"
REPORT_FILE="${REPORT_DIR}/perf_baseline_R4_$(date +%Y%m%d).html"
DATE_STR=$(date '+%Y-%m-%d %H:%M:%S')

mkdir -p "$RESULTS_DIR" "$REPORT_DIR"

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
        -d "grant_type=sys_pwd&client_id=sunos-client&client_secret=sunos-client&userAccount=zlx&password=${ENCRYPTED}" 2>/dev/null)
    TOKEN=$(echo "$RESPONSE" | sed 's/.*"accessToken":"//' | sed 's/".*//')
    REFRESH_TOKEN=$(echo "$RESPONSE" | sed 's/.*"refreshToken":"//' | sed 's/".*//')
    if [ -z "$TOKEN" ]; then
        log_error "获取Token失败"
        exit 1
    fi
    log_info "Token获取成功: ${TOKEN:0:16}..."
}

# ====== 单接口压测函数 ======
run_benchmark() {
    local scenario=$1
    local url=$2
    local method=$3
    local body=$4
    local total_requests=$5
    local round=$6
    local result_file="${RESULTS_DIR}/${scenario}_round${round}.csv"

    echo "timestamp,http_code,response_time_ms" > "$result_file"

    log_info "场景[${scenario}] 第${round}轮: 总请求=${total_requests}"

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

        if [ $((i % 50)) -eq 0 ]; then
            log_info "  进度: ${i}/${total_requests} 成功=${success_count} 失败=${error_count}"
        fi

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

    log_info "场景[${scenario}] 第${round}轮: P95=${p95}ms P99=${p99}ms TPS=${tps} 错误率=${error_rate}%"

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
    for svc in auth-service sunmax-gateway system-service device-service data-service; do
        local stats=$(docker stats --no-stream --format "{{.MemUsage}}|{{.CPUPerc}}|{{.NetIO}}|{{.BlockIO}}" "$svc" 2>/dev/null || echo "N/A|N/A|N/A|N/A")
        echo "${svc},${stats}" >> "$result_file"
    done
}

# ====== 生成HTML报告 ======
generate_html_report() {
    local median_file="${RESULTS_DIR}/median_results.csv"
    local gc_data=""
    local resource_data=""

    # 读取中位数结果
    local api_rows=""
    while IFS=',' read -r scenario p95 p99 tps error_rate; do
        local p95_status="✅"
        local p99_status="✅"
        local err_status="✅"
        [ "$p95" -gt 500 ] 2>/dev/null && p95_status="❌"
        [ "$p99" -gt 1000 ] 2>/dev/null && p99_status="❌"
        local err_val=$(echo "$error_rate" | bc 2>/dev/null)
        (( $(echo "$err_val > 0.5" | bc -l 2>/dev/null || echo 0) )) && err_status="❌"

        local scenario_cn=""
        case $scenario in
            login) scenario_cn="用户登录";;
            token_refresh) scenario_cn="Token刷新";;
            device_list) scenario_cn="设备列表查询";;
            site_data) scenario_cn="站点数据查询";;
            user_list) scenario_cn="系统用户查询";;
        esac

        api_rows="${api_rows}
        <tr>
          <td>${scenario_cn}</td>
          <td>${p95} ${p95_status}</td>
          <td>${p99} ${p99_status}</td>
          <td>${tps}</td>
          <td>${error_rate}% ${err_status}</td>
        </tr>"
    done < "$median_file"

    # 读取JVM GC数据
    local gc_rows=""
    for svc in auth-service sunmax-gateway system-service device-service data-service; do
        local gc_file="${RESULTS_DIR}/jvm_gc_${svc}.log"
        if [ -f "$gc_file" ]; then
            local gc_line=$(cat "$gc_file" | tail -1)
            gc_rows="${gc_rows}
        <tr>
          <td>${svc}</td>
          <td colspan='5'><pre>${gc_line}</pre></td>
        </tr>"
        fi
    done

    # 读取容器资源
    local res_rows=""
    while IFS=',' read -r service mem cpu net_io block_io; do
        [ "$service" = "service" ] && continue
        res_rows="${res_rows}
        <tr>
          <td>${service}</td>
          <td>${mem}</td>
          <td>${cpu}</td>
          <td>${net_io}</td>
          <td>${block_io}</td>
        </tr>"
    done < "${RESULTS_DIR}/container_resources.csv"

    # R1对比数据
    local r1_compare="
        <tr><td>用户登录</td><td>211</td><td>225</td><td>5.17</td><td>0%</td></tr>
        <tr><td>Token刷新</td><td>152</td><td>157</td><td>7.08</td><td>0%</td></tr>
        <tr><td>设备列表查询</td><td>43</td><td>45</td><td>28.51</td><td>0%</td></tr>
        <tr><td>站点数据查询</td><td>40</td><td>42</td><td>29.21</td><td>0%</td></tr>
        <tr><td>系统用户查询</td><td>40</td><td>41</td><td>29.46</td><td>0%</td></tr>"

    cat > "$REPORT_FILE" << HTMLEOF
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Elink-AI R4 性能基线报告</title>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; background: #f5f5f5; }
        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
        h2 { color: #34495e; margin-top: 30px; }
        table { border-collapse: collapse; width: 100%; margin: 15px 0; background: white; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        th, td { border: 1px solid #ddd; padding: 10px 12px; text-align: left; }
        th { background: #3498db; color: white; font-weight: 600; }
        tr:nth-child(even) { background: #f9f9f9; }
        tr:hover { background: #e8f4f8; }
        .summary { background: #eaf7ea; border-left: 4px solid #27ae60; padding: 15px; margin: 20px 0; }
        .threshold { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }
        .pass { color: #27ae60; font-weight: bold; }
        .fail { color: #e74c3c; font-weight: bold; }
        pre { font-size: 12px; white-space: pre-wrap; }
        .meta { color: #7f8c8d; font-size: 14px; }
    </style>
</head>
<body>
    <h1>Elink-AI R4 性能基线报告</h1>
    <p class="meta">生成时间：${DATE_STR} | 测试环境：158单机 | 测试工具：curl基准脚本（3轮中位数）</p>

    <div class="threshold">
        <strong>达标指标：</strong>
        API P95 ≤ 500ms | API P99 ≤ 1000ms | 错误率 ≤ 0.5% | JVM GC停顿 ≤ 100ms
    </div>

    <h2>1. API性能基线（3轮中位数）</h2>
    <table>
        <tr><th>场景</th><th>P95 (ms) ≤500</th><th>P99 (ms) ≤1000</th><th>TPS</th><th>错误率 ≤0.5%</th></tr>
        ${api_rows}
    </table>

    <h2>2. R1基线对比（2026-06-11 R1正式版）</h2>
    <table>
        <tr><th>场景</th><th>R1 P95(ms)</th><th>R1 P99(ms)</th><th>R1 TPS</th><th>R1 错误率</th></tr>
        ${r1_compare}
    </table>

    <h2>3. JVM GC指标（G1GC）</h2>
    <table>
        <tr><th>服务</th><th colspan="5">GC统计 (S0C S1C EC OC MC MTT YGC YGCT FGC FGCT GCT)</th></tr>
        ${gc_rows}
    </table>

    <h2>4. 容器资源占用</h2>
    <table>
        <tr><th>服务</th><th>内存占用</th><th>CPU%</th><th>网络IO</th><th>磁盘IO</th></tr>
        ${res_rows}
    </table>

    <div class="summary">
        <strong>测试结论：</strong>
        R4基线数据已采集完成，5个核心API端点各3轮压测数据已记录。
        与R1基线对比可评估PHASE-5优化效果。
    </div>
</body>
</html>
HTMLEOF
    log_info "HTML报告已生成: ${REPORT_FILE}"
}

# ====== 主流程 ======
main() {
    log_info "=========================================="
    log_info "P5-13 R4 性能基线测试开始"
    log_info "=========================================="

    get_token

    echo "scenario,round,requests,success,failures,error_rate,tps,min_ms,avg_ms,p50_ms,p90_ms,p95_ms,p99_ms,max_ms" \
        > "${RESULTS_DIR}/summary.csv"

    REQUESTS_PER_ROUND=300

    # 场景1: 用户登录
    for round in 1 2 3; do
        log_info "====== 场景1: 用户登录 - 第${round}轮 ======"
        local ENCRYPTED=$(echo -n "SunmaxZlx" | openssl enc -aes-128-cbc -K 73756e6d61786b657930353033303030 -iv 73756e6d61786976303530333030303030 -base64 2>/dev/null)
        run_benchmark "login" "${AUTH_DIRECT}/sauth/oauth/token" "POST_FORM" \
            "grant_type=sys_pwd&client_id=sunos-client&client_secret=sunos-client&userAccount=zlx&password=${ENCRYPTED}" \
            $REQUESTS_PER_ROUND $round
    done

    # 场景2: 设备列表查询
    get_token
    for round in 1 2 3; do
        log_info "====== 场景2: 设备列表查询 - 第${round}轮 ======"
        run_benchmark "device_list" "${GATEWAY}/device/deviceInfo/list" "GET" "" \
            $REQUESTS_PER_ROUND $round
    done

    # 场景3: 站点数据查询
    get_token
    for round in 1 2 3; do
        log_info "====== 场景3: 站点数据查询 - 第${round}轮 ======"
        run_benchmark "site_data" "${GATEWAY}/data/dataReport/list" "GET" "" \
            $REQUESTS_PER_ROUND $round
    done

    # 场景4: Token刷新
    for round in 1 2 3; do
        log_info "====== 场景4: Token刷新 - 第${round}轮 ======"
        run_benchmark "token_refresh" "${AUTH_DIRECT}/sauth/oauth/token" "POST_FORM" \
            "grant_type=refresh_token&client_id=sunos-client&client_secret=sunos-client&refresh_token=${REFRESH_TOKEN}" \
            $REQUESTS_PER_ROUND $round
        get_token
    done

    # 场景5: 系统用户查询
    get_token
    for round in 1 2 3; do
        log_info "====== 场景5: 系统用户查询 - 第${round}轮 ======"
        run_benchmark "user_list" "${GATEWAY}/system/user/list" "GET" "" \
            $REQUESTS_PER_ROUND $round
    done

    # 采集JVM GC
    log_info "采集JVM GC指标..."
    for svc in auth-service sunmax-gateway system-service device-service data-service; do
        collect_jvm_gc "$svc"
    done

    # 采集容器资源
    log_info "采集容器资源..."
    collect_container_resources

    # 计算中位数
    log_info "计算3轮中位数..."
    echo "scenario,p95,p99,tps,error_rate" > "${RESULTS_DIR}/median_results.csv"
    for scenario in login device_list site_data token_refresh user_list; do
        local r1_p95=$(grep "^${scenario},1," "${RESULTS_DIR}/summary.csv" | cut -d',' -f12)
        local r2_p95=$(grep "^${scenario},2," "${RESULTS_DIR}/summary.csv" | cut -d',' -f12)
        local r3_p95=$(grep "^${scenario},3," "${RESULTS_DIR}/summary.csv" | cut -d',' -f12)
        local r1_p99=$(grep "^${scenario},1," "${RESULTS_DIR}/summary.csv" | cut -d',' -f13)
        local r2_p99=$(grep "^${scenario},2," "${RESULTS_DIR}/summary.csv" | cut -d',' -f13)
        local r3_p99=$(grep "^${scenario},3," "${RESULTS_DIR}/summary.csv" | cut -d',' -f13)
        local r1_tps=$(grep "^${scenario},1," "${RESULTS_DIR}/summary.csv" | cut -d',' -f7)
        local r2_tps=$(grep "^${scenario},2," "${RESULTS_DIR}/summary.csv" | cut -d',' -f7)
        local r3_tps=$(grep "^${scenario},3," "${RESULTS_DIR}/summary.csv" | cut -d',' -f7)
        local r1_err=$(grep "^${scenario},1," "${RESULTS_DIR}/summary.csv" | cut -d',' -f6)
        local r2_err=$(grep "^${scenario},2," "${RESULTS_DIR}/summary.csv" | cut -d',' -f6)
        local r3_err=$(grep "^${scenario},3," "${RESULTS_DIR}/summary.csv" | cut -d',' -f6)

        local p95_median=$(echo -e "${r1_p95}\n${r2_p95}\n${r3_p95}" | sort -n | head -2 | tail -1)
        local p99_median=$(echo -e "${r1_p99}\n${r2_p99}\n${r3_p99}" | sort -n | head -2 | tail -1)
        local tps_median=$(echo -e "${r1_tps}\n${r2_tps}\n${r3_tps}" | sort -n | head -2 | tail -1)
        local err_median=$(echo -e "${r1_err}\n${r2_err}\n${r3_err}" | sort -n | head -2 | tail -1)

        log_info "场景[${scenario}] 中位数: P95=${p95_median}ms P99=${p99_median}ms TPS=${tps_median} 错误率=${err_median}%"
        echo "${scenario},${p95_median},${p99_median},${tps_median},${err_median}" >> "${RESULTS_DIR}/median_results.csv"
    done

    # 生成HTML报告
    log_info "生成HTML报告..."
    generate_html_report

    log_info "=========================================="
    log_info "P5-13 R4 性能基线测试完成"
    log_info "结果目录: ${RESULTS_DIR}"
    log_info "HTML报告: ${REPORT_FILE}"
    log_info "=========================================="

    echo ""
    echo "====== R4 性能基线汇总 ======"
    echo "场景,P95(ms),P99(ms),TPS,错误率(%)"
    cat "${RESULTS_DIR}/median_results.csv"
    echo ""
    echo "容器资源:"
    cat "${RESULTS_DIR}/container_resources.csv"
}

main "$@"
