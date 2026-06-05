# Elink-AI CI/CD 执行流程规范

> 版本：v1.0 | 创建日期：2026-06-05 | 状态：初始版本

---

## 一、Git分支管理策略

### 1.1 分支结构

| 分支 | 用途 | 命名规范 | 保护级别 |
|------|------|----------|----------|
| `main` | 生产就绪代码，仅通过PR合并 | `main` | 最高（强制review） |
| `develop` | 开发集成分支，日常开发基准 | `develop` | 高 |
| `refactor/*` | 重构任务专属分支 | `refactor/<phase>-<description>` | 中 |
| `feature/*` | 功能开发分支 | `feature/<ticket>-<description>` | 低 |
| `hotfix/*` | 紧急修复分支 | `hotfix/<ticket>-<description>` | 中 |

### 1.2 分支生命周期

```
main ───────────────────────────────────────────── tag: v3.0-phase1
  └── develop ──────────────────────────────────── 常驻分支
       ├── refactor/phase-1-security-hardening ─── 合并后保留（打tag后可删除）
       ├── refactor/phase-2-framework-upgrade ──── 下一个任务分支
       └── feature/P2-xxx-xxx ─────────────────── 短生命周期，完成后合并删除
```

### 1.3 当前分支状态

| 分支 | 状态 | Tag | 说明 |
|------|------|-----|------|
| `main` | 生产基线 | - | PHASE-1变更待合并 |
| `develop` | 已推送远程 | - | 开发集成基准 |
| `refactor/phase-1-security-hardening` | 已推送远程 | `v3.0-phase1` | PHASE-1完成标记 |

---

## 二、任务执行流程（PRE → TEST → GRAY）

### 2.1 PRE 阶段（前置检查）

**触发条件**：开始任何重构任务前

```bash
# 1. 确认工作区干净
git status --short  # 应返回空

# 2. 创建任务分支
git checkout develop
git pull origin develop
git checkout -b refactor/phase-N-description

# 3. 确认分支基于最新develop
git log --oneline -3
```

**检查清单：**
- [ ] 工作区无未提交变更
- [ ] 当前分支正确
- [ ] 数据库已备份（跨环境变更时）
- [ ] .env配置正确

### 2.2 EXEC 阶段（执行变更）

**流程：**
1. 修改代码/配置
2. 本地编译验证：`mvn clean compile -DskipTests -T 4`
3. 单元测试（如有）：`mvn test -pl <module>`
4. 提交变更（遵循Git提交规范）

**提交规范：**
```
<type>([技术栈]/<模块>): <描述>

type: feat|fix|docs|style|refactor|test|chore
技术栈: [java]|[vue]|[sql]|[config]
模块: auth/system/device/data/protocol等

示例：
refactor([java]/auth): 将OAuth2 client-secret外置为环境变量
docs([config]/全局): 同步修复文档版本和数据不一致
```

### 2.3 TEST 阶段（测试验证）

**编译验证：**
```bash
cd /work/elink-ai/elink-work
mvn clean package -DskipTests -T 4
```

**三层健康验证：**
```bash
# Layer 1: Docker Health
docker ps --format "table {{.Names}}\t{{.Status}}"

# Layer 2: HTTP端口
for port in 5000 60001 60002 60003 60004 60005 60006 60007 60008 60009 60010; do
  echo -n "Port $port: "
  curl -s -o /dev/null -w "%{http_code}" http://192.168.2.158:$port/actuator/health --max-time 5
  echo ""
done

# Layer 3: Nacos注册
curl -s "http://192.168.2.158:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=50" | python3 -m json.tool
```

**功能验证：**
- 核心业务接口响应验证（参照R1基线数据）
- 安全项残留检查（硬编码IP/密钥/fastjson残留等）

### 2.4 GRAY 阶段（灰度发布）

**单服务灰度：**
```bash
cd /work/elink-ai/elink-work
./hot-reload.sh reload <service-name>
```

**全量更新：**
```bash
./hot-reload.sh all
```

**回滚：**
```bash
# 自动回滚（hot-reload失败时触发）
./hot-reload.sh rollback <service-name>
```

---

## 三、回滚机制

### 3.1 触发条件

| 级别 | 条件 | 操作 |
|------|------|------|
| P0 | 服务无法启动（连续失败3次） | 立即回滚JAR + 重启容器 |
| P1 | 核心接口响应异常（5xx > 10%） | 5分钟内评估，否则回滚 |
| P2 | 性能劣化超过基准30% | 10分钟内评估，否则回滚 |
| P3 | 非核心功能异常 | 记录问题，下一迭代修复 |

### 3.2 回滚操作流程

```bash
# 1. 通过hot-reload.sh自动回滚
./hot-reload.sh rollback <service>

# 2. 手动回滚（如果自动回滚失败）
cd /work/elink-ai/elink-work/backups
ls -lt  # 找到最近备份
cp <backup-jar> ../<service>/target/<service>-exec.jar
docker-compose up -d --no-build --force-recreate <service>

# 3. Git代码回滚
git revert <commit-hash>
git push origin refactor/phase-N-description
```

### 3.3 备份保留策略

- JAR包备份：最近5个版本（hot-reload.sh自动管理）
- 数据库备份：每个Phase开始前全量备份（手动管理）
- Git Tag：每个Phase完成后打tag永久保留

---

## 四、环境变量管理

### 4.1 环境分层

| 变量类别 | 开发环境 | 测试环境 | 生产环境 |
|----------|----------|----------|----------|
| .env文件 | `.env.local` | `.env.staging` | `.env.production` |
| NACOS_HOST | 192.168.2.158 | 内网IP | 内网IP |
| MYSQL_* | 本地MySQL | 测试MySQL | 生产MySQL |
| REDIS_PASSWORD | 123456 | 测试密码 | 强密码 |
| 安全相关 | 可简化 | 与生产一致 | 严格配置 |

### 4.2 敏感变量清单

以下变量必须通过环境变量注入，禁止硬编码：
- `MYSQL_PASSWORD` / `REDIS_PASSWORD`
- `OAUTH2_CLIENT_SECRET`
- `PLATFORM_SIGN_KEY` / `PLATFORM_APP_KEY` / `PLATFORM_SECRET_KEY`
- `OSS_ACCESS_KEY_ID` / `OSS_ACCESS_KEY_SECRET`

---

## 五、重构任务执行检查清单

每个任务必须完成以下全部步骤才算完成：

### 5.1 代码变更
- [ ] 创建/切换到正确的任务分支
- [ ] 代码修改完成并通过本地编译
- [ ] 安全残留检查通过（grep验证）
- [ ] Git提交信息符合规范

### 5.2 部署验证
- [ ] 全量编译 BUILD SUCCESS
- [ ] JAR包完整性检查
- [ ] 三层健康验证通过
- [ ] 核心业务接口验证通过

### 5.3 文档更新（4份必须全部更新）
- [ ] `REFACTOR_TASKS.md` — 标记状态+完成时间+进度总览
- [ ] `REFACTOR_EXECUTE.md` — 新增执行记录
- [ ] `PROGRESS_REPORT.md` — 更新进度+详情+修改记录
- [ ] `REFACTOR_PLAN.md` — 更新进度总览+任务状态

### 5.4 归档
- [ ] 创建Git Tag
- [ ] 推送到远程仓库
