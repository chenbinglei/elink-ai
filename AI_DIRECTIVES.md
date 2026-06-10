# Elink-AI 重构升级优化 — AI 分阶段执行指令集

> 版本：v2.1 | 编制日期：2026-06-05 | 最后更新：2026-06-10 | 状态：**待执行**
>
> 本文档基于 REFACTOR_PLAN.md v1.3、REFACTOR_TASKS.md、REFACTOR_EXECUTE.md v1.6、PROGRESS_REPORT.md v1.4 以及全面审查结论编制。
> 每条指令可直接放入 AI 对话框执行，AI 按指令完成操作后自行核对验收标准。

---

## 全局约束（适用于所有阶段）

1. **禁止跳步升级**：Spring Boot 2.3 → 2.7 → 3.3 是不可跳过的路径，严禁直接 2.3 → 3.x
2. **每步必验**：每个指令执行完毕后必须运行编译验证 `mvn clean compile -DskipTests -T 4`，编译不通过不得进入下一指令
3. **文档同步**：每完成一条指令，必须按 DOC_UPDATE_GUIDELINES.md 同步更新 REFACTOR_TASKS.md、REFACTOR_EXECUTE.md、PROGRESS_REPORT.md、REFACTOR_PLAN.md 四份文档
4. **Git 提交**：每条指令完成后独立提交，提交信息遵循 git-commit-message 规则
5. **环境变量优先**：所有凭证、IP、密钥必须使用 `${ENV_VAR:default}` 格式，生产部署时 default 值必须被覆盖
6. **回滚阈值**：编译失败 → 立即回退代码；服务启动失败 → 执行 `./hot-reload.sh rollback <service>`；接口错误率 >5% → 评估回滚
7. **本地服务器验证**：所有修改必须在本地测试服务器（192.168.2.158）上完成启动验证，确认端口监听、基础服务响应正常。验证步骤：
   ```bash
   # 检查服务器连通性
   ping -c 3 192.168.2.158
   # 检查关键端口监听（Nacos/Redis/MySQL/Gateway/各微服务）
   ssh root@192.168.2.158 "ss -tlnp | grep -E '8848|6379|3306|5000|60001|60002|60003|60004|60005|60006|60007|60008|60009|60010'"
   # 检查 Nacos 服务注册状态
   curl -s http://192.168.2.158:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=20 | python3 -m json.tool
   ```
8. **变更文件清单**：每条指令执行时必须明确标识所有涉及变更的前后端文件，并在执行结束后产出完整变更文件清单写入 REFACTOR_EXECUTE.md 对应记录
9. **热更新验证闭环**：修改文件后必须通过热更新脚本部署并验证修改生效，禁止仅依赖编译通过作为唯一验收标准
10. **功能一致性验证**：每条指令完成后，必须对受影响的功能进行冒烟测试，确认无功能退化或异常
11. **执行后强制检查清单**：每条指令执行完毕后，AI 必须逐项完成以下检查并记录结果到 REFACTOR_EXECUTE.md。**禁止跳过任何一项**：
    ```bash
    # ┌───────────────────────────────────────────────┐
    # │  执行后强制检查清单（AI 必须逐项完成并记录）     │
    # └───────────────────────────────────────────────┘

    # [A] 编译验证
    #   执行: mvn clean compile -DskipTests -T 4
    #   预期: BUILD SUCCESS
    #   检查命令:
    mvn clean compile -DskipTests -T 4 2>&1 | tail -5

    # [B] 本地服务器验证
    #   执行: ./hot-reload.sh reload <service> （逐服务）
    #   预期: 三层健康验证通过（Docker healthy + HTTP UP + Nacos 注册正常）
    #   检查命令:
    ssh root@192.168.2.158 "docker inspect --format='{{.State.Health.Status}}' <service>"
    curl -s http://192.168.2.158:<port>/<context>/actuator/health
    curl -s http://192.168.2.158:8848/nacos/v1/ns/instance/list?serviceName=<service>

    # [C] 热更新验证
    #   执行: ./hot-reload.sh status [<service>]
    #   预期: 所有受影响的返回 healthy
    #   检查命令:
    ./hot-reload.sh status <service>

    # [D] 功能一致性验证
    #   执行: 针对受影响功能的冒烟测试
    #   预期: 核心业务功能正常运行，无功能退化
    #   检查命令:
    curl -s http://192.168.2.158:5000/sauth/actuator/health
    curl -X POST http://192.168.2.158:60001/sauth/oauth/token -d "..."

    # [E] Git 提交
    #   执行: 每条指令独立提交，遵循 git-commit-message 规则
    #   预期: 工作区干净，提交信息符合规范
    #   检查命令:
    git status --short
    git log --oneline -3

    # [F] 文档同步
    #   执行: 更新 REFACTOR_TASKS.md / REFACTOR_EXECUTE.md / PROGRESS_REPORT.md / REFACTOR_PLAN.md
    #   预期: 4份文档全部更新，版本号递增
    #   检查命令:
    grep -n '最后更新' REFACTOR_TASKS.md
    grep -n '版本：' REFACTOR_EXECUTE.md
    grep -n '版本：' PROGRESS_REPORT.md
    grep -n '版本：' REFACTOR_PLAN.md
    ```
12. **Git 提交流程规范**：
    - **提交时机**：每条指令执行完毕且核对完成标识后，必须立即提交
    - **提交粒度**：每条指令独立提交，禁止多条指令合并提交
    - **提交信息**：严格遵循 `.trae/rules/git-commit-message.md` 规范，格式为 `<type>(<scope>): <subject>`
    - **提交前检查**：`git status` 确认只包含当前指令的变更文件，无无关文件混入
    - **提交后验证**：`git log --oneline -3` 确认提交记录正确
    - **分支策略**：按照 BRANCH_MANIFEST.md 规定的分支执行，每个阶段使用独立分支
13. **文档同步强制规则**（覆盖全局约束 3）：
    - 每条指令完成后，AI 必须执行 `doc-update-enforcement.md` 中的更新检查清单
    - 4 份文档（REFACTOR_TASKS.md / REFACTOR_EXECUTE.md / PROGRESS_REPORT.md / REFACTOR_PLAN.md）全部更新后方可进入下一指令
    - 文档版本号递增：次版本号 +1（如 v1.2 → v1.3）

---

## 热更新执行规范

### 后端热更新（hot-reload.sh）

- **脚本路径**：`/work/elink-ai/elink-work/hot-reload.sh`
- **使用方式**：
  ```bash
  cd /work/elink-ai/elink-work

  # 单服务热更新（自动构建+重启+健康验证）
  ./hot-reload.sh reload <service-name>

  # 跳过构建（仅重启已有JAR）
  ./hot-reload.sh reload <service-name> skip

  # 手动回滚
  ./hot-reload.sh rollback <service-name>

  # 查看服务状态
  ./hot-reload.sh status <service-name>

  # 全量更新（按依赖顺序）
  ./hot-reload.sh all
  ```
- **服务启动顺序**（严格遵守）：
  ```
  基础设施层：redis → nacos → emqx1/emqx2
  核心服务层：auth-service → sunmax-gateway → system-service
  业务服务层：device-service → data-service → configure-service
  依赖服务层：protocol-service → together-service
  定时/运维层：crontab-service → devops-service → webapp-service
  ```
- **三层健康验证**（hot-reload.sh 自动执行）：
  1. Docker 容器健康状态（healthy）
  2. HTTP 端点 `/actuator/health` 响应正常
  3. Nacos 注册实例 healthy=true
- **回滚机制**：健康验证失败时自动回滚到上一个备份 JAR

### 前端热更新（dev-manager.js）

- **脚本路径**：`/work/elink-ai/elink-web/dev-manager.js`
- **配置文件**：`/work/elink-ai/elink-web/dev.config.js`
- **项目端口映射**：
  | 项目 | 端口 | 类型 | 开发命令 |
  |------|------|------|---------|
  | linkos | 9000 | vue-cli | npm run serve |
  | derms | 9001 | vite | npm run dev |
  | tycvs | 9002 | vue-cli | npm run serve |
- **使用方式**：
  ```bash
  cd /work/elink-ai/elink-web

  # 启动全部前端项目
  node dev-manager.js start-all

  # 单项目操作
  node dev-manager.js start linkos
  node dev-manager.js restart derms
  node dev-manager.js stop tycvs

  # 查看运行状态
  node dev-manager.js status

  # 查看日志
  node dev-manager.js logs linkos
  ```
- **环境变量**：前端项目通过 `elink-web/<project>/.env` 加载（含敏感值，不提交），模板为 `elink-web/<project>/.env.example`（可提交）；后端统一由 `elink-work/.env` 作为变量源

### 集成测试验证模板

每条指令完成后须执行以下验证并记录结果：

```bash
# 1. 后端编译验证
cd /work/elink-ai/elink-work && mvn clean compile -DskipTests -T 4

# 2. 后端热更新部署验证（替换<service>为实际服务名）
./hot-reload.sh reload <service>

# 3. 前端构建验证（如涉及前端变更）
cd /work/elink-ai/elink-web && node dev-manager.js restart <project>

# 4. 端口监听验证
ssh root@192.168.2.158 "ss -tlnp | grep -E '<port>'"

# 5. 核心功能冒烟测试
# 5a. Gateway 路由测试
curl -sf http://192.168.2.158:5000/<context>/actuator/health
# 5b. 认证接口测试
curl -sf -X POST http://192.168.2.158:60001/sauth/oauth/token -d "grant_type=password&username=admin&password=admin&client_id=sunos&client_secret=${OAUTH2_CLIENT_SECRET}"
# 5c. 设备列表接口测试（需先获取token）
curl -sf -H "Authorization: Bearer <token>" http://192.168.2.158:5000/device/device/site/list

# 6. 记录测试结果到REFACTOR_EXECUTE.md
```

---

## 阶段依赖关系图

```
PHASE-0（紧急修复与数据校正）
  │
  ▼
PHASE-1（安全加固补偿任务）
  │
  ▼
PHASE-2（框架升级与核心重构）── 严禁跳步
  │   P2-2a → P2-2b → P2-2c → P2-2c-2 → P2-2c-3 → P2-2c-4
  ▼
PHASE-3（代码质量与性能优化）
  │   P3-A / P3-B / P3-C / P3-C2 可并行，P3-D 在最后
  ▼
PHASE-4（前端现代化改造）
  │   P4-A → P4-BC → P4-D
  ▼
PHASE-5（构建部署与持续优化）
      P5-A / P5-B / P5-C 可并行，P5-D 在最后
```

---

# ═══════════════════════════════════════════════════════════
# PHASE-0：紧急修复与数据校正
# ═══════════════════════════════════════════════════════════

> **阶段目标**：修复审查中发现的生产环境紧迫风险，校正文档数据不一致问题，为后续阶段奠定可信基线。
>
> **前置条件**：无（可立即执行）
>
> **衔接关系**：PHASE-0 完成后才能启动 PHASE-1 补偿任务和 PHASE-2 框架升级。

---

## P0-1 | 为所有 Docker 容器添加资源限制

**阶段目标与核心任务：**
消除当前全部服务容器无内存/CPU限制的运维风险，防止单服务故障耗尽宿主机资源。

**具体执行步骤与操作要求：**

1. 编辑 `/work/elink-ai/elink-work/docker-compose.yml`，为每个服务添加 `deploy.resources.limits` 配置
2. 参照 docker-ops 规则中的资源矩阵：
   - auth-service: CPU 1, 内存 1G
   - sunmax-gateway: CPU 0.5, 内存 512M
   - system-service: CPU 1, 内存 1G
   - device-service: CPU 1, 内存 1G
   - data-service: CPU 2, 内存 2G
   - protocol-service: CPU 1, 内存 1G
   - crontab-service: CPU 1, 内存 1G
   - devops-service: CPU 0.5, 内存 768M
   - configure-service: CPU 0.5, 内存 768M
   - together-service: CPU 1, 内存 1G
   - webapp-service: CPU 0.5, 内存 768M
   - redis: CPU 0.5, 内存 512M
   - nacos: CPU 1, 内存 1G
   - emqx1/emqx2: CPU 1, 内存 512M
3. 同步调整 healthcheck 参数：interval 改为 10s，timeout 改为 5s，retries 改为 3-5，start_period 改为 30-60s

**输入输出规范：**
- 输入：当前 docker-compose.yml
- 输出：更新后的 docker-compose.yml，包含所有服务的 deploy.resources.limits 和优化后的 healthcheck

**变更文件清单：**
- `/work/elink-ai/elink-work/docker-compose.yml`（添加 deploy.resources.limits + 优化 healthcheck）

**质量验收标准：**
- [ ] `grep -c 'deploy:' docker-compose.yml` 返回值等于服务总数（14）
- [ ] `grep -c 'limits:' docker-compose.yml` 返回值等于服务总数
- [ ] 无任何服务缺少 memory 或 cpus 限制
- [ ] healthcheck 参数均在建议范围内（interval≥10s, timeout≥5s, retries 3-5, start_period 30-60s）
- [ ] 本地服务器验证：执行 `ssh root@192.168.2.158 "docker-compose -f /work/elink-ai/elink-work/docker-compose.yml config"` 验证配置语法无误
- [ ] 热更新验证：执行 `./hot-reload.sh status` 确认所有服务在资源限制下的运行状态，至少 auth-service、sunmax-gateway、system-service 三个核心服务返回 healthy
- [ ] 功能一致性：通过 Gateway 访问 `http://192.168.2.158:5000/sauth/actuator/health` 和 `http://192.168.2.158:5000/system/actuator/health` 响应正常

---

## P0-2 | 修正 CORS 白名单中的内网 IP

**阶段目标与核心任务：**
移除 Gateway CORS 配置中的内网 IP（192.168.2.158:9000/9001/9002），生产环境仅允许公网业务域名，开发环境通过 profile 区分。

**具体执行步骤与操作要求：**

1. 编辑 `/work/elink-ai/elink-work/sunmax-gateway/src/main/resources/application.yml`
2. 将 `allowed-origins` 中的以下条目移除：
   - `http://192.168.2.158:9000`
   - `http://192.168.2.158:9001`
   - `http://192.168.2.158:9002`
3. 保留 `http://localhost:9000/9001/9002` 用于本地开发
4. 保留 `https://os.enlinkitech.com`、`https://derms.enlinkitech.com`、`https://derms.enlinkitech.com:9536`
5. 编译验证网关模块

**输入输出规范：**
- 输入：当前 Gateway application.yml
- 输出：移除内网 IP 后的 yml，仅含公网域名和 localhost

**变更文件清单：**
- `/work/elink-ai/elink-work/sunmax-gateway/src/main/resources/application.yml`（移除内网IP，保留公网域名和localhost）

**质量验收标准：**
- [ ] `grep '192.168.2.158' sunmax-gateway/src/main/resources/application.yml` 返回空
- [ ] `grep 'allowed-origins' -A 10 sunmax-gateway/src/main/resources/application.yml` 仅含域名和 localhost
- [ ] `mvn clean package -pl sunmax-gateway -am -DskipTests -T 4` BUILD SUCCESS
- [ ] 本地服务器验证：`./hot-reload.sh reload sunmax-gateway` 执行成功，三层健康验证通过
- [ ] 热更新验证：`./hot-reload.sh status sunmax-gateway` 返回 healthy
- [ ] 功能一致性：前端通过 `http://192.168.2.158:9000` 访问系统，跨域请求正常（登录、数据查询等接口无CORS报错）

---

## P0-3 | 修正 Nacos/EMQX 默认密码并添加安全提示

**阶段目标与核心任务：**
docker-compose.yml 中 Nacos 默认凭据 `nacos/nacos` 和 EMQX 默认凭据 `admin/public` 不应在生产环境使用，需添加安全提示并支持环境变量覆盖。

**具体执行步骤与操作要求：**

1. 编辑 `docker-compose.yml`，将 Nacos 环境变量改为：
   ```
   - NACOS_USERNAME=${NACOS_USERNAME:-nacos}
   - NACOS_PASSWORD=${NACOS_PASSWORD:-nacos}
   ```
   并在 Nacos 服务上方添加注释提示：`# WARNING: 生产环境必须通过 .env 覆盖 NACOS_USERNAME 和 NACOS_PASSWORD`
2. 将 EMQX 环境变量改为：
   ```
   - EMQX_DASHBOARD__DEFAULT_USERNAME=${EMQX_ADMIN_USER:-admin}
   - EMQX_DASHBOARD__DEFAULT_PASSWORD=${EMQX_ADMIN_PASSWORD:-public}
   ```
   并添加注释提示：`# WARNING: 生产环境必须通过 .env 覆盖 EMQX_ADMIN_USER 和 EMQX_ADMIN_PASSWORD`
3. 更新 `elink-work/.env.example`，添加以上变量的占位行：
   ```
   NACOS_USERNAME=change_me
   NACOS_PASSWORD=change_me
   EMQX_ADMIN_USER=change_me
   EMQX_ADMIN_PASSWORD=change_me
   ```

**输入输出规范：**
- 输入：当前 docker-compose.yml、.env.example
- 输出：更新后的 docker-compose.yml 和 .env.example

**变更文件清单：**
- `/work/elink-ai/elink-work/docker-compose.yml`（Nacos/EMQX 环境变量改为 `${VAR:-default}` 格式，添加 WARNING 注释）
- `/work/elink-ai/elink-work/.env.example`（新增 NACOS_USERNAME/PASSWORD、EMQX_ADMIN_USER/PASSWORD 占位行）

**质量验收标准：**
- [ ] docker-compose.yml 中 Nacos 密码使用 `${NACOS_PASSWORD:-nacos}` 格式
- [ ] docker-compose.yml 中 EMQX 密码使用 `${EMQX_ADMIN_PASSWORD:-public}` 格式
- [ ] docker-compose.yml 中包含 WARNING 注释
- [ ] elink-work/.env.example 存在且包含上述 4 个变量占位行
- [ ] 本地服务器验证：`docker-compose -f /work/elink-ai/elink-work/docker-compose.yml config` 验证配置语法无误
- [ ] 热更新验证：`docker-compose up -d --no-build --force-recreate nacos emqx1 emqx2` 后 `docker ps` 确认容器运行正常
- [ ] 功能一致性：`curl -s http://192.168.2.158:8848/nacos/` 返回 Nacos 控制台页面；`curl -u admin:public http://192.168.2.158:18083/api/v5/status` 返回 EMQX 状态

---

## P0-4 | 校正文档统计数据不一致

**阶段目标与核心任务：**
准确统计 javax.* 引用数、Swagger 注解数、@Transactional 分布等技术债数据，同步更新 4 份文档中的所有相关数字。

**具体执行步骤与操作要求：**

1. 在 `/work/elink-ai/elink-work` 下执行精确统计命令：
   ```bash
   # javax.persistence 引用（含 import 行和使用行）
   echo "javax.persistence:"; grep -rn 'javax\.persistence' --include="*.java" . | grep -v target | wc -l
   # javax.annotation 引用
   echo "javax.annotation:"; grep -rn 'javax\.annotation' --include="*.java" . | grep -v target | wc -l
   # javax.websocket 引用
   echo "javax.websocket:"; grep -rn 'javax\.websocket' --include="*.java" . | grep -v target | wc -l
   # javax.servlet 引用
   echo "javax.servlet:"; grep -rn 'javax\.servlet' --include="*.java" . | grep -v target | wc -l
   # javax.validation 引用
   echo "javax.validation:"; grep -rn 'javax\.validation' --include="*.java" . | grep -v target | wc -l
   # javax.transaction 引用
   echo "javax.transaction:"; grep -rn 'javax\.transaction' --include="*.java" . | grep -v target | wc -l
   # 涉及文件数
   echo "javax总文件数:"; grep -rln 'import javax\.' --include="*.java" . | grep -v target | wc -l
   ```
2. 统计 Swagger 注解精确数量：
   ```bash
   echo "@Api:"; grep -rn '@Api(' --include="*.java" . | grep -v target | wc -l
   echo "@ApiOperation:"; grep -rn '@ApiOperation' --include="*.java" . | grep -v target | wc -l
   echo "@ApiModel:"; grep -rn '@ApiModel' --include="*.java" . | grep -v target | wc -l
   echo "@ApiModelProperty:"; grep -rn '@ApiModelProperty' --include="*.java" . | grep -v target | wc -l
   echo "@ApiImplicitParam:"; grep -rn '@ApiImplicitParam' --include="*.java" . | grep -v target | wc -l
   echo "@ApiImplicitParams:"; grep -rn '@ApiImplicitParams' --include="*.java" . | grep -v target | wc -l
   echo "Swagger文件数:"; grep -rln '@Api\b\|@ApiOperation\|@ApiModel\|@ApiModelProperty\|@ApiImplicitParam' --include="*.java" . | grep -v target | wc -l
   ```
3. 统计 @Transactional 精确数量：
   ```bash
   echo "@Transactional处数:"; grep -rn '@Transactional' --include="*.java" . | grep -v target | wc -l
   echo "@Transactional文件数:"; grep -rln '@Transactional' --include="*.java" . | grep -v target | wc -l
   ```
4. 将统计结果更新到以下文档的对应位置：
   - REFACTOR_PLAN.md 第 1.2.3 节 DEBT-05、REFACTOR_PLAN.md 第 2.1 节 Step 2c
   - REFACTOR_TASKS.md P2-2c 指令、P2-2b 指令
   - .trae/rules/refactor-upgrade.md 第 3 节、第 4 节
5. 校正 PHASE-1 完成率：REFACTOR_TASKS.md 进度总览表应标注 62%（8/13 完成，含补偿任务待完成），同时 REFACTOR_PLAN.md 进度总览保持 100%（核心任务已完成）

**输入输出规范：**
- 输入：代码库实时扫描结果
- 输出：校正后的 REFACTOR_PLAN.md、REFACTOR_TASKS.md、REFACTOR_EXECUTE.md、PROGRESS_REPORT.md、.trae/rules/refactor-upgrade.md

**变更文件清单：**
- `/work/elink-ai/REFACTOR_PLAN.md`（校正 javax/Swagger 统计数据、PHASE-1 完成率）
- `/work/elink-ai/REFACTOR_TASKS.md`（校正统计数据、PHASE-1 完成率）
- `/work/elink-ai/REFACTOR_EXECUTE.md`（校正统计数据）
- `/work/elink-ai/PROGRESS_REPORT.md`（校正统计数据）
- `/work/elink-ai/.trae/rules/refactor-upgrade.md`（校正第3/4节统计数据）

**质量验收标准：**
- [ ] 4 份文档中 javax.* 引用总数与实际扫描结果一致（当前扫描：177处 javax.persistence + 44处其他 = ~221处 / ~130文件）
- [ ] 4 份文档中 Swagger 注解总数与实际扫描结果一致
- [ ] REFACTOR_TASKS.md 与 REFACTOR_PLAN.md 的 PHASE-1 完成率计算逻辑清晰且已标注说明
- [ ] 所有文档版本号已递增
- [ ] 本地服务器验证：文档更新不影响运行服务，`./hot-reload.sh status` 确认所有服务仍为 healthy
- [ ] 热更新验证：无需热更新（纯文档变更）
- [ ] 功能一致性：无需功能验证（纯文档变更）

---

## P0-5 | 排查 Together-service 健康检查性能异常

**阶段目标与核心任务：**
R1 性能基线显示 Together-service 健康检查平均 2.5s、最大 8.1s，远超其他服务（15ms左右），必须定位根因并修复。

**具体执行步骤与操作要求：**

1. 查看 Together-service 日志排查启动/运行时异常：
   ```bash
   docker logs --since 1h together-service 2>&1 | grep -E "(ERROR|WARN|Exception|slow|timeout)" | tail -50
   ```
2. 检查 JVM GC 情况（容器内存 838.9MiB）：
   ```bash
   docker exec together-service jstat -gc 1 1000 5
   ```
3. 检查数据库连接池状态和慢查询：
   ```bash
   docker exec together-service jstat -gcutil 1 1000 3
   ```
4. 检查 Nacos 注册是否存在延迟（查看服务注册时间与启动时间的差值）
5. 检查 actuator 端点配置是否存在超时或阻塞调用：
   ```bash
   curl -w "@curl-format.txt" -o /dev/null -s http://localhost:60009/actuator/health
   ```
6. 根据发现的根因提出修复方案并实施

**输入输出规范：**
- 输入：Together-service 运行日志、JVM 指标、数据库连接池状态
- 输出：诊断报告 + 修复代码/配置变更

**变更文件清单：**
- `/work/elink-ai/elink-work/together-service/src/main/resources/application.yml`（根据诊断结果调整配置）
- `/work/elink-ai/elink-work/together-service/src/main/java/**`（根据诊断结果修复代码，如健康端点优化等）

**质量验收标准：**
- [ ] 已产出诊断报告，明确根因（GC/慢查询/Nacos延迟/Health端点阻塞等）
- [ ] 实施修复后，30次迭代健康检查平均响应时间 ≤ 100ms
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] 本地服务器验证：`./hot-reload.sh reload together-service` 执行成功，三层健康验证通过
- [ ] 热更新验证：`./hot-reload.sh status together-service` 返回 healthy
- [ ] 功能一致性：`curl -s http://192.168.2.158:60009/together/actuator/health` 响应时间 ≤ 100ms，业务接口正常

---

## P0-6 | 排查 EMQX1 CPU 异常（91.84%）

**阶段目标与核心任务：**
R1 基线显示 EMQX1 CPU 占用 91.84%，异常偏高，可能导致 MQTT 消息丢包或延迟。

**具体执行步骤与操作要求：**

1. 查看 EMQX 运行指标：
   ```bash
   curl -u admin:public http://localhost:18083/api/v5/stats
   ```
2. 检查连接数和消息吞吐：
   ```bash
   curl -u admin:public http://localhost:18083/api/v5/connections | python3 -m json.tool | head -20
   ```
3. 检查是否存在大量断线重连风暴或异常 Topic 订阅
4. 如果是业务负载正常导致的高 CPU，评估是否需要增加资源配额；如果是异常行为，定位并修复
5. 更新 docker-compose.yml 中 EMQX 的资源限制（当前建议值 CPU 1, 内存 512M，如需调整则更新）

**输入输出规范：**
- 输入：EMQX Dashboard API 数据、Docker 容器指标
- 输出：诊断报告 + 配置优化

**变更文件清单：**
- `/work/elink-ai/elink-work/docker-compose.yml`（EMQX 资源限制调整）
- EMQX 配置文件（根据诊断结果调整，如连接数限制、Topic 优化等）

**质量验收标准：**
- [ ] 已产出诊断报告
- [ ] EMQX CPU 占用降至合理范围（< 50% 在正常业务负载下）
- [ ] docker-compose.yml 中 EMQX 资源限制已更新为合理值
- [ ] 本地服务器验证：`docker stats emqx1 --no-stream` 确认 CPU 占用 < 50%
- [ ] 热更新验证：`docker-compose up -d --no-build --force-recreate emqx1` 后 `docker ps` 确认容器运行正常
- [ ] 功能一致性：`curl -u admin:public http://192.168.2.158:18083/api/v5/status` 返回 EMQX 正常状态；protocol-service MQTT 连接和消息收发正常

---

# ═══════════════════════════════════════════════════════════
# PHASE-1：安全加固补偿任务
# ═══════════════════════════════════════════════════════════

> **阶段目标**：完成 P1-T3 和 P1-T9 因环境限制回退后的补偿方案，实现数据库安全管理和加密传输。
>
> **前置条件**：PHASE-0 完成（数据统计已校正，资源限制已添加）
>
> **衔接关系**：P1-COMP 完成后 PHASE-2 才能启动，因为 Flyway 引入和 ddl-auto:validate 是 Spring Boot 3.x 升级的前置条件。

---

## P1-COMP-1 | 引入 Flyway + 切换 ddl-auto 为 validate（补偿 P1-T3）

**阶段目标与核心任务：**
引入 Flyway 替代 Hibernate 自动 DDL 管理，逐服务锁定数据库表结构，最终将 ddl-auto 从 update 切换为 validate。

**具体执行步骤与操作要求：**

**Step 1：在父 POM 添加 Flyway 依赖管理**
```xml
<!-- 在父POM的 dependencyManagement 中添加 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <!-- 版本由 Spring Boot BOM 管理，不手动指定 -->
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

**Step 2：为每个使用 JPA 的服务添加 Flyway 依赖**
逐服务在 `pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```
受影响服务（10个）：auth-service, system-service, device-service, data-service, protocol-service, crontab-service, devops-service, configure-service, together-service, webapp-service

**Step 3：为每个数据库生成 V1 基线迁移脚本**
对每个数据库执行结构导出，保存为 `src/main/resources/db/migration/V1__<dbname>_baseline.sql`：
```bash
mysqldump -h ${MYSQL_HOST} -u ${MYSQL_USERNAME} -p${MYSQL_PASSWORD} \
  --no-data --skip-add-drop-table --complete-insert \
  <database> > V1__<database>_baseline.sql
```
数据库清单：sunos-system, sunos-operate, sunos-data, sunos-model, sunos-access, sunos-log, sunos-configure

**Step 4：逐服务排查 Entity-DB 差异**
优先级：auth-service > device-service > data-service > 其他
1. 临时将某个服务的 ddl-auto 改为 update
2. 启动服务，观察 Hibernate DDL 变更日志
3. 将变更整理为 `V2__fix_xxx_column_type.sql`
4. 恢复 ddl-auto: validate，验证服务正常启动
5. 提交迁移脚本

**Step 5：全量切换 ddl-auto 为 validate**
```bash
cd /work/elink-ai/elink-work
for svc in auth-service system-service device-service data-service protocol-service \
           crontab-service devops-service configure-service together-service webapp-service; do
  sed -i 's/ddl-auto: update/ddl-auto: validate/' "${svc}/src/main/resources/application.yml"
done
```

**Step 6：编译验证**
```bash
mvn clean compile -DskipTests -T 4
```

**输入输出规范：**
- 输入：当前 10 个服务的 pom.xml、application.yml、数据库实例
- 输出：
  - 父 POM 含 flyway 依赖管理
  - 10 个服务含 flyway 依赖 + 迁移脚本目录 `src/main/resources/db/migration/`
  - 10 个服务 ddl-auto: validate
  - 每个数据库至少一个 V1 基线脚本和增量修复脚本

**变更文件清单：**
- `/work/elink-ai/elink-work/pom.xml`（添加 Flyway 依赖管理）
- `/work/elink-ai/elink-work/auth-service/pom.xml`（添加 flyway-core + flyway-mysql 依赖）
- `/work/elink-ai/elink-work/system-service/pom.xml`（同上）
- `/work/elink-ai/elink-work/device-service/pom.xml`（同上）
- `/work/elink-ai/elink-work/data-service/pom.xml`（同上）
- `/work/elink-ai/elink-work/protocol-service/pom.xml`（同上）
- `/work/elink-ai/elink-work/crontab-service/pom.xml`（同上）
- `/work/elink-ai/elink-work/devops-service/pom.xml`（同上）
- `/work/elink-ai/elink-work/configure-service/pom.xml`（同上）
- `/work/elink-ai/elink-work/together-service/pom.xml`（同上）
- `/work/elink-ai/elink-work/webapp-service/pom.xml`（同上）
- 各服务 `src/main/resources/db/migration/V1__*_baseline.sql`（基线迁移脚本）
- 各服务 `src/main/resources/db/migration/V2__fix_*.sql`（增量修复脚本）
- 各服务 `src/main/resources/application.yml`（ddl-auto: update → validate）

**质量验收标准：**
- [ ] `grep -rn 'ddl-auto: update' --include="*.yml" . | grep -v target | wc -l` 返回 0
- [ ] `grep -rn 'ddl-auto: validate' --include="*.yml" . | grep -v target | wc -l` 返回 ≥ 10
- [ ] 每个使用 JPA 的服务均有 `db/migration/V1__*.sql`
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] 至少 5 个核心服务（auth/gateway/system/device/data）启动验证通过
- [ ] 本地服务器验证：逐服务执行 `./hot-reload.sh reload <service>` 确认启动成功、Flyway 迁移日志无错误
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：核心业务功能冒烟测试通过（用户登录、设备列表查询、数据查询）

---

## P1-COMP-2 | MySQL SSL 配置 + useSSL=true（补偿 P1-T9）

**阶段目标与核心任务：**
为生产 MySQL 配置 SSL 证书，将 JDBC 连接参数 useSSL 切换为 true，确保数据库连接加密传输。

**具体执行步骤与操作要求：**

**Step 1：确认 MySQL SSL 状态**
```bash
mysql -h ${MYSQL_HOST} -u root -p${MYSQL_PASSWORD} -e "SHOW VARIABLES LIKE '%ssl%';"
```

**Step 2：如 SSL 未启用，生成自签名证书并配置 MySQL**
```bash
# 生成 CA 密钥和证书
openssl genrsa 2048 > ca-key.pem
openssl req -new -x509 -nodes -days 3650 -key ca-key.pem -out ca-cert.pem

# 生成服务端密钥和证书
openssl req -newkey rsa:2048 -days 3650 -nodes -keyout server-key.pem -out server-req.pem
openssl rsa -in server-key.pem -out server-key.pem
openssl x509 -req -in server-req.pem -days 3650 -CA ca-cert.pem -CAkey ca-key.pem -set_serial 01 -out server-cert.pem
```
更新 MySQL 配置 my.cnf：
```ini
[mysqld]
require_secure_transport=ON
ssl-ca=/path/to/ca-cert.pem
ssl-cert=/path/to/server-cert.pem
ssl-key=/path/to/server-key.pem
```

**Step 3：切换全部 JDBC 连接为 useSSL=true**
```bash
cd /work/elink-ai/elink-work
find . -name "application.yml" -path "*/src/*" -exec sed -i 's/useSSL=false/useSSL=true/g' {} +
```

**Step 4：如需客户端证书验证，在 JDBC URL 追加参数**
```
&verifyServerCertificate=true&trustCertificateKeyStoreUrl=file:///path/to/truststore&trustCertificateKeyStorePassword=changeit
```

**Step 5：验证连接**
```bash
mysql -h ${MYSQL_HOST} -u root -p${MYSQL_PASSWORD} --ssl-mode=REQUIRED -e "SELECT 1;"
```

**输入输出规范：**
- 输入：MySQL 服务端配置、当前 JDBC URL
- 输出：SSL 证书文件、更新后的 MySQL 配置、所有 application.yml 的 useSSL=true

**变更文件清单：**
- MySQL 服务端 SSL 证书文件（ca-cert.pem, server-cert.pem, server-key.pem）
- MySQL 配置 my.cnf（启用 SSL）
- 各服务 `src/main/resources/application.yml`（useSSL=false → useSSL=true）
- `/work/elink-ai/elink-work/docker-compose.yml`（如 MySQL 为容器部署，需挂载证书）

**质量验收标准：**
- [ ] `grep -rn 'useSSL=false' --include="*.yml" . | grep -v target | wc -l` 返回 0
- [ ] `grep -rn 'useSSL=true' --include="*.yml" . | grep -v target | wc -l` 为全部 JDBC 连接数（13条）
- [ ] MySQL `SHOW VARIABLES LIKE '%ssl%'` 显示 SSL 已启用
- [ ] 全量编译通过
- [ ] 本地服务器验证：逐服务执行 `./hot-reload.sh reload <service>` 确认服务启动成功、数据库连接正常
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：核心业务功能冒烟测试通过（用户登录、设备列表查询、数据查询），确认 SSL 连接无报错

---

## ✅ P1-COMP-3 | 制定 OAuth2 迁移对照表（PHASE-2 前置设计）— 已完成 2026-06-08

**阶段目标与核心任务：**
产出 auth-service OAuth2 迁移的完整技术设计方案，明确旧 API → 新 API 的映射关系、Token 格式兼容策略、前端适配要点。

**具体执行步骤与操作要求：**

1. 分析 auth-service 当前安全配置，列出所有使用 `spring-cloud-starter-oauth2` 的类：
   ```bash
   grep -rn 'AuthorizationServerConfigurer\|ResourceServerConfigurer\|SMTokenService\|OAuth2AuthorizationServerConfigurer' --include="*.java" . | grep -v target
   ```

2. 产出迁移对照表，至少包含：

| 旧组件/API | 新组件/API | 迁移要点 |
|-----------|-----------|---------|
| AuthorizationServerConfigurer | @Configuration + RegisteredClientRepository | 授权服务器配置方式变更 |
| ResourceServerConfigurer | SecurityFilterChain + oauth2ResourceServer() | 资源服务器配置方式变更 |
| SMTokenService（4个接口） | 逐个适配新Spring Security API | 自定义Token服务适配 |
| JWT Token 生成/验证 | NimbusJwtDecoder + JwtEncoder | Token格式保持兼容 |
| /oauth/token 端点 | Token Endpoint 新地址 | 前端调用地址变更 |

3. 产出 Token 兼容策略：
   - 旧 Token 在迁移过渡期内是否继续有效？
   - 是否需要双 Token 验证逻辑？
   - 前端 token 刷新机制如何适配？

4. 产出前端适配要点清单（供 PHASE-4 参考）

5. 将设计方案写入 REFACTOR_EXECUTE.md 的新增章节「OAuth2 迁移设计」

**输入输出规范：**
- 输入：auth-service 安全配置源码
- 输出：OAuth2 迁移对照表 + Token 兼容策略 + 前端适配要点，写入 REFACTOR_EXECUTE.md

**变更文件清单：**
- `/work/elink-ai/REFACTOR_EXECUTE.md`（新增「OAuth2 迁移设计」章节）

**质量验收标准：**
- [ ] 对照表覆盖 auth-service 全部安全配置类
- [ ] SMTokenService 4个接口均有明确适配方案
- [ ] Token 兼容策略已定义过渡期方案
- [ ] 前端适配要点 ≥ 5 条
- [ ] 本地服务器验证：文档更新不影响运行服务，`./hot-reload.sh status` 确认所有服务仍为 healthy
- [ ] 热更新验证：无需热更新（纯文档/设计变更）
- [ ] 功能一致性：无需功能验证（纯文档/设计变更）

---

# ═══════════════════════════════════════════════════════════
# PHASE-2：框架升级与核心重构
# ═══════════════════════════════════════════════════════════

> **阶段目标**：将核心技术栈从 Spring Boot 2.3.0 + Java 8 升级至 Spring Boot 3.3.x + Java 17/21 + Spring Cloud 2023.x，完成 Swagger→SpringDoc、javax→jakarta、OAuth2 迁移及 Feign 调用重构。
>
> **前置条件**：PHASE-0 和 PHASE-1 全部完成
>
> **衔接关系**：PHASE-2 完成后才能启动 PHASE-3（代码质量优化依赖新框架的日志/监控 API）

---

## P2-2a | Spring Boot 2.3.0 → 2.7.18 + Swagger → SpringDoc 1.7.0 ✅ 已完成 2026-06-08

**阶段目标与核心任务：**
将 Spring Boot 升级至 2.7.18 LTS，Spring Cloud 升级至 2021.0.9，同时将 Swagger 2.9.2 迁移至 springdoc-openapi-ui 1.7.0（Spring Boot 2.7 专用版本），并配置 2.7 兼容参数。

**具体执行步骤与操作要求：**

**Step 1：修改父 POM 版本**
```bash
cd /work/elink-ai/elink-work
sed -i 's|<version>2.3.0.RELEASE</version>|<version>2.7.18</version>|' pom.xml
sed -i 's|<spring-cloud.version>Hoxton.SR8</spring-cloud.version>|<spring-cloud.version>2021.0.9</spring-cloud.version>|' pom.xml
```

**Step 2：替换 Swagger 依赖为 SpringDoc 1.7.0**
在父 POM 中：
- 删除 `io.springfox:springfox-swagger2:2.9.2`
- 删除 `io.springfox:springfox-swagger-ui:2.9.2`
- 删除 `io.swagger:swagger-models:1.5.21`
- 删除 `com.github.xiaoymin:swagger-bootstrap-ui:1.9.6`
- 新增 `org.springdoc:springdoc-openapi-ui:1.7.0`

**Step 3：全局替换 Swagger 注解**（使用实际统计的精确数量替换）
```bash
# 8类注解映射（注：属性映射复杂，建议在IDE中验证每处替换）
find . -name "*.java" -path "*/src/*" -exec sed -i \
  -e 's/@Api(tags = /@Tag(name = /g' \
  -e 's/@ApiOperation(value = /@Operation(summary = /g' \
  -e 's/@ApiOperation(value=/@Operation(summary=/g' \
  -e 's/@ApiParam(/@Parameter(/g' \
  -e 's/@ApiModel(value = /@Schema(description = /g' \
  -e 's/@ApiModel(/@Schema(/g' \
  -e 's/@ApiModelProperty(value = /@Schema(description = /g' \
  -e 's/@ApiModelProperty(/@Schema(/g' \
  -e 's/@ApiImplicitParam(name = /@Parameter(name = /g' \
  {} +
```

**Step 4：添加 Spring Boot 2.7 兼容配置**
为每个服务的 `application.yml` 添加：
```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant-path-matcher
  main:
    allow-circular-references: true
```

**Step 5：Spring Cloud Alibaba 适配**
在父 POM 的 `<dependencyManagement>` 中添加：
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-dependencies</artifactId>
    <version>2021.0.9.0</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```
将子模块中 `spring-cloud-starter-alibaba-nacos-discovery` 的硬编码版本号 `2.2.9.RELEASE` 移除，交由 BOM 管理。

**Step 6：逐服务编译验证**
```bash
mvn clean compile -DskipTests -T 4
```
遇编译错误逐个修复，重点关注注解属性不兼容问题。

**Step 7：残留检查**
```bash
grep -rn 'springfox\|swagger-bootstrap' --include="pom.xml" . | grep -v target
grep -rn 'import springfox\|import io.springboot' --include="*.java" . | grep -v target
grep -rn '@Api(' --include="*.java" . | grep -v target
```

**输入输出规范：**
- 输入：当前父 POM、各服务 POM、全部 Java 源码
- 输出：升级后的 POM、迁移后的 Java 注解、每个服务的 application.yml 兼容配置

**变更文件清单：**
- `/work/elink-ai/elink-work/pom.xml`（Spring Boot 2.7.18 + Spring Cloud 2021.0.9 + SpringDoc 1.7.0 + SCA 2021.0.9.0）
- `/work/elink-ai/elink-work/sunmax-common/pom.xml`（替换 Swagger 依赖为 SpringDoc）
- 各服务 `pom.xml`（替换 Swagger 依赖为 SpringDoc）
- 各服务 `src/main/java/**`（Swagger 注解 → OpenAPI 注解替换）
- 各服务 `src/main/resources/application.yml`（添加 2.7 兼容配置）

**质量验收标准：**
- [ ] `grep '2.3.0.RELEASE' pom.xml` 返回空
- [ ] `grep 'springfox\|swagger-bootstrap' --include="pom.xml" -r . | grep -v target | wc -l` 返回 0
- [ ] `grep '@Api(' --include="*.java" -rn . | grep -v target | wc -l` 返回 0
- [ ] `grep '@ApiModel\|@ApiModelProperty' --include="*.java" -rn . | grep -v target | wc -l` 返回 0
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] 本地服务器验证：逐服务执行 `./hot-reload.sh reload <service>` 确认启动成功、SpringDoc 端点可访问
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：`curl -s http://192.168.2.158:60001/sauth/v3/api-docs` 返回 OpenAPI JSON；核心业务功能冒烟测试通过

---

## P2-2b | Java 8 → Java 17 ✅ 已完成

**阶段目标与核心任务：**
将 Java 版本从 8 升级至 17，更新 Maven compiler 配置和 Dockerfile 基础镜像，处理 Java 17 JPMS 反射访问限制和内部 API 兼容性。

**实际执行记录（2026-06-09）：**
- 父POM+12子模块POM java.version 8→17
- Dockerfile：基于 openjdk:8-jre 手动安装 OpenJDK 17.0.2（因 Docker Hub 拉取 eclipse-temurin:17-jre 超时，改用华为镜像）
- 修复 DataReportServiceImpl 中 3 处 Java 17 泛型推断严格化导致的方法引用编译错误
- 添加 JDK_JAVA_OPTIONS --add-opens 参数解决 JPMS 反射访问限制（FST/Redisson/JAXB 库需要）
- JAVA_TOOL_OPTIONS 不支持 --add-opens，改用 JDK_JAVA_OPTIONS
- hot-reload.sh 添加 Java 17 环境变量设置（JAVA_17_HOME）
- crontab-service 健康检查路径修正 /scrontab → /crontab
- 清理无效 --add-opens 条目（sun.reflect 等 Java 17 中不存在的包）
- 11 服务热更新部署验证通过（Docker healthy + HTTP UP + Nacos 注册正常）
- 冒烟测试通过（Gateway 路由 200 / OAuth2 端点正常 / System UP / 容器 Java 版本 17.0.2）

**具体执行步骤与操作要求：**

**Step 1：修改父 POM Java 版本**
```bash
sed -i 's|<maven.compiler.source>8</maven.compiler.source>|<maven.compiler.source>17</maven.compiler.source>|' pom.xml
sed -i 's|<maven.compiler.target>8</maven.compiler.target>|<maven.compiler.target>17</maven.compiler.target>|' pom.xml
sed -i 's|<java.version>8</java.version>|<java.version>17</java.version>|' pom.xml
```

**Step 2：更新 Dockerfile 基础镜像**
> ⚠️ 实际执行中因 Docker Hub 拉取超时，改为基于 openjdk:8-jre 手动安装 OpenJDK 17.0.2
```bash
# 原方案（Docker Hub 可达时）
sed -i 's|FROM openjdk:8-jre|FROM eclipse-temurin:17-jre|' Dockerfile

# 实际方案（Docker Hub 不可达时）
# 在 Dockerfile 中添加 Java 17 安装步骤：
RUN wget -q "https://mirrors.huaweicloud.com/openjdk/17.0.2/openjdk-17.0.2_linux-x64_bin.tar.gz" -O /tmp/jdk17.tar.gz && \
    mkdir -p /usr/local/java/17 && \
    tar xzf /tmp/jdk17.tar.gz -C /usr/local/java/17 --strip-components=1 && \
    rm /tmp/jdk17.tar.gz
ENV JAVA_HOME=/usr/local/java/17
ENV PATH="${JAVA_HOME}/bin:${PATH}"
```

**Step 3：配置 Java 17 JPMS 兼容性参数**
> ⚠️ 必须使用 JDK_JAVA_OPTIONS 而非 JAVA_TOOL_OPTIONS，后者不支持 --add-opens
```bash
# 在 Dockerfile 中设置环境变量（FST 序列化库深度反射需要）
ENV JDK_JAVA_OPTIONS="\
--add-opens java.base/java.lang=ALL-UNNAMED \
--add-opens java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens java.base/java.util=ALL-UNNAMED \
--add-opens java.base/java.io=ALL-UNNAMED \
--add-opens java.base/java.math=ALL-UNNAMED \
--add-opens java.base/java.net=ALL-UNNAMED \
--add-opens java.base/java.nio=ALL-UNNAMED \
--add-opens java.base/java.security=ALL-UNNAMED \
--add-opens java.base/java.text=ALL-UNNAMED \
--add-opens java.base/java.time=ALL-UNNAMED \
--add-opens java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens java.base/sun.security.action=ALL-UNNAMED \
--add-opens java.base/sun.security.provider=ALL-UNNAMED \
--add-opens java.base/sun.security.util=ALL-UNNAMED \
--add-opens java.base/sun.security.x509=ALL-UNNAMED"
```

**Step 4：检查 Java 17 不兼容代码**
```bash
grep -rn 'sun\.misc\.\|sun\.reflect\.\|com\.sun\.' --include="*.java" . | grep -v target
```

**Step 5：处理 Hibernate 5.6 的 javax→jakarta 残留**
Spring Boot 2.7 仍使用 javax.*，此处无需做 javax→jakarta 迁移，但需确认无显式 jakarta.* 引用。

**Step 6：编译验证**
```bash
mvn clean compile -DskipTests -T 4
```

**Step 7：热更新部署验证**
```bash
# 重建基础镜像
docker build -t elink-base:latest -f Dockerfile .
# 逐服务热更新
./hot-reload.sh reload auth-service
# ... 依次更新所有11个服务
# 检查状态
./hot-reload.sh status
```

**Step 8：冒烟测试**
```bash
# Gateway 路由
curl -s -o /dev/null -w "%{http_code}" http://localhost:5000/sauth/actuator/health
# OAuth2 Token 端点
curl -X POST http://localhost:60001/sauth/oauth/token -d "grant_type=password&..."
# System Service
curl http://localhost:60002/system/actuator/health
# 容器 Java 版本
docker exec auth-service java -version
# JDK_JAVA_OPTIONS 生效
docker exec auth-service java -version 2>&1 | head -1
```

**输入输出规范：**
- 输入：当前 POM Java 版本配置、Dockerfile
- 输出：Java 17 配置、更新后的 Dockerfile（含 JDK_JAVA_OPTIONS --add-opens 参数）

**变更文件清单：**
- `/work/elink-ai/elink-work/pom.xml`（Java 版本 8 → 17）
- `/work/elink-ai/elink-work/Dockerfile`（基于 openjdk:8-jre 手动安装 OpenJDK 17.0.2 + JDK_JAVA_OPTIONS --add-opens 参数）
- `/work/elink-ai/elink-work/hot-reload.sh`（添加 Java 17 环境变量设置 JAVA_17_HOME）
- `/work/elink-ai/elink-work/docker-compose.yml`（修正 crontab-service 健康检查路径 /scrontab → /crontab）
- 12 个子模块 pom.xml（compiler 8 → 17）
- `together-service/src/main/java/com/sunmax/together/service/operation/impl/DataReportServiceImpl.java`（修复泛型推断 3 处）

**质量验收标准：**
- [√] `grep '<java.version>' pom.xml` 显示 17
- [√] Dockerfile 使用 OpenJDK 17.0.2（基于 openjdk:8-jre 手动安装）
- [√] `grep 'sun\.misc\.\|sun\.reflect\.' --include="*.java" -rn . | grep -v target | wc -l` 返回 0
- [√] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [√] 热更新部署验证：11 个服务全部 Docker healthy + HTTP UP + Nacos 注册正常
- [√] 冒烟测试：Gateway 路由 200、OAuth2 端点正常响应、System Service UP、容器 Java 版本 17.0.2
- [√] JPMS 兼容性：JDK_JAVA_OPTIONS 含 --add-opens 参数，FST/Redisson/JAXB 反射访问正常

---

## P2-2c | Spring Boot 2.7 → 3.3.x + javax→jakarta + OAuth2 迁移 ✅ 已完成

**完成时间：** 2026-06-09

**阶段目标与核心任务：**
执行最高风险的升级步骤：Spring Boot 3.3.x + Spring Cloud 2023.0.x + javax→jakarta 命名空间迁移 + OAuth2 模块迁移。

**具体执行步骤与操作要求：**

**Step 1：升级 Spring Boot 和 Spring Cloud 版本**
```bash
sed -i 's|<version>2.7.18</version>|<version>3.3.6</version>|' pom.xml
sed -i 's|<spring-cloud.version>2021.0.9</spring-cloud.version>|<spring-cloud.version>2023.0.4</spring-cloud.version>|' pom.xml
```

**Step 2：javax → jakarta 全局替换**（使用实际统计结果）
```bash
find . -name "*.java" -path "*/src/*" -exec sed -i \
  -e 's/import javax\.persistence\./import jakarta.persistence./g' \
  -e 's/import javax\.annotation\./import jakarta.annotation./g' \
  -e 's/import javax\.websocket\./import jakarta.websocket./g' \
  -e 's/import javax\.servlet\./import jakarta.servlet./g' \
  -e 's/import javax\.validation\./import jakarta.validation./g' \
  -e 's/import javax\.transaction\./import jakarta.transaction./g' \
  {} +
```

**Step 3：替换 OAuth2 依赖**
在父 POM 中：
- 删除 `spring-cloud-starter-oauth2`
- 在 auth-service 中新增：`spring-authorization-server`（版本由 Spring Boot BOM 管理）
- 在其他 9 个服务中新增：`spring-boot-starter-oauth2-resource-server`

**Step 4：适配 OAuth2 配置类（按 P1-COMP-3 的迁移对照表执行）**
- auth-service：重写 AuthorizationServer 配置
- 所有服务：重写 ResourceServer 配置为 SecurityFilterChain 方式
- SMTokenService：逐接口适配新 Spring Security API

**Step 5：使用 OpenRewrite 辅助迁移（推荐但非必须）**
```bash
mvn org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.activeRecipes=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_3
```

**Step 6：删除 2.7 兼容配置**
移除 Step P2-2a Step 4 添加的兼容配置：
```yaml
# 删除以下配置（Spring Boot 3.x 默认行为已变更）
# spring.mvc.pathmatch.matching-strategy=ant-path-matcher
# spring.main.allow-circular-references=true
```

**Step 7：编译验证**
```bash
mvn clean compile -DskipTests -T 4
```

**输入输出规范：**
- 输入：P2-2a/b 已升级的代码库、P1-COMP-3 迁移设计方案
- 输出：Spring Boot 3.3.x 全量代码、OAuth2 新配置、jakarta 命名空间代码

**变更文件清单：**
- `/work/elink-ai/elink-work/pom.xml`（Spring Boot 3.3.6 + Spring Cloud 2023.0.4）
- `/work/elink-ai/elink-work/auth-service/pom.xml`（替换 OAuth2 依赖为 spring-authorization-server）
- 其余 9 个服务 `pom.xml`（替换 OAuth2 依赖为 spring-boot-starter-oauth2-resource-server）
- 全部 `src/main/java/**`（javax.* → jakarta.* import 替换，约 221 处/130 文件）
- auth-service 安全配置类（重写 AuthorizationServer 配置）
- 所有服务安全配置类（重写 ResourceServer 配置为 SecurityFilterChain）
- SMTokenService 相关类（适配新 Spring Security API）
- 各服务 `src/main/resources/application.yml`（删除 2.7 兼容配置）

**质量验收标准：**
- [ ] `grep -rn 'import javax\.' --include="*.java" . | grep -v target | wc -l` 返回 0
- [ ] `grep 'spring-cloud-starter-oauth2' --include="pom.xml" -rn . | grep -v target | wc -l` 返回 0
- [ ] auth-service 包含 spring-authorization-server 依赖
- [ ] 9 个资源服务包含 spring-boot-starter-oauth2-resource-server 依赖
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] **⚠️ 待验证**：本地服务器验证 — 逐服务执行 `./hot-reload.sh reload <service>` 确认 Spring Boot 3.3.x 启动成功、jakarta 命名空间无报错
- [ ] **⚠️ 待验证**：热更新验证 — `./hot-reload.sh status` 确认所有服务 healthy
- [ ] **⚠️ 待验证**：功能一致性 — 用户登录/Token 获取/刷新正常；`curl -s http://192.168.2.158:5000/sauth/actuator/health` 响应正常；核心业务功能冒烟测试通过

**注意事项（重要）：** 此步骤为全项目最高风险操作，执行前务必确保：
- Git 工作区干净，已创建 `refactor/phase-2-2c` 分支
- 数据库已完整备份
- P1-COMP-1（Flyway）已就绪，ddl-auto: validate 生效

---

## P2-2c-2 | 补全事务管理（ARCH-05）

**阶段目标与核心任务：**
逐服务审查 Service 层，为涉及多表写操作的方法添加 `@Transactional` 注解，区分读/写事务传播级别。

**具体执行步骤与操作要求：**

1. 统计当前事务覆盖情况：
   ```bash
   echo "@Transactional处数:"; grep -rn '@Transactional' --include="*.java" . | grep -v target | wc -l
   echo "@Transactional文件数:"; grep -rln '@Transactional' --include="*.java" . | grep -v target | wc -l
   ```
2. 逐服务审查并添加注解（手动操作为主）
   - 优先级：device-service > together-service > webapp-service > system-service > protocol-service > 其他
   - 多表写操作：`@Transactional(rollbackFor = Exception.class)`
   - 纯查询方法：`@Transactional(readOnly = true)`
   - 类级别可添加 `@Transactional(readOnly = true)`，写方法单独覆盖
3. 全量编译：
   ```bash
   mvn clean compile -DskipTests -T 4
   ```

**输入输出规范：**
- 输入：各服务 Service 层源码
- 输出：添加事务注解后的 Service 类

**变更文件清单：**
- 各服务 Service 层 Java 文件（添加 @Transactional 注解，涉及 device-service/together-service/webapp-service/system-service/protocol-service 等）

**质量验收标准：**
- [ ] 所有涉及多表写操作的 Service 方法均有 @Transactional 注解
- [ ] 纯查询方法标注了 `@Transactional(readOnly = true)`
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] 本地服务器验证：逐服务执行 `./hot-reload.sh reload <service>` 确认启动成功、事务注解无冲突
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：涉及多表操作的业务功能（设备创建、数据写入等）冒烟测试通过，事务回滚场景验证正常

---

## P2-2c-3 | Spring Cloud Alibaba 版本配置

**阶段目标与核心任务：**
在父 POM 中配置 Spring Cloud Alibaba 2023.0.3.2 的 dependencyManagement，确保与 Spring Boot 3.3.x + Spring Cloud 2023.0.4 兼容。

**具体执行步骤与操作要求：**

1. 在父 POM 的 `<dependencyManagement>` 中添加（如 P2-2a 中已添加旧版本则先替换）：
   ```xml
   <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-alibaba-dependencies</artifactId>
       <version>2023.0.3.2</version>
       <type>pom</type>
       <scope>import</scope>
   </dependency>
   ```
2. 确保所有子模块中 `spring-cloud-starter-alibaba-nacos-discovery` 无硬编码版本号
3. 编译验证

**输入输出规范：**
- 输入：当前父 POM
- 输出：含 SCA BOM 的父 POM

**变更文件清单：**
- `/work/elink-ai/elink-work/pom.xml`（添加/替换 SCA BOM 2023.0.3.2）
- 子模块 `pom.xml`（移除 Nacos 硬编码版本号）

**质量验收标准：**
- [ ] `grep 'spring-cloud-alibaba-dependencies' pom.xml` 显示 2023.0.3.2
- [ ] 子模块无 Nacos 硬编码版本号
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] 本地服务器验证：`./hot-reload.sh reload auth-service` 确认 Nacos 注册正常
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy 且 Nacos 注册实例版本正确
- [ ] 功能一致性：`curl -s http://192.168.2.158:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=20` 确认所有服务注册正常

---

## P2-2c-4 | Feign 调用重构（ARCH-06）

**阶段目标与核心任务：**
将当前 42 个 FeignController 代理模式重构为服务间直接通过 Feign Client 接口调用，消除冗余代理层。

**具体执行步骤与操作要求：**

1. 绘制当前 FeignController 调用依赖图谱（42个文件）
2. 在 `sunmax-common` 中创建 `feign` 包，定义统一的 `@FeignClient` 接口
3. 各服务实现自己的 FeignClient 接口（Controller 层实现接口方法）
4. 调用方直接注入 FeignClient 接口，替换原 FeignController 代理调用
5. 删除所有 FeignController 代理类
6. 为每个 FeignClient 配置 FallbackFactory 实现统一降级
7. 编译验证

**输入输出规范：**
- 输入：42 个 FeignController 源码
- 输出：sunmax-common/feign 包中的接口 + 各服务新实现 + FallbackFactory + 已删除的 FeignController

**变更文件清单：**
- `/work/elink-ai/elink-work/sunmax-common/src/main/java/**/feign/`（新增 FeignClient 接口包）
- 各服务 `src/main/java/**`（实现 FeignClient 接口 + FallbackFactory）
- 删除全部 `*FeignController*.java`（42个文件）

**质量验收标准：**
- [ ] `find . -name "*FeignController*" -path "*/src/*" | grep -v target | wc -l` 返回 0
- [ ] sunmax-common/feign 包包含所有跨服务调用接口
- [ ] 每个 FeignClient 接口均有对应 FallbackFactory
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] 本地服务器验证：逐服务执行 `./hot-reload.sh reload <service>` 确认启动成功、Feign 调用链路正常
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：跨服务调用场景（如设备数据查询、协议下发等）冒烟测试通过，FallbackFactory 降级逻辑验证正常

**注意事项：** 此任务工作量最大，建议在 P2-2c 主体迁移完成并通过验证后再执行

---

# ═══════════════════════════════════════════════════════════
# PHASE-3：代码质量与性能优化
# ═══════════════════════════════════════════════════════════

> **阶段目标**：清理技术债务，优化运行性能，建立基准数据。
>
> **前置条件**：PHASE-2 完成
>
> **衔接关系**：PHASE-3 中 P3-A、P3-B、P3-C、P3-C2 可并行执行，P3-D（性能基准测试）必须在其他任务完成后执行。

---

## P3-A | 清理 e.printStackTrace() 和 System.out/err.print（DEBT-01/02）

**阶段目标与核心任务：**
将全部 `e.printStackTrace()`（95处/20文件）替换为 `log.error`，将 `System.out/err.println`（109处/34文件）替换为 `log.info/log.error`，确保每个修改类都有 Logger 声明。

**具体执行步骤与操作要求：**

1. 统计当前数量：
   ```bash
   echo "e.printStackTrace():"; grep -rn 'e\.printStackTrace()' --include="*.java" . | grep -v target | wc -l
   echo "System.out/err:"; grep -rn 'System\.\(out\|err\)\.print' --include="*.java" . | grep -v target | wc -l
   ```
2. 批量替换（需人工审查上下文）
3. 检查 Logger 声明完整性：
   ```bash
   grep -rln 'e\.printStackTrace()\|System\.\(out\|err\)\.print' --include="*.java" . | grep -v target | while read f; do
     if ! grep -q 'Logger log\|@Slf4j' "$f"; then
       echo "缺少Logger: $f"
     fi
   done
   ```
4. 全量编译验证

**输入输出规范：**
- 输入：含 printStackTrace/System.out 的 Java 文件
- 输出：替换后的 Java 文件（含 Logger 声明）

**变更文件清单：**
- 含 `e.printStackTrace()` 的 Java 文件（95处/20文件）
- 含 `System.out/err.println` 的 Java 文件（109处/34文件）
- 需新增 Logger 声明或 @Slf4j 注解的文件

**质量验收标准：**
- [ ] `grep -rn 'e\.printStackTrace()' --include="*.java" . | grep -v target | wc -l` 返回 0
- [ ] `grep -rn 'System\.\(out\|err\)\.print' --include="*.java" . | grep -v target | wc -l` 返回 0
- [ ] 所有修改文件均包含 Logger 声明或 @Slf4j 注解
- [ ] 本地服务器验证：`./hot-reload.sh reload <受影响服务>` 确认启动成功、日志输出正常（无 console 输出残留）
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：核心业务功能冒烟测试通过，日志级别和输出格式符合预期

---

## P3-B | 依赖版本升级 + CSS extract 优化（DEBT-06/07/08/09）

**阶段目标与核心任务：**
升级 OSS SDK 2.8.3→3.17.4、Redisson 3.11.3→3.36.0、移除 Jackson 手动版本号、修正 annotations RELEASE→24.0.1、修正 groupId org.example→com.elink（26处）、修复 linkos/tycvs 的 css.extract: false→true。

**具体执行步骤与操作要求：**

1. OSS SDK 升级（需手动适配 API 3.x 不兼容变化）
2. Redisson 升级
3. 移除 Jackson 手动版本号（删除 `<version>2.18.0</version>`）
4. 修正 annotations 版本：`sed -i 's|<version>RELEASE</version>|<version>24.0.1</version>|' pom.xml`
5. 批量替换 groupId：`find . -name "pom.xml" -exec sed -i 's|<groupId>org\.example</groupId>|<groupId>com.elink</groupId>|g' {} +`
6. CSS extract：编辑 linkos/vue.config.js 和 tycvs/vue.config.js，将 `extract: false` 改为 `extract: true`
7. 全量编译 + 前端构建验证

**输入输出规范：**
- 输入：当前 POM 依赖版本、前端 vue.config.js
- 输出：升级后的依赖、修正后的 groupId、优化后的前端配置

**变更文件清单：**
- `/work/elink-ai/elink-work/pom.xml`（OSS SDK 2.8.3→3.17.4、移除 Jackson 手动版本、annotations RELEASE→24.0.1、groupId org.example→com.elink）
- `/work/elink-ai/elink-work/sunmax-common/pom.xml`（Redisson 3.11.3→3.36.0）
- 各子模块 `pom.xml`（groupId 替换）
- `/work/elink-ai/elink-web/linkos/vue.config.js`（extract: false → true）
- `/work/elink-ai/elink-web/tycvs/vue.config.js`（extract: false → true）
- OSS SDK API 调用适配文件（3.x 不兼容变化）

**质量验收标准：**
- [ ] `grep 'RELEASE' pom.xml` 返回空
- [ ] `grep '2.8.3' pom.xml` 返回空
- [ ] `grep '3.11.3' sunmax-common/pom.xml` 返回空
- [ ] `grep 'org.example' --include="pom.xml" -r . | wc -l` 返回 0
- [ ] linkos/vue.config.js 和 tycvs/vue.config.js 中 extract 为 true
- [ ] 本地服务器验证：`./hot-reload.sh reload <受影响服务>` 确认启动成功；前端 `node dev-manager.js restart linkos && node dev-manager.js restart tycvs` 确认构建正常
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：OSS 文件上传/下载功能正常；前端页面样式无异常（CSS extract 生效后样式一致）

---

## P3-C | 收窄异常捕获 + 清理 TODO/FIXME + 关闭 Hibernate 统计（DEBT-03/04）

**阶段目标与核心任务：**
1. 将 `catch(Exception)` （357处/92文件）收窄为具体异常类型
2. 处理全部 TODO/FIXME/HACK 注释（16处/10文件）
3. 关闭4个服务的 `hibernate.generate_statistics: true`

**具体执行步骤与操作要求：**

1. 统计并逐服务审查 catch(Exception)，替换为具体异常类型
2. 列出全部 TODO/FIXME：`grep -rn 'TODO\|FIXME\|HACK' --include="*.java" . | grep -v target`
3. 逐条处理：实现功能/删除过时注释/转为Issue
4. 关闭 Hibernate 统计：
   ```bash
   for svc in protocol-service data-service device-service configure-service; do
     sed -i 's/generate_statistics: true/generate_statistics: false/' \
       "${svc}/src/main/resources/application.yml"
   done
   ```
5. 全量编译

**输入输出规范：**
- 输入：含宽泛异常捕获和 TODO 的 Java 文件、含 generate_statistics: true 的 yml
- 输出：收敛后的异常处理、清理后的注释、关闭统计的配置

**变更文件清单：**
- 含 `catch(Exception)` 的 Java 文件（357处/92文件，收窄为具体异常类型）
- 含 TODO/FIXME/HACK 的 Java 文件（16处/10文件）
- protocol-service/data-service/device-service/configure-service 的 `application.yml`（generate_statistics: true → false）

**质量验收标准：**
- [ ] `grep -rn 'generate_statistics: true' --include="*.yml" . | wc -l` 返回 0
- [ ] TODO/FIXME 清单已审查完毕
- [ ] catch(Exception) 数量显著减少（目标降低 50%+），剩余有合理理由
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] 本地服务器验证：`./hot-reload.sh reload <受影响服务>` 确认启动成功、异常处理逻辑无退化
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：核心业务功能冒烟测试通过，异常场景（如参数校验失败、资源不存在等）返回正确的错误信息和 HTTP 状态码

---

## P3-C2 | 修正超时与连接池性能参数（ARCH-07）

**阶段目标与核心任务：**
修正以下性能配置问题：
1. Gateway connect-timeout 从 600000ms（10分钟）→ 3000ms
2. Gateway response-timeout 从 60s → 15s
3. HikariCP idle-timeout 从 600000ms → 60000ms（8个服务）
4. Redis timeout 从 60s → 10s（9个服务）
5. Hystrix timeout.enabled 当前为 false，需启用或迁移至 Resilience4j

**具体执行步骤与操作要求：**

1. 修正 Gateway 超时：
   ```bash
   sed -i 's/connect-timeout: 600000/connect-timeout: 3000/' sunmax-gateway/src/main/resources/application.yml
   sed -i 's/response-timeout: 60s/response-timeout: 15s/' sunmax-gateway/src/main/resources/application.yml
   ```
2. 修正 HikariCP idle-timeout（8个服务）：
   ```bash
   for svc in auth-service system-service crontab-service data-service devops-service \
              together-service webapp-service configure-service; do
     sed -i 's/idle-timeout: 600000/idle-timeout: 60000/' "${svc}/src/main/resources/application.yml"
   done
   ```
3. 修正 Redis timeout（9个服务）：
   ```bash
   for svc in auth-service system-service crontab-service data-service device-service \
              devops-service protocol-service together-service webapp-service configure-service; do
     sed -i 's/timeout: 60s/timeout: 10s/' "${svc}/src/main/resources/application.yml"
   done
   ```
4. 启用 Hystrix 超时（如仍使用）：
   ```bash
   sed -i 's/enabled: false/enabled: true/' sunmax-gateway/src/main/resources/application.yml
   ```
   **注意**：如已在 P2-2a 中迁移至 Resilience4j，则跳过此步并确认 Resilience4j 配置正确

5. 全量编译

**输入输出规范：**
- 输入：当前 Gateway/HikariCP/Redis/Hystrix 配置
- 输出：优化后的超时和连接池参数

**变更文件清单：**
- `/work/elink-ai/elink-work/sunmax-gateway/src/main/resources/application.yml`（connect-timeout 600000→3000, response-timeout 60s→15s, Hystrix timeout.enabled false→true）
- auth-service/system-service/crontab-service/data-service/devops-service/together-service/webapp-service/configure-service 的 `application.yml`（idle-timeout 600000→60000）
- auth-service/system-service/crontab-service/data-service/device-service/devops-service/protocol-service/together-service/webapp-service/configure-service 的 `application.yml`（Redis timeout 60s→10s）

**质量验收标准：**
- [ ] `grep 'connect-timeout: 600000' sunmax-gateway/src/main/resources/application.yml` 返回空
- [ ] `grep -rn 'idle-timeout: 600000' --include="*.yml" . | wc -l` 返回 0
- [ ] `grep -rn 'timeout: 60s' --include="*.yml" . | wc -l` 返回 0（Redis）
- [ ] `mvn clean compile -DskipTests -T 4` BUILD SUCCESS
- [ ] 本地服务器验证：`./hot-reload.sh reload sunmax-gateway` 确认网关启动成功；逐服务执行 `./hot-reload.sh reload <service>` 确认连接池参数生效
- [ ] 热更新验证：`./hot-reload.sh status` 确认所有服务 healthy
- [ ] 功能一致性：前端通过 Gateway 访问各服务接口响应正常，无超时错误；长轮询场景（如有）需单独验证

**注意事项**：Gateway connect-timeout 从 10 分钟改为 3 秒需确认前端无长轮询场景依赖

---

## P3-D | 性能基准测试（R1）

**阶段目标与核心任务：**
建立当前系统性能基线（R1），为最终优化对比提供数据支撑。

**具体执行步骤与操作要求：**

1. 准备 JMeter 或 k6 压测脚本覆盖以下场景：
   - 用户登录/Token 刷新
   - 设备列表查询
   - 站点数据查询
   - 实时数据推送（WebSocket）
   - 告警数据写入
2. 执行 3 轮压测，每轮至少 5 分钟，取中位数
3. 采样以下指标：
   - API P95/P99 响应时间
   - TPS（核心接口）
   - 错误率
   - JVM GC 停顿（jstat/GC日志）
   - 容器内存/CPU
   - 数据库活跃连接数
   - 前端 FCP/LCP（Lighthouse 采样）
4. 将结果写入 PROGRESS_REPORT.md 的 R1 基线章节

**输入输出规范：**
- 输入：运行中的生产/测试环境
- 输出：R1 性能基线报告，写入 PROGRESS_REPORT.md

**变更文件清单：**
- `/work/elink-ai/PROGRESS_REPORT.md`（新增 R1 性能基线章节）
- JMeter/k6 压测脚本文件

**质量验收标准：**
- [ ] 报告包含全部 8 项关键指标的采样值
- [ ] 至少覆盖 5 个核心业务场景
- [ ] 数据经 3 轮验证取中位数
- [ ] 本地服务器验证：压测期间 `./hot-reload.sh status` 确认所有服务运行稳定
- [ ] 热更新验证：无需热更新（测试/文档变更）
- [ ] 功能一致性：压测结束后核心业务功能正常，无服务异常

---

# ═══════════════════════════════════════════════════════════
# PHASE-4：前端现代化改造
# ═══════════════════════════════════════════════════════════

> **阶段目标**：统一前端技术栈，提取公共模块，引入 TypeScript，迁移至 Vite 构建。
>
> **前置条件**：PHASE-3 完成（后端 API 稳定后再做前端改造）
>
> **衔接关系**：P4-A 必须最先完成，P4-BC 依赖 P4-A 的公共包，P4-D 在最后执行。

---

## P4-A | 创建 @elink/shared 公共包

**阶段目标与核心任务：**
从3个前端项目中提取公共模块（request/http、auth、utils），创建内部 npm 包 `@elink/shared`，配置 monorepo workspace。

**具体执行步骤与操作要求：**

1. 在 `elink-web` 下创建目录结构：
   ```
   packages/shared/
   ├── package.json
   ├── src/
   │   ├── index.js
   │   ├── http/
   │   │   └── request.js    ← 统一 axios 封装（合并3项目的拦截器逻辑）
   │   ├── auth/
   │   │   └── index.js      ← 统一 token 管理
   │   └── utils/
   │       └── index.js      ← 公共工具函数
   ```
2. 创建 `elink-web/pnpm-workspace.yaml`：
   ```yaml
   packages:
     - 'linkos'
     - 'derms'
     - 'tycvs'
     - 'packages/*'
   ```
3. 合并3个项目的 `request.js` 拦截器逻辑（去重、统一 token 刷新、统一错误处理）
4. 在各项目 `package.json` 中添加 `"@elink/shared": "workspace:*"`
5. 改造各项目引用 `@elink/shared` 的模块
6. 全前端构建验证：
   ```bash
   for proj in linkos derms tycvs; do
     cd "$proj" && npm run build && cd ..
   done
   ```

**输入输出规范：**
- 输入：3 个项目的 request.js、auth、utils 代码
- 输出：@elink/shared 公共包 + pnpm-workspace.yaml + 各项目引用改造

**变更文件清单：**
- `/work/elink-ai/elink-web/packages/shared/package.json`（新建 @elink/shared 公共包）
- `/work/elink-ai/elink-web/packages/shared/src/`（http/request.js、auth/index.js、utils/index.js）
- `/work/elink-ai/elink-web/pnpm-workspace.yaml`（新建 monorepo workspace 配置）
- `/work/elink-ai/elink-web/linkos/package.json`（添加 @elink/shared 依赖）
- `/work/elink-ai/elink-web/derms/package.json`（同上）
- `/work/elink-ai/elink-web/tycvs/package.json`（同上）
- 各项目中引用 @elink/shared 模块的源码文件

**质量验收标准：**
- [ ] `packages/shared/package.json` 存在且 name 为 `@elink/shared`
- [ ] `pnpm-workspace.yaml` 存在且包含 4 个路径
- [ ] 3 个项目均引用 `@elink/shared`
- [ ] 3 个项目 `npm run build` 全部成功
- [ ] 本地服务器验证：`node dev-manager.js start-all` 确认 3 个前端项目启动成功
- [ ] 热更新验证：`node dev-manager.js status` 确认 3 个项目运行中
- [ ] 功能一致性：前端登录/Token 刷新/HTTP 请求拦截功能正常（使用 @elink/shared 的统一封装）

---

## P4-BC | linkos + tycvs 迁移至 Vite + 3项目 Vuex → Pinia

**阶段目标与核心任务：**
1. 将 linkos 和 tycvs 从 Vue CLI 5 迁移至 Vite 6
2. 将 3 个前端项目的 Vuex 4 全部迁移至 Pinia

**具体执行步骤与操作要求：**

**Vite 迁移（linkos 为例，tycvs 同理）：**
1. 安装 Vite 依赖：`npm install -D vite @vitejs/plugin-vue`
2. 创建 `vite.config.js`（参考 derms 配置），迁移 `vue.config.js` 中的 proxy、alias、插件配置
3. 替换 `require.context` → `import.meta.glob`
4. 替换 `process.env.VUE_APP_*` → `import.meta.env.VITE_*`
5. 更新 `package.json` scripts：`"dev": "vite"`, `"build": "vite build"`, `"preview": "vite preview"`
6. 移除 `@vue/cli-service` 依赖
7. 删除 `vue.config.js`

**Pinia 迁移（3 个项目均执行）：**
1. 安装 pinia：`npm install pinia`
2. 逐模块迁移 `store/modules/*.js` → `stores/*.js`，使用 `defineStore` 替代 Vuex modules
3. 组件中 `this.$store` → `useXxxStore()`
4. 更新 `main.js`：`app.use(createPinia())`
5. 移除 vuex 依赖

**全前端构建验证**

**输入输出规范：**
- 输入：linkos/tycvs 的 vue.config.js、3 个项目的 Vuex store
- 输出：vite.config.js、Pinia stores、更新后的 main.js 和 package.json

**变更文件清单：**
- `/work/elink-ai/elink-web/linkos/vite.config.js`（新建，替换 vue.config.js）
- `/work/elink-ai/elink-web/tycvs/vite.config.js`（新建，替换 vue.config.js）
- `/work/elink-ai/elink-web/linkos/vue.config.js`（删除）
- `/work/elink-ai/elink-web/tycvs/vue.config.js`（删除）
- `/work/elink-ai/elink-web/linkos/package.json`（移除 @vue/cli-service，添加 vite 依赖，更新 scripts）
- `/work/elink-ai/elink-web/tycvs/package.json`（同上）
- 3 个项目的 `src/store/` → `src/stores/`（Vuex → Pinia 迁移）
- 3 个项目的 `src/main.js`（Vuex → Pinia 初始化）
- 使用 `require.context`/`process.env.VUE_APP_*` 的源码文件（迁移为 import.meta.glob/import.meta.env.VITE_*）

**质量验收标准：**
- [ ] linkos/tycvs 无 `vue.config.js`（已替换为 `vite.config.js`）
- [ ] linkos/tycvs 的 `package.json` 无 `@vue/cli-service` 依赖
- [ ] 3 个项目 `package.json` 无 `vuex` 依赖，有 `pinia` 依赖
- [ ] 3 个项目 `npm run build` 全部成功
- [ ] 本地服务器验证：`node dev-manager.js restart linkos && node dev-manager.js restart tycvs` 确认 Vite 开发服务器启动成功
- [ ] 热更新验证：`node dev-manager.js status` 确认 3 个项目运行中，HMR 热更新正常
- [ ] 功能一致性：3 个前端项目核心功能（登录、数据查询、状态管理）正常，Pinia Store 数据读写正确

---

## P4-D | TypeScript 渐进式引入

**阶段目标与核心任务：**
在3个前端项目中渐进式引入 TypeScript，启用 `allowJs: true`，从 utils 和 api 层开始将 `.js` 改为 `.ts`。

**具体执行步骤与操作要求：**

1. 每个项目安装 TypeScript 依赖：`npm install -D typescript vue-tsc`
2. 创建 `tsconfig.json`：
   ```json
   {
     "compilerOptions": {
       "target": "ES2020",
       "module": "ESNext",
       "moduleResolution": "bundler",
       "strict": false,
       "allowJs": true,
       "jsx": "preserve",
       "paths": { "@/*": ["src/*"] },
       "baseUrl": "."
     },
     "include": ["src/**/*"]
   }
   ```
3. 从 utils/api 层逐步 `.js` → `.ts`（手动操作，每月推进 10-15 个文件）
4. 组件 SFC 添加 `<script lang="ts">`
5. 新增模块建议启用 `strict: true`

**输入输出规范：**
- 输入：3 个项目的 JS 源码
- 输出：tsconfig.json + 部分迁移的 .ts 文件 + 带有 lang="ts" 的组件

**变更文件清单：**
- `/work/elink-ai/elink-web/linkos/tsconfig.json`（新建）
- `/work/elink-ai/elink-web/derms/tsconfig.json`（新建）
- `/work/elink-ai/elink-web/tycvs/tsconfig.json`（新建）
- 各项目中 `.js` → `.ts` 迁移的文件（utils/api 层优先）
- 添加 `<script lang="ts">` 的 SFC 组件文件

**质量验收标准：**
- [ ] 3 个项目都有 `tsconfig.json` 且 `allowJs: true`
- [ ] `vue-tsc --noEmit` 通过或仅有可控的 type 错误
- [ ] 新增模块（如 @elink/shared）须全部为 `.ts` 且 `strict: true`
- [ ] 本地服务器验证：`node dev-manager.js start-all` 确认 3 个前端项目启动成功，TypeScript 编译无阻断性错误
- [ ] 热更新验证：`node dev-manager.js status` 确认 3 个项目运行中，HMR 正常
- [ ] 功能一致性：已迁移为 .ts 的模块功能正常，JS/TS 混合模式下无运行时错误

---

# ═══════════════════════════════════════════════════════════
# PHASE-5：构建部署与持续优化
# ═══════════════════════════════════════════════════════════

> **阶段目标**：完善 CI/CD 流水线，建立监控告警体系，补全单元测试，执行最终性能回归。
>
> **前置条件**：PHASE-4 完成
>
> **衔接关系**：P5-A/B/C 可并行，P5-D 必须在最后。

---

## P5-A | 配置 CI/CD 流水线

**阶段目标与核心任务：**
创建 CI/CD 流水线覆盖后端 lint+测试+构建、前端 lint+构建、Docker 镜像构建、灰度部署。

**具体执行步骤与操作要求：**

1. 根据项目代码托管平台（GitHub/GitLab）创建对应流水线文件：
   - GitHub: `.github/workflows/ci.yml`
   - GitLab: `.gitlab-ci.yml`
2. 流水线阶段设计：
   ```
   lint → test → build → docker-build → deploy-canary → verify → deploy-full
   ```
3. 后端阶段：`mvn lint:lint` → `mvn test` → `mvn package -DskipTests`
4. 前端阶段：`npm run lint` → `npm run build`
5. Docker 阶段：`docker build`
6. 灰度部署阶段：10% 流量金丝雀 → 观察 1 小时 → 全量

**输入输出规范：**
- 输入：项目仓库
- 输出：CI/CD 配置文件

**变更文件清单：**
- `.github/workflows/ci.yml` 或 `.gitlab-ci.yml`（新建 CI/CD 流水线配置）

**质量验收标准：**
- [ ] CI/CD 配置文件存在且语法合法
- [ ] 流水线可成功触发并完成 lint→test→build 阶段
- [ ] 本地服务器验证：CI/CD 配置语法验证通过（如 `gitlab-ci-lint` 或 `actionlint`）
- [ ] 热更新验证：无需热更新（CI/CD 配置变更）
- [ ] 功能一致性：流水线构建产物与本地 `mvn package` + `npm run build` 产物一致

---

## P5-B | 部署 Prometheus + Grafana 监控体系

**阶段目标与核心任务：**
新增 docker-compose.monitoring.yml，部署监控栈，配置 5 条核心告警规则。

**具体执行步骤与操作要求：**

1. 创建 `docker-compose.monitoring.yml`，包含：
   - Prometheus（9090）
   - Grafana（3000）
   - cAdvisor
   - Node Exporter
2. 创建 `prometheus/prometheus.yml` 配置 Spring Boot Actuator 抓取
3. 创建 `prometheus/alert_rules.yml`，5 条核心规则：
   - 服务健康检查失败 → 立即告警
   - API P99 > 1s → 告警
   - JVM 堆内存 > 85% → 告警
   - MySQL 慢查询 > 3s → 告警
   - 容器重启 > 3次/5分钟 → 告警
4. 配置 Grafana Dashboard（JVM + API + 容器 + MySQL）
5. 启动监控栈并验证数据采集

**输入输出规范：**
- 输入：Docker 环境
- 输出：docker-compose.monitoring.yml + Prometheus 配置 + Grafana Dashboard

**变更文件清单：**
- `/work/elink-ai/elink-work/docker-compose.monitoring.yml`（新建监控栈配置）
- `/work/elink-ai/elink-work/prometheus/prometheus.yml`（新建 Prometheus 抓取配置）
- `/work/elink-ai/elink-work/prometheus/alert_rules.yml`（新建告警规则）
- Grafana Dashboard JSON 配置文件

**质量验收标准：**
- [ ] `docker-compose.monitoring.yml` 存在
- [ ] Prometheus 可通过 9090 端口访问
- [ ] Grafana 可通过 3000 端口访问
- [ ] 5 条核心告警规则已配置
- [ ] 本地服务器验证：`docker-compose -f docker-compose.monitoring.yml up -d` 后 `curl -s http://192.168.2.158:9090/-/healthy` 返回 Prometheus 健康；`curl -s http://192.168.2.158:3000/api/health` 返回 Grafana 健康
- [ ] 热更新验证：`docker ps` 确认 prometheus/grafana/cadvisor/node-exporter 容器运行正常
- [ ] 功能一致性：Prometheus 成功抓取各 Spring Boot Actuator 指标；Grafana Dashboard 展示 JVM/API/容器/MySQL 数据

---

## P5-C | 补全核心单元测试（覆盖率 ≥ 60%）

**阶段目标与核心任务：**
为核心 Service 层补全 JUnit 单元测试，重点覆盖设备管理、订单支付、用户认证、数据采集等关键业务逻辑。

**具体执行步骤与操作要求：**

1. 添加 JaCoCo 插件到父 POM：
   ```xml
   <plugin>
       <groupId>org.jacoco</groupId>
       <artifactId>jacoco-maven-plugin</artifactId>
       <executions>
           <execution><goals><goal>prepare-agent</goal></goals></execution>
           <execution><id>report</id><phase>test</phase><goals><goal>report</goal></goals></execution>
       </executions>
   </plugin>
   ```
2. 取消 `skipTests=true`（仅在 CI 环境中留作可选参数）
3. 逐服务补全测试用例，优先级：device-service > together-service > auth-service > data-service
4. 运行 `mvn test jacoco:report` 验证覆盖率 ≥ 60%

**输入输出规范：**
- 输入：各服务 Service 层源码
- 输出：测试用例代码 + JaCoCo 覆盖率报告

**变更文件清单：**
- `/work/elink-ai/elink-work/pom.xml`（添加 JaCoCo 插件）
- 各服务 `src/test/java/`（新增单元测试用例，优先 device-service/together-service/auth-service/data-service）

**质量验收标准：**
- [ ] `mvn test` 全部通过
- [ ] JaCoCo 报告显示核心 Service 层覆盖率 ≥ 60%
- [ ] 本地服务器验证：`mvn test` 执行期间 `./hot-reload.sh status` 确认运行服务不受影响
- [ ] 热更新验证：无需热更新（测试代码变更不影响运行服务）
- [ ] 功能一致性：测试用例通过后，核心业务功能冒烟测试无退化

---

## P5-D | 性能回归测试（R4 vs R1）

**阶段目标与核心任务：**
执行最终性能回归测试，对比 R1 基线数据，验证优化目标达成。

**具体执行步骤与操作要求：**

1. 使用与 R1 相同的压测脚本和参数
2. 采样全部 8 项关键指标
3. 对比 R4 vs R1 是否达成目标：

| 指标 | 目标 |
|------|------|
| API P95 响应时间 | ≤ 200ms |
| API P99 响应时间 | ≤ 500ms |
| 吞吐量 TPS | ≥ 500 |
| 错误率 | ≤ 0.1% |
| JVM GC 停顿 | ≤ 50ms |
| 数据库连接池利用率 | 60-80% |
| 前端 FCP | ≤ 2s |
| 前端 LCP | ≤ 2.5s |

4. 生成最终回归报告写入 PROGRESS_REPORT.md
5. 创建 Git Tag：`git tag -a v3.0 -m "Elink-AI v3.0 重构升级全部完成"`

**输入输出规范：**
- 输入：运行中的生产/测试环境、R1 基线数据
- 输出：R4 vs R1 对比报告，写入 PROGRESS_REPORT.md

**变更文件清单：**
- `/work/elink-ai/PROGRESS_REPORT.md`（新增 R4 vs R1 对比报告）
- Git Tag v3.0

**质量验收标准：**
- [ ] 回归测试报告已生成
- [ ] 全部优化目标指标达标
- [ ] Git Tag v3.0 已创建
- [ ] 本地服务器验证：压测期间 `./hot-reload.sh status` 确认所有服务运行稳定，无 OOM/异常重启
- [ ] 热更新验证：无需热更新（测试/文档变更）
- [ ] 功能一致性：压测结束后核心业务功能正常，性能指标满足目标（API P95 ≤ 200ms, P99 ≤ 500ms, 错误率 ≤ 0.1%）

---

# ═══════════════════════════════════════════════════════════
# 附录 A：快速回滚指令模板
# ═══════════════════════════════════════════════════════════

```
回滚指定服务（3分钟内完成）：
cd /work/elink-ai/elink-work && ./hot-reload.sh rollback <service-name>

查看服务错误日志：
docker logs --since 5m <service-name> 2>&1 | grep -E "(ERROR|Exception|OOM)"

全量级回滚（多服务异常）：
git checkout <previous-tag>
cd /work/elink-ai/elink-work && mvn clean package -DskipTests -T 4
docker build -t elink-base:latest -f Dockerfile .
./start.sh
```

---

# 附录 B：文档更新触发矩阵

| 指令完成 | REFACTOR_TASKS.md | REFACTOR_EXECUTE.md | PROGRESS_REPORT.md | REFACTOR_PLAN.md |
|----------|:---:|:---:|:---:|:---:|
| P0-* | 更新状态+进度 | 新增执行记录 | 更新进度+风险 | 更新进度总览 |
| P1-COMP-* | 标记完成+时间 | 新增执行记录 | 更新进度+详情 | 更新任务状态 |
| P2-* | 标记完成+时间 | 新增执行记录 | 更新进度+详情 | 更新进度+审计 |
| P3-* | 标记完成+时间 | 新增执行记录 | 更新进度+详情 | 更新进度+审计 |
| P4-* | 标记完成+时间 | 新增执行记录 | 更新进度+详情 | 更新进度+审计 |
| P5-* | 标记完成+时间 | 新增执行记录 | 更新进度+详情 | 更新进度+审计 |

---

# 附录 C：技术债实时统计对照表

以下数据基于 2026-06-05 代码库扫描，各指令执行前应重新统计确认：

| 技术债 | 当前统计 | 方案标注 | 差异 | 涉及指令 |
|--------|---------|---------|------|---------|
| javax.persistence import | 177处 | 232处 | -55 | P2-2c |
| javax其他 import | 44处 | 85处 | -41 | P2-2c |
| javax总文件数 | ~130 | 120文件 | +10 | P2-2c |
| @Api注解 | 1234+处/100+文件 | 121处 | 重新统计 | P2-2a |
| @Transactional | 60文件 | 60文件 | 一致 | P2-2c-2 |
| e.printStackTrace() | 95处/20文件 | 95处/20文件 | 一致 | P3-A |
| System.out/err | 109处/34文件 | 109处/34文件 | 一致 | P3-A |
| catch(Exception) | 357处/92文件 | 357处/92文件 | 一致 | P3-C |
| FeignController | 42个文件 | — | 新增统计 | P2-2c-4 |
| @GeneratedValue | 84处/84文件 | — | 新增统计 | P2-2c |

**注意**：Swagger 注解方案标注 11832 处但实际扫描仅 1234+ 处（100 文件截断），需完整扫描确认后更新。
