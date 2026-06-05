# Elink-AI 重构升级任务拆解清单

> 基于 REFACTOR_PLAN.md v1.2 生成 | 创建日期：2026-06-03 | 最后更新：2026-06-04
>
> 每条任务包含：任务编号、指令语句、精确执行命令、完成标识
>
> **使用方式：** 逐条将【指令语句】放入AI对话框，AI按【执行命令】操作，完成后核对【完成标识】

---

## 项目进度总览

| 阶段 | 任务总数 | 已完成 | 进行中 | 待开始 | 完成率 |
|------|---------|--------|--------|--------|--------|
| PHASE-1 | 9+1(验证) | 10 | 0 | 0 | 100% |
| PHASE-2 | 3 | 0 | 0 | 3 | 0% |
| PHASE-3 | - | 0 | 0 | - | 0% |
| PHASE-4 | - | 0 | 0 | - | 0% |
| PHASE-5 | - | 0 | 0 | - | 0% |

### 已完成任务记录

| 任务编号 | 任务名称 | 完成时间 | 执行人 | 备注 |
|----------|----------|----------|--------|------|
| P1-T1 | 替换 Fastjson 1.2.0 为 fastjson2 2.0.52 | 2026-06-03 | AI | 100个Java文件150处import全部迁移，编译通过 |
| P1-T2 | 收紧 CORS 策略 | 2026-06-03 | AI | 已配置3个业务域名白名单，编译通过 |
| P1-T3 | JPA ddl-auto 从 update 改为 validate | 2026-06-03 | AI | 10个业务服务全部替换，编译通过 |
| P1-T4 | 清除前端硬编码 IP 地址 | 2026-06-03 | AI | 3个前端项目8个文件替换，编译通过 |
| P1-T5 | 修正 HikariCP 连接池参数 | 2026-06-03 | AI | maximum-pool-size→30+leak-detection→30000，编译通过 |
| P1-T6 | 修正 crontab-service JAR 名拼写 | 2026-06-03 | AI | 6处scrontab→crontab替换，编译通过 |
| P1-T7 | OAuth2 client-secret 硬编码外置 | 2026-06-03 | AI | 9个服务全部替换为${OAUTH2_CLIENT_SECRET}，编译通过 |
| P1-T8 | configure-service 平台密钥硬编码外置 | 2026-06-03 | AI | yml 5个密钥+Java 1处硬密钥全部外置，编译通过 |
| P1-T9 | 数据库连接 useSSL 修复 | 2026-06-03 | AI | 13条JDBC连接useSSL=true+移除autoReconnect，编译通过 |
| P1-V | PHASE-1 全量验证 | 2026-06-03 | AI | 编译+打包通过，9项残留0，修复1处遗漏IP，100%完成 |

---

## PHASE-1：安全加固与紧急修复

> 优先级：P0/P1 | 前置条件：无 | 目标：消除所有安全隐患

---

### P1-T1 | 替换 Fastjson 1.2.0 为 fastjson2 2.0.52（SEC-01） ✅ 已完成

**完成时间：** 2026-06-03

**指令语句：**
> 在 elink-work 全局替换 Fastjson 1.2.0 为 fastjson2 2.0.52，修改父POM依赖声明，并替换全部100个Java文件中的150处import语句，从 `com.alibaba.fastjson` 迁移至 `com.alibaba.fastjson2`，同时适配API差异。

**执行命令：**
```bash
# 1. 修改父POM依赖声明（分步替换，避免sed多行匹配失败）
cd /work/elink-ai/elink-work
sed -i 's|<groupId>com.alibaba</groupId><!-- fastjson -->|<groupId>com.alibaba.fastjson2</groupId>|' pom.xml
sed -i 's|<artifactId>fastjson</artifactId>|<artifactId>fastjson2</artifactId>|' pom.xml
sed -i 's|<version>1.2.0</version>|<version>2.0.52</version>|' pom.xml

# 2. 全局替换Java import语句
find . -name "*.java" -exec sed -i \
  -e 's/import com\.alibaba\.fastjson\.JSON;/import com.alibaba.fastjson2.JSON;/g' \
  -e 's/import com\.alibaba\.fastjson\.JSONObject;/import com.alibaba.fastjson2.JSONObject;/g' \
  -e 's/import com\.alibaba\.fastjson\.JSONArray;/import com.alibaba.fastjson2.JSONArray;/g' \
  -e 's/import com\.alibaba\.fastjson\.TypeReference;/import com.alibaba.fastjson2.TypeReference;/g' \
  {} +

# 3. 全量编译验证
mvn clean compile -DskipTests -T 4

# 4. 统计残留
grep -rn 'import com\.alibaba\.fastjson\.' --include="*.java" . | wc -l
```

**完成标识：**
- [√] `grep -rn 'import com.alibaba.fastjson.' --include="*.java" . | wc -l` 返回 0 （验证通过：0处残留）
- [√] `mvn clean compile -DskipTests` 返回 BUILD SUCCESS（验证通过：编译成功）
- [√] 父POM中无 `com.alibaba:fastjson:1.2.0` 依赖（验证通过：已替换为com.alibaba.fastjson2:fastjson2:2.0.52）

---

### P1-T2 | 收紧 CORS 策略（SEC-02） ✅ 已完成

**完成时间：** 2026-06-03

**指令语句：**
> 将 sunmax-gateway 的 CORS 配置中 `allowed-origins: "*"` 替换为具体业务域名白名单，禁止任意跨域访问。

**执行命令：**
```bash
cd /work/elink-ai/elink-work
# 修改 application.yml 中 CORS 配置
# 将第30行 allowed-origins: "*" 替换为具体域名
sed -i 's/allowed-origins: "\*"/allowed-origins: "https:\/\/os.enlinkitech.com,https:\/\/derms.enlinkitech.com,https:\/\/derms.enlinkitech.com:9536"/' \
  sunmax-gateway/src/main/resources/application.yml

# 验证修改
grep -n 'allowed-origins' sunmax-gateway/src/main/resources/application.yml

# 编译验证
mvn clean package -pl sunmax-gateway -am -DskipTests -T 4
```

**完成标识：**
- [√] `grep 'allowed-origins' sunmax-gateway/src/main/resources/application.yml` 不包含 `"*"`（验证通过：已替换为具体业务域名白名单）
- [√] `mvn package -pl sunmax-gateway -am -DskipTests` 返回 BUILD SUCCESS（验证通过：编译成功）

**实际配置域名白名单：**
- `https://os.enlinkitech.com`
- `https://derms.enlinkitech.com`
- `https://derms.enlinkitech.com:9536`

**注意事项：** 请将示例域名替换为实际业务域名

---

### P1-T3 | JPA ddl-auto 从 update 改为 validate（SEC-03） ✅ 已完成

**完成时间：** 2026-06-03

**指令语句：**
> 将全部10个业务服务的 application.yml 中 `ddl-auto: update` 改为 `ddl-auto: validate`，禁止生产环境自动DDL变更。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 批量替换（10个服务的 application.yml）
for svc in auth-service system-service device-service data-service protocol-service \
           crontab-service devops-service configure-service together-service webapp-service; do
  sed -i 's/ddl-auto: update/ddl-auto: validate/' "${svc}/src/main/resources/application.yml"
done

# 验证无残留
grep -rn 'ddl-auto: update' --include="application.yml" . | wc -l

# 验证已修改
grep -rn 'ddl-auto: validate' --include="application.yml" . | wc -l

# 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [√] `grep -rn 'ddl-auto: update' --include="application.yml" . | wc -l` 返回 0（验证通过：0处残留）
- [√] `grep -rn 'ddl-auto: validate' --include="application.yml" . | wc -l` 返回 10（验证通过：10个服务全部替换）
- [√] `mvn clean compile -DskipTests` 返回 BUILD SUCCESS（验证通过：编译成功）

**注意事项：** 修改前必须确认数据库表结构与Entity完全同步，否则启动会报验证错误

---

### P1-T4 | 清除前端硬编码 IP 地址（SEC-04）

**指令语句：**
> 清除前端3个项目中的硬编码IP地址，将以下6个文件中的硬编码服务器地址替换为环境变量：
> - linkos/src/utils/request.js（第13行 47.110.235.112）
> - linkos/src/api/websocket/webSocket.js（第4行 ws://192.168.2.158:5000）
> - linkos/public/config.js（第4行 47.110.235.112:21002）
> - linkos/vue.config.js（第47行 192.168.2.158:5000）
> - derms/src/api/websocket/webSocket.js（第12行 ws://47.110.235.112:21010）
> - tycvs/src/utils/requestPath.js（第5行 192.168.2.158:5000）

**执行命令：**
```bash
cd /work/elink-ai/elink-web

# === linkos ===

# 1. request.js - 用环境变量替换硬编码IP
# 修改第13行，将硬编码IP改为读取环境变量
sed -i "s|const serverIpAddress = \`\${locationProtocol}//47.110.235.112\${ portNum }\`|const serverIpAddress = process.env.VUE_APP_API_HOST || \`\${locationProtocol}//\${location.hostname}\${ portNum }\`|" \
  linkos/src/utils/request.js

# 2. webSocket.js - 用环境变量替换硬编码WS地址
sed -i "s|const serverIpAddress = 'ws://192.168.2.158:5000'|const serverIpAddress = process.env.VUE_APP_WS_URL || \`${location.protocol === 'https:' ? 'wss:' : 'ws:'}//\${location.host}\`|" \
  linkos/src/api/websocket/webSocket.js

# 3. public/config.js - 用占位符替换硬编码
sed -i 's|iemsUrl: "http:\/\/47.110.235.112:21002\/"|iemsUrl: window.__APP_CONFIG__?.iemsUrl || ""|' \
  linkos/public/config.js

# 4. vue.config.js - 代理目标改用环境变量
sed -i "s|target: 'http://192.168.2.158:5000'|target: process.env.VUE_APP_PROXY_TARGET || 'http://localhost:5000'|" \
  linkos/vue.config.js

# === derms ===

# 5. webSocket.js - 用环境变量替换
sed -i "s|const url = isDev() ? 'ws://47.110.235.112:21010' : formalIpAddress|const url = isDev() ? (import.meta.env.VITE_WS_URL || 'ws://localhost:5000') : formalIpAddress|" \
  derms/src/api/websocket/webSocket.js

# === tycvs ===

# 6. requestPath.js - 用环境变量替换
sed -i "s|const serverIpAddress = \`\${locationProtocol}//192.168.2.158\${portNum}\`|const serverIpAddress = process.env.VUE_APP_API_HOST || \`\${locationProtocol}//\${location.hostname}\${portNum}\`|" \
  tycvs/src/utils/requestPath.js

# 验证：检查是否还有硬编码IP
grep -rn '47\.110\.235\.112\|192\.168\.2\.158\|121\.41\.109\.130' --include="*.js" --include="*.vue" . | \
  grep -v node_modules | grep -v '//' | grep -v '\.map'
```

**完成标识：**
- [ ] `grep -rn '47\.110\.235\.112\|192\.168\.2\.158' --include="*.js" linkos/src/ linkos/public/ derms/src/ tycvs/src/` 返回空（排除注释行）
- [ ] linkos、derms、tycvs 均可 `npm run build` 成功

---

### P1-T5 | 修正 HikariCP 连接池参数（ARCH-04）

**指令语句：**
> 将 auth-service 的 HikariCP maximum-pool-size 从 1000 调整为 30，并新增 leak-detection-threshold: 30000。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 修改 auth-service application.yml
sed -i 's/maximum-pool-size: 1000/maximum-pool-size: 30/' \
  auth-service/src/main/resources/application.yml

# 新增泄漏检测（在 maximum-pool-size 行后插入）
sed -i '/maximum-pool-size: 30/a\      leak-detection-threshold: 30000' \
  auth-service/src/main/resources/application.yml

# 验证
grep -A1 'maximum-pool-size' auth-service/src/main/resources/application.yml

# 编译
mvn clean package -pl auth-service -am -DskipTests -T 4
```

**完成标识：**
- [ ] `grep 'maximum-pool-size' auth-service/src/main/resources/application.yml` 显示 30
- [ ] `grep 'leak-detection-threshold' auth-service/src/main/resources/application.yml` 显示 30000
- [ ] 编译通过

---

### P1-T6 | 修正 crontab-service JAR 名拼写（DEBT-10）

**指令语句：**
> 将 crontab-service 相关的 JAR 文件名从 `scrontab-service-exec.jar` 修正为 `crontab-service-exec.jar`，同步修改 docker-compose.yml、hot-reload.sh 和 crontab-service/pom.xml（`<finalName>scrontab-service</finalName>`）三处。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 修改 docker-compose.yml
sed -i 's/scrontab-service-exec\.jar/crontab-service-exec.jar/' docker-compose.yml

# 2. 修改 hot-reload.sh
sed -i 's/scrontab-service-exec\.jar/crontab-service-exec.jar/' hot-reload.sh
sed -i 's/"crontab-service:scrontab-service-exec.jar"/"crontab-service:crontab-service-exec.jar"/' hot-reload.sh

# 3. 修改 crontab-service/pom.xml 中的 finalName
sed -i 's|<finalName>scrontab-service</finalName>|<finalName>crontab-service</finalName>|' crontab-service/pom.xml

# 验证无残留
grep -rn 'scrontab' --include="*.yml" --include="*.sh" --include="*.xml" .
```

**完成标识：**
- [ ] `grep -rn 'scrontab' --include="*.yml" --include="*.sh" --include="*.xml" .` 返回空
- [ ] `grep 'crontab-service-exec.jar' docker-compose.yml hot-reload.sh` 两处均存在
- [ ] `grep '<finalName>crontab-service</finalName>' crontab-service/pom.xml` 存在

---

### P1-T7 | OAuth2 client-secret 硬编码外置（SEC-02扩展）

**指令语句：**
> 将9个业务服务 application.yml 中硬编码的 `client-secret: sunos-client` 替换为环境变量引用 `${OAUTH2_CLIENT_SECRET:sunos-client}`，生产环境通过 .env 注入真实密钥。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 批量替换9个服务的 OAuth2 client-secret
for svc in webapp-service together-service devops-service protocol-service data-service \
           system-service device-service crontab-service configure-service; do
  sed -i 's/client-secret: sunos-client/client-secret: \${OAUTH2_CLIENT_SECRET:sunos-client}/' \
    "${svc}/src/main/resources/application.yml"
done

# 验证
grep -rn 'client-secret: sunos-client' --include="application.yml" . | wc -l
grep -rn 'OAUTH2_CLIENT_SECRET' --include="application.yml" . | wc -l

# 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] `grep -rn 'client-secret: sunos-client$' --include="application.yml" . | wc -l` 返回 0（行尾无环境变量的硬编码）
- [ ] `grep -rn 'OAUTH2_CLIENT_SECRET' --include="application.yml" . | wc -l` 返回 9
- [ ] 编译通过

---

### P1-T8 | configure-service 平台密钥硬编码外置（SEC-02扩展）

**指令语句：**
> 将 configure-service 中硬编码的平台对接密钥外置为环境变量：
> - application.yml 中的 `id: MACQKWDXI`、`secret: JbhI7olOTAKs2ZNU`、`data-secret: RVPxJ4aiZwMxnGri`
> - HttpResponseUtil.java 中内嵌的 `AESUtil.encrypt("RVPxJ4aiZwMxnGri", ...)` 硬编码密钥

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 替换 application.yml 中的硬编码密钥为环境变量
sed -i 's/id: MACQKWDXI/id: \${PLATFORM_ID:MACQKWDXI}/' \
  configure-service/src/main/resources/application.yml
sed -i 's/secret: JbhI7olOTAKs2ZNU/secret: \${PLATFORM_SECRET:JbhI7olOTAKs2ZNU}/' \
  configure-service/src/main/resources/application.yml
sed -i 's/data-secret: RVPxJ4aiZwMxnGri/data-secret: \${PLATFORM_DATA_SECRET:RVPxJ4aiZwMxnGri}/' \
  configure-service/src/main/resources/application.yml

# 2. 替换 Java 代码中的硬编码（需手动修改 HttpResponseUtil.java）
# 将 AESUtil.encrypt("RVPxJ4aiZwMxnGri", "kWKNeZyRYVCgYlX1", data)
# 改为 AESUtil.encrypt(platformDataSecret, aesKey, data)
# 其中 platformDataSecret 通过 @Value("${platform.data-secret}") 注入

# 验证 application.yml 已修改
grep -n 'PLATFORM_' configure-service/src/main/resources/application.yml

# 编译
mvn clean package -pl configure-service -am -DskipTests -T 4
```

**完成标识：**
- [ ] `configure-service/src/main/resources/application.yml` 中 id/secret/data-secret 使用 `${...}` 环境变量
- [ ] `HttpResponseUtil.java` 中无硬编码密钥字符串
- [ ] 编译通过

---

### P1-T9 | 数据库连接 useSSL=false 修复（数据安全）

**指令语句：**
> 将全部13条JDBC连接字符串中的 `useSSL=false` 改为 `useSSL=true`，确保数据库连接启用SSL加密。同时移除 `autoReconnect=true`（HikariCP已管理连接生命周期）。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 批量替换所有服务的 application.yml
find . -name "application.yml" -exec sed -i \
  -e 's/useSSL=false/useSSL=true/g' \
  -e 's/autoReconnect=true&//g' \
  -e 's/&autoReconnect=true//g' \
  {} +

# 验证
grep -rn 'useSSL=false' --include="application.yml" . | wc -l
grep -rn 'useSSL=true' --include="application.yml" . | wc -l

# 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] `grep -rn 'useSSL=false' --include="application.yml" . | wc -l` 返回 0
- [ ] `grep -rn 'useSSL=true' --include="application.yml" . | wc -l` 返回 13
- [ ] 编译通过

**注意事项：** 启用SSL前需确认MySQL服务端已配置SSL证书，否则连接会失败。内网开发环境可暂时保留useSSL=false，仅生产环境启用

---

### P1-V | PHASE-1 全量验证 ✅ 已完成

**完成时间：** 2026-06-03

**验证结果：**

| 验证项 | 预期 | 实际 | 状态 |
|--------|------|------|------|
| 全量编译 | BUILD SUCCESS | BUILD SUCCESS (48.4s) | ✅ 通过 |
| 全量打包 | BUILD SUCCESS | BUILD SUCCESS (50.3s) | ✅ 通过 |
| JAR完整性 | 11/11 OK | 11/11 OK | ✅ 通过 |
| 1. Fastjson旧import | 0 | 0 | ✅ 通过 |
| 2. CORS通配符 | 0 | 0 | ✅ 通过 |
| 3. ddl-auto:update | 0 | 0 | ✅ 通过 |
| 4. 硬编码IP(代码) | 0 | 0（修复1处遗漏） | ✅ 通过 |
| 5. HikariCP 1000 | 0 | 0 | ✅ 通过 |
| 6. scrontab构建残留 | 0 | 0 | ✅ 通过 |
| 7. OAuth2硬编码secret | 0 | 0 | ✅ 通过 |
| 8. 密钥硬编码(非默认值) | 0 | 0 | ✅ 通过 |
| 9. useSSL=false | 0 | 0 | ✅ 通过 |

**验证过程中修复的问题：**
- 发现并修复了 `configure-service/application.yml` 中 `interflow-url` 遗漏的硬编码IP `1.95.55.247`，已外置为 `${PLATFORM_INTERFLOW_URL:http://1.95.55.247/...}`

**完成标识：**
- [√] 全量编译 BUILD SUCCESS
- [√] 全量打包 BUILD SUCCESS
- [√] 9项残留检查全部返回 0
- [ ] Git Tag v3.0-phase1 待项目负责人确认后创建

---

## PHASE-2：框架升级与核心重构

> 优先级：P1 | 前置条件：PHASE-1完成 | 严禁跳步

---

### P2-2a | Spring Boot 2.3.0 → 2.7.18 过渡升级

**指令语句：**
> 将 Spring Boot 从 2.3.0.RELEASE 升级至 2.7.18，Spring Cloud 从 Hoxton.SR8 升级至 2021.0.9，同时将 Swagger 2.9.2 迁移至 **springdoc-openapi 1.7.0**（Spring Boot 2.7 专用版本，2.x要求Spring Boot 3.x），添加 Spring Boot 2.7 兼容配置，并完成全部1335处Swagger注解迁移。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 修改父POM版本
sed -i 's|<version>2.3.0.RELEASE</version>|<version>2.7.18</version>|' pom.xml
sed -i 's|<spring-cloud.version>Hoxton.SR8</spring-cloud.version>|<spring-cloud.version>2021.0.9</spring-cloud.version>|' pom.xml

# 2. 替换Swagger依赖为SpringDoc 1.7.0（在父POM中替换以下4个依赖）
# 删除:
#   io.springfox:springfox-swagger2:2.9.2
#   io.springfox:springfox-swagger-ui:2.9.2
#   io.swagger:swagger-models:1.5.21
#   com.github.xiaoymin:swagger-bootstrap-ui:1.9.6
# 新增:
#   org.springdoc:springdoc-openapi-ui:1.7.0

# 3. 添加2.7兼容配置（每个服务的application.yml）
# spring.mvc.pathmatch.matching-strategy=ant-path-matcher
# spring.main.allow-circular-references=true

# 4. 全局替换Swagger注解（1335处/100文件）- 完成6类注解映射
find . -name "*.java" -exec sed -i \
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

# 5. 逐服务编译验证
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] 父POM中 spring-boot-starter-parent 版本为 2.7.18
- [ ] 父POM中 spring-cloud.version 为 2021.0.9
- [ ] `grep -rn 'springfox\|swagger-bootstrap' --include="pom.xml" .` 返回空
- [ ] `grep -rn 'org.springdoc' --include="pom.xml" .` 显示 springdoc-openapi-ui:1.7.0
- [ ] `grep -rn '@Api(' --include="*.java" . | grep -v target | wc -l` 返回 0
- [ ] `grep -rn '@ApiModel\|@ApiModelProperty' --include="*.java" . | grep -v target | wc -l` 返回 0
- [ ] `mvn clean compile -DskipTests` BUILD SUCCESS

**注意事项：** Swagger→SpringDoc 迁移建议使用IDE批量替换，注解属性映射复杂（如@Api→@Tag需去掉tags=的方括号）；Spring Boot 2.7 **必须**搭配 springdoc-openapi 1.7.0，2.x版本要求Spring Boot 3.x

---

### P2-2b | Java 8 → Java 17

**指令语句：**
> 将 Java 版本从 8 升级至 17，修改 Maven compiler 配置和 Dockerfile 基础镜像，处理反射访问和内部API兼容性问题。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 修改父POM Java版本
sed -i 's|<maven.compiler.source>8</maven.compiler.source>|<maven.compiler.source>17</maven.compiler.source>|' pom.xml
sed -i 's|<maven.compiler.target>8</maven.compiler.target>|<maven.compiler.target>17</maven.compiler.target>|' pom.xml
sed -i 's|<java.version>8</java.version>|<java.version>17</java.version>|' pom.xml

# 2. 修改Dockerfile基础镜像
sed -i 's|FROM openjdk:8-jre|FROM eclipse-temurin:17-jre|' Dockerfile

# 3. 检查Java 17不兼容代码（反射访问内部API）
grep -rn 'sun\.misc\.\|sun\.reflect\.\|com\.sun\.' --include="*.java" . | grep -v target

# 4. 如需要，在docker-entrypoint.sh中添加JVM参数
# --add-opens java.base/java.lang=ALL-UNNAMED
# --add-opens java.base/java.lang.reflect=ALL-UNNAMED

# 5. 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] 父POM java.version 为 17
- [ ] Dockerfile 使用 eclipse-temurin:17-jre
- [ ] `mvn clean compile -DskipTests` BUILD SUCCESS
- [ ] 无 sun.misc / sun.reflect 等内部API直接调用

---

### P2-2c | Spring Boot 2.7 → 3.3.x + javax→jakarta + OAuth2迁移

**指令语句：**
> 将 Spring Boot 从 2.7.18 升级至 3.3.x，执行 javax→jakarta 命名空间迁移（317处/120文件），迁移 OAuth2 至 spring-authorization-server，补全核心 Service 事务管理。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 升级Spring Boot和Spring Cloud版本
sed -i 's|<version>2.7.18</version>|<version>3.3.6</version>|' pom.xml
sed -i 's|<spring-cloud.version>2021.0.9</spring-cloud.version>|<spring-cloud.version>2023.0.4</spring-cloud.version>|' pom.xml

# 2. javax → jakarta 全局替换
find . -name "*.java" -exec sed -i \
  -e 's/import javax\.persistence\./import jakarta.persistence./g' \
  -e 's/import javax\.annotation\./import jakarta.annotation./g' \
  -e 's/import javax\.websocket\./import jakarta.websocket./g' \
  -e 's/import javax\.servlet\./import jakarta.servlet./g' \
  -e 's/import javax\.validation\./import jakarta.validation./g' \
  -e 's/import javax\.transaction\./import jakarta.transaction./g' \
  {} +

# 3. 替换OAuth2依赖
# 删除: spring-cloud-starter-oauth2
# 新增: spring-authorization-server（auth-service）
# 新增: spring-boot-starter-oauth2-resource-server（其他服务）

# 4. 适配OAuth2配置类
# auth-service: AuthorizationServerConfigurer → 新API适配
# ResourceServerConfigurer → 新API适配

# 5. 使用OpenRewrite辅助迁移（推荐）
# mvn org.openrewrite.maven:rewrite-maven-plugin:run \
#   -Drewrite.activeRecipes=org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_3

# 6. 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] `grep -rn 'import javax\.' --include="*.java" . | grep -v target | wc -l` 返回 0
- [ ] 父POM无 spring-cloud-starter-oauth2 依赖
- [ ] auth-service 包含 spring-authorization-server 依赖
- [ ] `mvn clean compile -DskipTests` BUILD SUCCESS

**注意事项：** 此步骤为最高风险操作，强烈建议使用OpenRewrite自动迁移；OAuth2迁移需逐类适配，不可批量替换

---

### P2-2c-2 | 补全事务管理（ARCH-05）

**指令语句：**
> 逐服务审查Service层，为涉及多表写操作的方法添加 `@Transactional` 注解。重点覆盖：device-service（设备控制/配置写入）、together-service（订单创建/支付）、webapp-service（业务写操作）、system-service（用户权限变更）。区分读/写操作使用不同事务传播级别：读操作用 `@Transactional(readOnly = true)`，写操作用默认传播级别。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 统计当前事务注解覆盖情况
echo "当前@Transactional:"; grep -rn '@Transactional' --include="*.java" . | grep -v target | wc -l
echo "当前写操作方法(无事务):"
# 查找包含save/update/delete/insert/create开头的方法，但所在类缺少@Transactional
grep -rn 'public.*void\s*\(save\|update\|delete\|insert\|create\|add\|modify\|remove\)' --include="*.java" . | grep -v target | grep 'ServiceImpl' | wc -l

# 2. 逐服务审查并添加注解（手动操作）
# 优先级：device-service > together-service > webapp-service > system-service
# 规则：
#   - 多表写操作：@Transactional(rollbackFor = Exception.class)
#   - 只读查询：@Transactional(readOnly = true)
#   - 类级别添加 @Transactional(readOnly = true)，写方法单独覆盖

# 3. 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] 所有涉及多表写操作的Service方法均有@Transactional注解
- [ ] 纯查询方法标注了@Transactional(readOnly = true)
- [ ] `mvn clean compile -DskipTests` BUILD SUCCESS

---

### P2-2c-3 | Spring Cloud Alibaba版本配置

**指令语句：**
> 在父POM中添加 Spring Cloud Alibaba 2023.0.3.2 的 dependencyManagement 配置，确保与 Spring Boot 3.3.x + Spring Cloud 2023.0.4 兼容。同步更新 Nacos 客户端版本。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 在父POM的 <dependencyManagement> 中添加 Spring Cloud Alibaba BOM
#    <dependency>
#        <groupId>com.alibaba.cloud</groupId>
#        <artifactId>spring-cloud-alibaba-dependencies</artifactId>
#        <version>2023.0.3.2</version>
#        <type>pom</type>
#        <scope>import</scope>
#    </dependency>

# 2. 更新各服务中的 Nacos discovery 依赖为统一版本（由BOM管理）
#    确保 spring-cloud-starter-alibaba-nacos-discovery 无硬编码版本号

# 3. 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] 父POM包含 spring-cloud-alibaba-dependencies:2023.0.3.2
- [ ] 子模块无 Nacos 依赖硬编码版本号
- [ ] `mvn clean compile -DskipTests` BUILD SUCCESS

---

### P2-2c-4 | Feign调用重构（ARCH-06）

**指令语句：**
> 重构微服务间的 Feign 调用架构：将当前使用 FeignController 代理的调用模式，改为服务间直接通过 Feign Client 接口调用。定义统一的 Feign Client 接口（放在 sunmax-common 的 feign 包中），删除各服务的 FeignController 代理类。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 统计当前FeignController数量
find . -name "*FeignController*" -o -name "*Feign*Controller*" | grep -v target

# 2. 重构步骤（手动操作为主）：
#   a. 在 sunmax-common 中创建 feign 包
#   b. 为每个服务定义 @FeignClient 接口，声明实际API方法
#   c. 各服务实现自己的 FeignClient 接口（Controller层实现）
#   d. 调用方直接注入 FeignClient 接口，删除 FeignController 代理
#   e. 统一异常处理（FeignFallbackFactory）

# 3. 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] `find . -name "*FeignController*" | grep -v target | wc -l` 返回 0
- [ ] sunmax-common/feign 包包含所有跨服务调用接口
- [ ] `mvn clean compile -DskipTests` BUILD SUCCESS

**注意事项：** 此任务工作量最大，涉及所有微服务，建议在P2-2c的主体迁移（jakarta + OAuth2）完成并通过验证后再执行

---

## PHASE-3：代码质量与性能优化

> 优先级：P2 | 前置条件：PHASE-2完成

---

### P3-A | 清理 e.printStackTrace() 和 System.out/err.print（DEBT-01/02）

**指令语句：**
> 将全部 e.printStackTrace()（95处/20文件）替换为 log.error，将 System.out.println/System.err.println（109处/34文件）替换为 log.info/log.error，**同时确保每个修改的类中都存在 SLF4J Logger 声明 `private static final Logger log = LoggerFactory.getLogger(XxxClass.class)`**。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 统计当前数量
echo "e.printStackTrace():"; grep -rn 'e\.printStackTrace()' --include="*.java" . | grep -v target | wc -l
echo "System.out/err:"; grep -rn 'System\.\(out\|err\)\.print' --include="*.java" . | grep -v target | wc -l

# 2. 批量替换（需人工审查上下文后执行）
# e.printStackTrace() → log.error("操作描述", e)
# System.out.println(xxx) → log.info("{}", xxx)
# System.err.println(xxx) → log.error("{}", xxx)

# 3. 检查Logger声明完整性（关键步骤）
# 对每个修改的文件，确认存在以下声明之一：
#   private static final Logger log = LoggerFactory.getLogger(XxxClass.class);
#   或使用 @Slf4j (Lombok注解)
# 缺少Logger声明的文件需补全，否则编译报错

# 自动查找缺少Logger声明的修改文件：
grep -rln 'e\.printStackTrace()\|System\.\(out\|err\)\.print' --include="*.java" . | grep -v target | while read f; do
  if ! grep -q 'Logger log\|@Slf4j' "$f"; then
    echo "缺少Logger: $f"
  fi
done

# 4. 验证残留
grep -rn 'e\.printStackTrace()\|System\.\(out\|err\)\.print' --include="*.java" . | grep -v target | wc -l
```

**完成标识：**
- [ ] `grep -rn 'e\.printStackTrace()' --include="*.java" . | grep -v target | wc -l` 返回 0
- [ ] `grep -rn 'System\.\(out\|err\)\.print' --include="*.java" . | grep -v target | wc -l` 返回 0
- [ ] 所有修改文件均包含Logger声明或@Slf4j注解

---

### P3-B | 依赖版本升级 + CSS extract 优化

**指令语句：**
> 升级以下依赖：OSS SDK 2.8.3→3.17.4、Redisson 3.11.3→3.36.0、Jackson移除手动版本号、annotations RELEASE→24.0.1、groupId org.example→com.elink（26处）；修复 linkos/tycvs 的 css.extract: false→true。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. OSS SDK升级
sed -i 's|<version>2.8.3</version>|<version>3.17.4</version>|' pom.xml
# 注意: OSS SDK 3.x API 与 2.x 不兼容，需手动适配代码

# 2. Redisson升级（在sunmax-common/pom.xml中）
sed -i 's|<version>3.11.3</version>|<version>3.36.0</version>|' sunmax-common/pom.xml

# 3. 移除Jackson手动版本号（让Spring Boot BOM管理）
# 删除 pom.xml 中 jackson-core 的 <version>2.18.0</version>

# 4. 修正annotations版本
sed -i 's|<version>RELEASE</version>|<version>24.0.1</version>|' pom.xml

# 5. 批量替换groupId（26处/13个pom.xml）
find . -name "pom.xml" -exec sed -i 's|<groupId>org\.example</groupId>|<groupId>com.elink</groupId>|g' {} +

# 6. CSS extract优化
sed -i 's/extract: false/extract: true/' /work/elink-ai/elink-web/linkos/vue.config.js
sed -i 's/extract: false/extract: true/' /work/elink-ai/elink-web/tycvs/vue.config.js

# 7. 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] `grep 'RELEASE' pom.xml` 返回空
- [ ] `grep '2.8.3' pom.xml` 返回空
- [ ] `grep '3.11.3' sunmax-common/pom.xml` 返回空
- [ ] `grep 'org.example' --include="pom.xml" -r . | wc -l` 返回 0
- [ ] linkos/vue.config.js 和 tycvs/vue.config.js 中 extract 为 true

---

### P3-C | 收窄异常捕获 + 清理TODO/FIXME + 关闭Hibernate统计（DEBT-03/04）

**指令语句：**
> 1. 将 `catch(Exception e)` （357处/92文件）收窄为具体异常类型（IOException/SQLException/BusinessException等），逐服务审查推进。
> 2. 处理全部 TODO/FIXME/HACK 注释（16处/10文件），逐条评估：实现/删除/转为GitHub Issue跟踪。
> 3. 将 protocol-service、data-service、device-service、configure-service 的 `hibernate.generate_statistics: true` 改为 `false`，避免生产环境性能损耗。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# === DEBT-03: 收窄异常捕获 ===
# 1. 统计当前数量
echo "catch(Exception):"; grep -rn 'catch(Exception' --include="*.java" . | grep -v target | wc -l

# 2. 逐服务审查并替换（手动操作为主）
# 替换原则：
#   catch(Exception e) → catch(IOException | SQLException e)  （根据方法签名中实际抛出的异常）
#   catch(Exception e) → catch(BusinessException e)           （自定义业务异常）
#   保留 Exception 兜底的场景：顶层统一异常处理器、调度任务入口
# 建议按服务优先级：device-service > together-service > auth-service > data-service

# === DEBT-04: 清理TODO/FIXME ===
# 3. 列出所有TODO/FIXME
grep -rn 'TODO\|FIXME\|HACK' --include="*.java" . | grep -v target

# 4. 逐条处理：实现功能/删除过时注释/转为Issue

# === hibernate.generate_statistics ===
# 5. 关闭4个服务的Hibernate统计
for svc in protocol-service data-service device-service configure-service; do
  sed -i 's/generate_statistics: true/generate_statistics: false/' \
    "${svc}/src/main/resources/application.yml"
done

# 验证
echo "catch(Exception) 残留:"; grep -rn 'catch(Exception' --include="*.java" . | grep -v target | wc -l
echo "generate_statistics: true 残留:"; grep -rn 'generate_statistics: true' --include="*.yml" . | wc -l

# 全量编译
mvn clean compile -DskipTests -T 4
```

**完成标识：**
- [ ] `grep -rn 'generate_statistics: true' --include="*.yml" . | wc -l` 返回 0
- [ ] TODO/FIXME清单已审查完毕，无未处理项
- [ ] catch(Exception)数量显著减少（目标降低50%+），剩余均有合理理由
- [ ] `mvn clean compile -DskipTests` BUILD SUCCESS

**注意事项：** catch(Exception)收窄工作量大（357处），建议分服务逐步推进，每个服务审查完成后单独提交

---

### P3-D | 性能基准测试（R1）

**指令语句：**
> 建立 PHASE-3 完成后的性能基准线（R1），记录API响应时间、吞吐量、GC停顿、前端加载等指标。

**执行命令：**
```bash
# 使用JMeter或k6执行压测脚本（需预先准备脚本）
# k6 run --out json=results.json performance-test.js
# 或 jmeter -n -t test-plan.jmx -l results.jtl

# 报告输出路径
# /work/elink-ai/elink-work/logs/perf_baseline_R1_YYYYMMDD.html

# 关键采样指标（手动记录至报告）：
# - API P95响应时间
# - API P99响应时间
# - 吞吐量(TPS)
# - 错误率
# - JVM GC停顿时间
# - 前端FCP/LCP
```

**完成标识：**
- [ ] R1基线报告已生成
- [ ] 报告中包含全部8项关键指标的采样值

---

## PHASE-4：前端现代化改造

> 优先级：P2 | 前置条件：PHASE-3完成

---

### P4-A | 创建 @elink/shared 公共包

**指令语句：**
> 在 elink-web 下创建 packages/shared 目录，从3个项目中提取公共模块：request.js → @elink/shared/http、auth.js → @elink/shared/auth、公共 utils → @elink/shared/utils，并配置 pnpm-workspace.yaml。

**执行命令：**
```bash
cd /work/elink-ai/elink-web

# 1. 创建workspace结构
mkdir -p packages/shared/src/{http,auth,utils}

# 2. 创建 pnpm-workspace.yaml
cat > pnpm-workspace.yaml << 'EOF'
packages:
  - 'linkos'
  - 'derms'
  - 'tycvs'
  - 'packages/*'
EOF

# 3. 创建 packages/shared/package.json
cat > packages/shared/package.json << 'EOF'
{
  "name": "@elink/shared",
  "version": "1.0.0",
  "private": true,
  "main": "src/index.js",
  "dependencies": {
    "axios": "^1.7.0"
  }
}
EOF

# 4. 从linkos提取公共模块（手动重构步骤）
# - 复制 request.js → packages/shared/src/http/request.js
# - 复制 auth相关 → packages/shared/src/auth/
# - 复制 公共utils → packages/shared/src/utils/

# 5. 在各项目 package.json 中添加依赖
# "@elink/shared": "workspace:*"
```

**完成标识：**
- [ ] packages/shared/package.json 存在
- [ ] pnpm-workspace.yaml 存在且包含4个路径
- [ ] 3个项目均引用 @elink/shared

---

### P4-BC | linkos + tycvs 迁移至 Vite + Vuex → Pinia

**指令语句：**
> 将 linkos 和 tycvs 从 Vue CLI 5 迁移至 Vite 6，将3个前端项目的 Vuex 4 全部迁移至 Pinia。

**执行命令：**
```bash
cd /work/elink-ai/elink-web

# === Vite迁移（linkos示例，tycvs同理） ===

# 1. 安装Vite依赖
cd linkos
npm install -D vite @vitejs/plugin-vue

# 2. 创建 vite.config.js（参考derms配置）
# 3. 迁移 vue.config.js 中 proxy/alias/插件配置
# 4. 替换 require.context → import.meta.glob
# 5. 替换 process.env.VUE_APP_ → import.meta.env.VITE_
# 6. 更新 package.json scripts:
#    "dev": "vite", "build": "vite build", "preview": "vite preview"
# 7. 移除 @vue/cli-service 依赖

# === Pinia迁移（3个项目均执行） ===

# 1. 安装pinia
npm install pinia

# 2. 逐模块迁移 store/modules/*.js → stores/*.js
#    使用 defineStore 替代 Vuex modules
#    组件中 this.$store → useXxxStore()

# 3. 更新 main.js
# import { createPinia } from 'pinia'
# app.use(createPinia())

# 4. 移除 vuex 依赖

# 全前端构建验证
cd /work/elink-ai/elink-web
for proj in linkos derms tycvs; do
  cd "$proj" && npm ci && npm run lint && npm run build && cd ..
done
```

**完成标识：**
- [ ] linkos/tycvs 无 vue.config.js（已替换为 vite.config.js）
- [ ] linkos/tycvs 的 package.json 无 @vue/cli-service 依赖
- [ ] 3个项目 package.json 无 vuex 依赖，有 pinia 依赖
- [ ] 3个项目 npm run build 全部成功

---

### P4-D | TypeScript 渐进式引入

**指令语句：**
> 在3个前端项目中渐进式引入TypeScript，启用 allowJs: true，从 utils 和 api 层开始将 .js 改为 .ts，SFC组件添加 lang="ts"。

**执行命令：**
```bash
cd /work/elink-ai/elink-web

# 每个项目执行：
for proj in linkos derms tycvs; do
  cd "$proj"

  # 1. 安装TypeScript依赖
  npm install -D typescript vue-tsc

  # 2. 创建 tsconfig.json
  cat > tsconfig.json << 'EOF'
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
EOF

  # 3. 从 utils/api 层逐步 .js → .ts（手动操作）
  # 4. 组件 SFC 添加 <script lang="ts">

  cd ..
done
```

**完成标识：**
- [ ] 3个项目都有 tsconfig.json 且 allowJs: true
- [ ] TypeScript编译无阻断错误（vue-tsc --noEmit 通过或有可控的type错误）

---

## PHASE-5：构建部署与持续优化

> 优先级：P2 | 前置条件：PHASE-4完成

---

### P5-A | 配置 CI/CD 流水线

**指令语句：**
> 创建 CI/CD 流水线配置文件，涵盖后端Lint+测试+构建、前端Lint+构建、Docker镜像构建、灰度部署等阶段。

**执行命令：**
```bash
# 创建 .github/workflows/ci.yml 或 .gitlab-ci.yml
# （文件内容见 REFACTOR_EXECUTE.md 中 PHASE-5 TASK-5A 的YAML模板）

# 验证流水线配置语法
# GitHub: 使用 action-lint 校验
# GitLab: 使用 gitlab-ci-lint 校验
```

**完成标识：**
- [ ] CI/CD 配置文件存在且语法合法
- [ ] 流水线可成功触发并完成 lint→test→build 阶段

---

### P5-B | 部署 Prometheus + Grafana 监控体系

**指令语句：**
> 新增 docker-compose.monitoring.yml，部署 Prometheus + Grafana + cAdvisor + Node Exporter，配置核心告警规则。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 创建 docker-compose.monitoring.yml
# 2. 创建 prometheus/ 目录，包含 prometheus.yml 和告警规则
# 3. 创建 grafana/ 目录，包含 provisioning 配置和 Dashboard JSON
# 4. 启动监控栈
# docker-compose -f docker-compose.monitoring.yml up -d

# 核心告警规则：
# - 服务健康检查失败 → 立即告警
# - API P99 > 1s → 告警
# - JVM堆内存 > 85% → 告警
# - MySQL慢查询 > 3s → 告警
# - 容器重启 > 3次/5分钟 → 告警
```

**完成标识：**
- [ ] docker-compose.monitoring.yml 存在
- [ ] Prometheus可通过9090端口访问
- [ ] Grafana可通过3000端口访问
- [ ] 5条核心告警规则已配置

---

### P5-C | 补全核心单元测试（覆盖率≥60%）

**指令语句：**
> 为核心Service层补全JUnit单元测试，重点覆盖设备管理、订单支付、用户认证、数据采集等关键业务逻辑，目标覆盖率≥60%。

**执行命令：**
```bash
cd /work/elink-ai/elink-work

# 1. 生成覆盖率报告
# mvn jacoco:prepare-agent test jacoco:report

# 2. 查看当前覆盖率
# cat target/site/jacoco/index.html | grep "Total"

# 3. 逐服务补全测试用例（手动编写）
# 优先级：device-service > together-service > auth-service > data-service

# 4. 验证覆盖率
# 再次运行 jacoco:report，确认 ≥60%
```

**完成标识：**
- [ ] `mvn test` 全部通过
- [ ] JaCoCo报告显示核心Service层覆盖率 ≥ 60%

---

### P5-D | 性能回归测试（对比R1基线）

**指令语句：**
> 执行最终性能回归测试，对比R1基线数据，验证优化目标达成情况。

**执行命令：**
```bash
# 使用与R1相同的压测脚本和工具
# 对比以下指标是否达成优化目标：

# | 指标              | R1基线 | 目标     |
# | API P95响应时间    | ___ms  | ≤200ms  |
# | API P99响应时间    | ___ms  | ≤500ms  |
# | 吞吐量(TPS)       | ___    | ≥500    |
# | 错误率            | ___%   | ≤0.1%   |
# | JVM GC停顿        | ___ms  | ≤50ms   |
# | 前端FCP           | ___s   | ≤2s     |
# | 前端LCP           | ___s   | ≤2.5s   |

# 生成最终对比报告
# /work/elink-ai/elink-work/logs/perf_regression_R4_vs_R1_YYYYMMDD.html
```

**完成标识：**
- [ ] 回归测试报告已生成
- [ ] 全部优化目标指标达标
- [ ] 最终Tag v3.0 已创建：`git tag -a v3.0 -m "Elink-AI v3.0 重构升级全部完成"`

---

## 执行依赖关系图

```
PHASE-1（安全加固）
  P1-T1 ──┐
  P1-T2 ──┤
  P1-T3 ──┤ （9个任务可并行执行）
  P1-T4 ──┤
  P1-T5 ──┤
  P1-T6 ──┤
  P1-T7 ──┤  新增：OAuth2 client-secret外置
  P1-T8 ──┤  新增：平台密钥外置
  P1-T9 ──┘  新增：useSSL修复
      │
      ▼
  P1-V（全量验证，9项残留检查）──→ Git Tag: v3.0-phase1
      │
      ▼
PHASE-2（框架升级）── 严禁跳步！
  P2-2a（2.3→2.7 + SpringDoc 1.7.0）──→ 验证
      │
      ▼
  P2-2b（Java 8→17）──→ 验证
      │
      ▼
  P2-2c（2.7→3.3 + jakarta + OAuth2）──→ 验证
      │
      ▼
  P2-2c-2（事务管理补全 ARCH-05）──→ 验证
      │
      ▼
  P2-2c-3（Spring Cloud Alibaba 2023.0.3.2）──→ 验证
      │
      ▼
  P2-2c-4（Feign调用重构 ARCH-06）──→ 验证 ──→ Git Tag: v3.0-phase2
      │
      ▼
PHASE-3（代码质量）
  P3-A ──┐
  P3-B ──┤ （可并行）
  P3-C ──┤  新增：DEBT-03/04 + generate_statistics
  P3-D ──┘
      │ ──→ Git Tag: v3.0-phase3
      ▼
PHASE-4（前端现代化）
  P4-A（@elink/shared）──→ P4-BC（Vite+Pinia）──→ P4-D（TypeScript）
      │ ──→ Git Tag: v3.0-phase4
      ▼
PHASE-5（构建部署）
  P5-A ──┐
  P5-B ──┤ （可并行）
  P5-C ──┤
  P5-D ──┘ ──→ Git Tag: v3.0
```

---

## 异常回滚速查

```bash
# 服务级快速回滚（3分钟内）
cd /work/elink-ai/elink-work && ./hot-reload.sh rollback <service-name>

# 查看服务错误日志
docker logs --since 5m <service-name> 2>&1 | grep -E "(ERROR|Exception|OOM)"

# 强制重建容器（hot-reload.sh失败时）
cd /work/elink-ai/elink-work
docker-compose --env-file /work/elink-ai/.env up -d --force-recreate <service-name>

# 全量级回滚（多服务异常）
git checkout <previous-tag>
cd /work/elink-ai/elink-work && mvn clean package -DskipTests -T 4
docker build -t elink-base:latest -f Dockerfile .
./start.sh
```
