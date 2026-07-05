#!/usr/bin/env python3.10
# -*- coding: utf-8 -*-
"""
Prometheus Token 刷新脚本

功能：
  1. 使用 prom_monitor 专用监控账号登录 auth-service
  2. 获取 JWT access_token
  3. 原子写入 token 文件供 Prometheus bearer_token_file 读取

依赖：
  - cryptography（AES-CBC 加密）
  - 仅需 Python 3.10+ 标准库 + cryptography

部署：
  crontab -e
  0 3 * * * /usr/bin/python3.10 /work/elink-ai/elink-work/scripts/refresh_prometheus_token.py >> /work/elink-ai/elink-work/logs/refresh_token.log 2>&1
"""

import base64
import json
import os
import os.path
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import padding

# ============== 配置 ==============
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_DIR = os.path.dirname(SCRIPT_DIR)
ENV_FILE = os.path.join(PROJECT_DIR, ".env")
TOKEN_FILE = os.path.join(PROJECT_DIR, "prometheus", "prometheus_token")
TOKEN_FILE_TMP = TOKEN_FILE + ".tmp"

# 从环境变量读取配置（支持 .env 文件和容器环境变量）
AUTH_URL = os.environ.get("AUTH_SERVICE_URL", "http://192.168.2.158:60001")
CLIENT_ID = os.environ.get("PROM_CLIENT_ID", "sunos-client")
MONITOR_USER = os.environ.get("PROM_MONITOR_USER", "prom_monitor")
MONITOR_PASSWORD = os.environ.get("PROM_MONITOR_PASSWORD", "")

# AES-CBC 加密参数（与后端 SecretUtil 一致）
AES_KEY = b"sunmaxkey0503000"
AES_IV = b"sunmaxiv05030000"


def load_env_file(filepath):
    """从 .env 文件加载环境变量（不覆盖已存在的环境变量）"""
    if not os.path.exists(filepath):
        return
    with open(filepath, "r") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            if "=" not in line:
                continue
            key, _, value = line.partition("=")
            key = key.strip()
            value = value.strip()
            if key and key not in os.environ:
                os.environ[key] = value


def encrypt_password(plain):
    """AES-CBC 加密 + Base64（与前端加密逻辑一致）"""
    padder = padding.PKCS7(128).padder()
    padded = padder.update(plain.encode("utf-8")) + padder.finalize()
    cipher = Cipher(algorithms.AES(AES_KEY), modes.CBC(AES_IV))
    encryptor = cipher.encryptor()
    encrypted = encryptor.update(padded) + encryptor.finalize()
    return base64.b64encode(encrypted).decode("utf-8")


def login():
    """登录 auth-service 获取 access_token"""
    if not MONITOR_PASSWORD:
        print("ERROR: PROM_MONITOR_PASSWORD 未设置", file=sys.stderr)
        return None

    encrypted_pwd = encrypt_password(MONITOR_PASSWORD)
    encoded_pwd = urllib.parse.quote(encrypted_pwd, safe="")
    post_data = (
        f"grant_type=sys_pwd&client_id={CLIENT_ID}"
        f"&userAccount={MONITOR_USER}&password={encoded_pwd}"
    ).encode("utf-8")

    url = f"{AUTH_URL}/sauth/oauth/token"
    req = urllib.request.Request(
        url,
        data=post_data,
        headers={"Content-Type": "application/x-www-form-urlencoded"},
        method="POST",
    )

    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            body = json.loads(resp.read().decode("utf-8"))
            if body.get("code") in (0, 20000):
                token = body.get("data", {}).get("accessToken")
                if token:
                    return token
                print("ERROR: 响应中无 accessToken", file=sys.stderr)
                return None
            print(f"ERROR: 登录失败 code={body.get('code')}, msg={body.get('message')}", file=sys.stderr)
            return None
    except urllib.error.URLError as e:
        print(f"ERROR: 请求 auth-service 失败: {e}", file=sys.stderr)
        return None
    except Exception as e:
        print(f"ERROR: 登录异常: {e}", file=sys.stderr)
        return None


def write_token_file(token):
    """原子写入 token 文件（先写临时文件再 rename，避免 Prometheus 读到半写文件）"""
    os.makedirs(os.path.dirname(TOKEN_FILE), exist_ok=True)
    with open(TOKEN_FILE_TMP, "w") as f:
        f.write(token)
    os.chmod(TOKEN_FILE_TMP, 0o644)
    os.rename(TOKEN_FILE_TMP, TOKEN_FILE)
    print(f"Token 已写入: {TOKEN_FILE} (长度={len(token)})")


def main():
    print(f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] 开始刷新 Prometheus token...")

    # 加载 .env 文件
    global MONITOR_PASSWORD, AUTH_URL, CLIENT_ID, MONITOR_USER
    load_env_file(ENV_FILE)
    MONITOR_PASSWORD = os.environ.get("PROM_MONITOR_PASSWORD", MONITOR_PASSWORD)
    AUTH_URL = os.environ.get("AUTH_SERVICE_URL", AUTH_URL)
    CLIENT_ID = os.environ.get("PROM_CLIENT_ID", CLIENT_ID)
    MONITOR_USER = os.environ.get("PROM_MONITOR_USER", MONITOR_USER)

    # 登录获取 token
    token = login()
    if not token:
        print("FAILED: 获取 token 失败")
        return 1

    # 写入文件
    write_token_file(token)
    print("SUCCESS: Prometheus token 刷新完成")
    return 0


if __name__ == "__main__":
    sys.exit(main())
