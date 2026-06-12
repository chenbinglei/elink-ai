---
alwaysApply: true
scene: docker_ops
version: "1.1.0"
lastUpdated: "2026-06-03"
changelog:
  - version: "1.0.0"
    date: "2026-06-03"
    changes: "初始版本"
  - version: "1.1.0"
    date: "2026-06-03"
    changes: "集成至智能体系统，添加场景元数据和迭代追踪"
---

# Elink-AI Docker 容器化运维规则

当执行 Docker 容器管理、服务部署、热更新操作时，必须严格遵循以下规则。

## 1. 【热更新操作规范】

### 前置检查
- 确认 .env 文件存在且配置正确
- 确认 Nacos 服务健康（curl http://$NACOS_HOST:8848/nacos/）
- 确认 Redis 服务健康
- 确认磁盘空间充足（JAR备份需要空间）

### 热更新流程（hot-reload.sh）
1. 备份当前JAR（自动保留最近5个版本）
2. Maven构建（`mvn package -pl <service> -am -DskipTests -T 4`）
3. Docker容器重启（`docker-compose up -d --no-build --force-recreate <service>`）
4. 三层健康验证：Docker Health + HTTP端口 + Nacos注册
5. 资源监控（10秒观察期，检测内存泄漏）
6. 失败自动回滚（恢复上一备份JAR）

### 灰度发布规范
- 单服务更新：`./hot-reload.sh reload <service>`
- 全量更新：`./hot-reload.sh all`（按依赖顺序）
- 跳过构建：`./hot-reload.sh reload <service> skip`
- 手动回滚：`./hot-reload.sh rollback <service>`

## 2. 【服务启动顺序】

```
基础设施层：redis → nacos → emqx1/emqx2
核心服务层：auth-service → sunmax-gateway → system-service
业务服务层：device-service → data-service → configure-service
依赖服务层：protocol-service(依赖device-service) → together-service
定时/运维层：crontab-service(依赖together-service) → devops-service → webapp-service
```

## 3. 【容器资源限制建议】

当前所有服务均未设置 deploy.resources.limits，建议添加：

| 服务 | CPU限制 | 内存限制 | 说明 |
|------|---------|---------|------|
| auth-service | 1 | 1G | 认证服务，内存需求中等 |
| sunmax-gateway | 0.5 | 512M | 网关，当前-Xms256m |
| system-service | 1 | 1G | 系统管理 |
| device-service | 1 | 1G | 设备管理 |
| data-service | 2 | 2G | 数据服务+TDengine JNI |
| protocol-service | 1 | 1G | 协议服务+MQTT |
| crontab-service | 1 | 1G | 定时任务+TDengine JNI |
| devops-service | 0.5 | 768M | 运维服务 |
| configure-service | 0.5 | 768M | 配置服务+MQTT |
| together-service | 1 | 1G | 业务聚合 |
| webapp-service | 0.5 | 768M | Web应用 |

## 4. 【健康检查参数】

| 参数 | 当前值 | 建议值 | 说明 |
|------|--------|--------|------|
| interval | 5s | 10s | 检查间隔，5s过于频繁 |
| timeout | 3s | 5s | 超时时间 |
| retries | 6-8 | 3-5 | 重试次数 |
| start_period | 20-40s | 30-60s | 启动等待期，Spring Boot需更长时间 |

## 5. 【JVM参数规范】

当前所有服务使用 `-XX:+UseParallelGC`，建议：
- 网关/轻量服务：`-Xms256m -Xmx256m -XX:+UseSerialGC`
- 业务服务：`-Xms512m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200`
- 数据服务（TDengine）：`-Xms1g -Xmx1g -XX:+UseG1GC`
- 通用：添加 `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/`

## 6. 【Docker Compose 已知问题】

1. **crontab-service JAR名拼写**：docker-compose.yml中为 `scrontab-service-exec.jar`，应为 `crontab-service-exec.jar`
2. **emqx1/emqx2 未配置集群**：两个EMQX实例独立运行，未形成集群
3. **Nacos 未配置持久化**：standalone模式，数据存储在内嵌Derby，容器重启丢失
4. **Redis 未配置持久化策略**：appendonly yes 但未配置 appendfsync
5. **所有服务共享同一基础镜像**：elink-base:latest，无版本锁定
6. **env_file 使用绝对路径**：`/work/elink-ai/.env`，部署到其他机器需修改
7. **data-service/crontab-service 挂载宿主机TDengine库**：强耦合宿主机环境
