#!/bin/bash
set -e

NACOS_HOST=${NACOS_HOST:-127.0.0.1}
NACOS_PORT=${NACOS_PORT:-8848}

# 健康检查失败自动退出参数
HEALTH_CHECK_FAIL_LIMIT=${HEALTH_CHECK_FAIL_LIMIT:-4}
HEALTH_CHECK_INTERVAL=${HEALTH_CHECK_INTERVAL:-10}
HEALTH_CHECK_CONTEXT=${HEALTH_CHECK_CONTEXT:-}

# 确保日志目录可写
mkdir -p /app/logs 2>/dev/null || true

if [ -f /usr/lib/libtaos.so.1 ] && [ ! -L /usr/lib/libtaos.so ]; then
    ln -s /usr/lib/libtaos.so.1 /usr/lib/libtaos.so 2>/dev/null || true
fi

wait_for_nacos_registration() {
    local service_name=$1
    local max_retries=${2:-60}
    local retry=0

    while [ $retry -lt $max_retries ]; do
        if curl -sf "http://${NACOS_HOST}:${NACOS_PORT}/nacos/v1/ns/instance/list?serviceName=${service_name}" 2>/dev/null | grep -q '"healthy":true'; then
            echo "[INFO] Service ${service_name} is registered and healthy in Nacos!"
            return 0
        fi
        retry=$((retry + 1))
        echo "[WARN] Waiting for ${service_name} to register in Nacos... (${retry}/${max_retries})"
        sleep 2
    done

    echo "[ERROR] Service ${service_name} did not register in Nacos within timeout!"
    return 1
}

if [ -n "${WAIT_FOR_SERVICE}" ]; then
    echo "[INFO] Waiting for dependent service '${WAIT_FOR_SERVICE}' to register in Nacos..."
    wait_for_nacos_registration "${WAIT_FOR_SERVICE}" "${WAIT_MAX_RETRIES:-60}" || exit 1
fi

# 启动 Java 进程（后台运行，以便监控健康检查）
"$@" &
JAVA_PID=$!

# 健康检查监控：连续失败超过阈值时主动终止 Java 进程
# 这样容器以非零退出码退出，触发 docker restart: on-failure:3 策略
if [ -n "${HEALTH_CHECK_PORT}" ]; then
    echo "[INFO] Health check monitor enabled on port ${HEALTH_CHECK_PORT}, context: ${HEALTH_CHECK_CONTEXT:-/}, fail limit: ${HEALTH_CHECK_FAIL_LIMIT}"

    (
        # 等待 start_period 对应的时间后再开始监控
        sleep ${HEALTH_CHECK_START_DELAY:-50}
        fail_count=0
        while kill -0 "$JAVA_PID" 2>/dev/null; do
            if curl -sf "http://localhost:${HEALTH_CHECK_PORT}${HEALTH_CHECK_CONTEXT}/actuator/health" > /dev/null 2>&1; then
                fail_count=0
            else
                fail_count=$((fail_count + 1))
                echo "[WARN] Health check failed (${fail_count}/${HEALTH_CHECK_FAIL_LIMIT})"
            fi

            if [ "$fail_count" -ge "$HEALTH_CHECK_FAIL_LIMIT" ]; then
                echo "[ERROR] Health check failed ${HEALTH_CHECK_FAIL_LIMIT} times, terminating Java process (PID: ${JAVA_PID})"
                kill -TERM "$JAVA_PID"
                sleep 3
                kill -9 "$JAVA_PID" 2>/dev/null || true
                exit 1
            fi
            sleep "$HEALTH_CHECK_INTERVAL"
        done
    ) &
    MONITOR_PID=$!
fi

# 等待 Java 进程退出
wait "$JAVA_PID"
EXIT_CODE=$?

# 清理监控进程
if [ -n "${MONITOR_PID}" ]; then
    kill "$MONITOR_PID" 2>/dev/null || true
fi

exit $EXIT_CODE
