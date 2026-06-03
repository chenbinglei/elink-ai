#!/bin/bash
set -e

NACOS_HOST=${NACOS_HOST:-127.0.0.1}
NACOS_PORT=${NACOS_PORT:-8848}

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

exec "$@"
