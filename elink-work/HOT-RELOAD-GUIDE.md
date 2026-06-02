# elink-work 热更新系统 v2.0

## 一、核心机制

| 机制 | 实现方式 | 说明 |
|------|---------|------|
| **热更新载体** | Volume Mount | `./<service>/target:/app/target:ro`，JAR 包从宿主机挂载，无需重建镜像 |
| **自动检测** | 文件修改时间轮询 | 守护进程每 5 秒检测 JAR 文件 mtime 变化，变化后自动触发热更新 |
| **三层健康验证** | Docker + HTTP + Nacos | ①容器健康检查状态 ②HTTP 端口响应 ③Nacos 服务注册 |
| **自动回滚** | 备份 + 恢复 | 热更新前自动备份当前 JAR，健康检查失败时恢复备份并重启 |
| **资源监控** | docker stats | 更新前后对比内存/CPU，超过 100MiB 增量告警内存泄漏 |
| **优雅关闭** | SERVER_SHUTDOWN=graceful | Spring Boot 2.3+ 优雅停机，30秒超时完成进行中请求 |

## 二、使用方式

### 1. 首次部署（带自动监控）

```bash
./start.sh --watch
```

此命令会：编译 → 构建镜像 → 启动所有服务 → 自动开启后台监控守护进程

### 2. 手动热更新

```bash
# 编译并热更新单个服务（完整流程）
./hot-reload.sh reload protocol-service

# 跳过编译，使用已有JAR热更新（最快）
./hot-reload.sh reload protocol-service skip

# 热更新所有服务
./hot-reload.sh all
```

### 3. 自动监控模式

```bash
# 后台监控所有服务（JAR变化自动触发更新）
./hot-reload.sh watch-start

# 后台仅监控特定服务
./hot-reload.sh watch-start protocol-service crontab-service

# 停止监控
./hot-reload.sh watch-stop

# 前台调试模式（直接看到实时日志）
./hot-reload.sh watch
```

### 4. 查看状态与监控

```bash
./hot-reload.sh status                          # 所有服务健康+注册状态
./hot-reload.sh status protocol-service         # 单服务状态
./hot-reload.sh monitor                         # 实时资源监控（所有服务）
./hot-reload.sh monitor protocol-service        # 单服务资源监控
./hot-reload.sh list                            # 列出服务+端口+状态
./hot-reload.sh history                         # 更新历史
./hot-reload.sh history protocol-service        # 单服务更新历史
```

### 5. 手动回滚

```bash
./hot-reload.sh rollback protocol-service
```

## 三、热更新完整7步流程

```
[1/7] 检查当前状态 → 记录资源快照
[2/7] 备份当前JAR → 保留最近5个版本
[3/7] Maven编译 → 记录构建日志
[4/7] 重启容器 → docker-compose --force-recreate
[5/7] 三层健康验证 → 容器健康 + HTTP端口 + Nacos注册
      ↓ 失败
      → 自动恢复备份JAR → 再次重启 → 验证回滚结果
[6/7] 资源监控 60s → 每10秒采样，前后对比内存/CPU
[7/7] Nacos注册验证 → 确认服务可被其他服务发现
```

## 四、服务端口映射

| 服务 | 端口 | Nacos服务名 |
|------|------|-------------|
| auth-service | 60001 | sauth-service |
| sunmax-gateway | 5000 | sunmax-gateway |
| system-service | 60002 | system-service |
| device-service | 60003 | device-service |
| data-service | 60004 | sunos-data-service |
| protocol-service | 60005 | sunos-protocol-service |
| crontab-service | 60006 | scrontab-service |
| devops-service | 60007 | devops-service |
| configure-service | 60008 | configure-service |
| together-service | 60009 | together-service |
| webapp-service | 60010 | swebapp-service |

## 五、服务启动依赖

```
Redis ──→ auth-service ──→ system-service ──→ device-service ──→ protocol-service
EMQX1 ──→ configure-service                                └→ (等待Nacos注册)
Nacos ──→ sunmax-gateway ──→ data-service ──→ together-service ──→ crontab-service
          devops-service                                   └→ (等待Nacos注册)
          webapp-service
```

- **protocol-service**：等待 device-service 在 Nacos 注册成功后才启动
- **crontab-service**：等待 together-service 在 Nacos 注册成功后才启动

## 六、日志与备份

| 路径 | 说明 |
|------|------|
| `backups/` | JAR 备份文件（`<service>_<jar>.<timestamp>`） |
| `logs/reload-history.log` | 所有热更新操作历史 |
| `logs/watch-daemon.log` | 后台监控守护进程日志 |
| `logs/build-<service>.log` | 各服务构建日志 |

## 七、关键文件

| 文件 | 说明 |
|------|------|
| `docker-compose.yml` | 全量服务编排 + 健康检查 + 优雅关闭 |
| `Dockerfile` | 基础镜像 (含curl + entrypoint) |
| `docker-entrypoint.sh` | 容器入口 (Nacos等待逻辑) |
| `hot-reload.sh` | 热更新核心脚本 |
| `start.sh` | 一键部署脚本 (支持 `--watch`) |

## 八、管理控制台

| 服务 | 地址 |
|------|------|
| API网关 | http://192.168.2.158:5000 |
| Nacos控制台 | http://192.168.2.158:8848/nacos |
| EMQX1控制台 | http://192.168.2.158:18083 (admin/public) |
| EMQX2控制台 | http://192.168.2.158:28083 (admin/public) |
