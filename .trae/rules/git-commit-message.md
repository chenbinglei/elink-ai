---
alwaysApply: true
scene: git_message
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

在此处编写规则，自定义 AI 生成提交信息的风格。

# Vue + Java 全栈项目 Git Commit Message 生成规则

当用户请求生成或优化 Git 提交信息时，请严格遵循以下规范：

## 1. 【整体结构与语言】
- 默认使用**中文**输出提交信息。
- 提交信息必须包含 Header（标题行）、Body（正文描述）和 Footer（可选），各部分之间用空行分隔。
- 保持客观、专业的陈述语气，避免使用第一人称（如“我修改了...”），直接使用动词开头（如“修复了...”，“新增了...”）。

## 2. 【Header 规范】
- 格式：<type>(<scope>): <subject>
- type（必填）：使用标准前缀：feat（新功能）、fix（修复Bug）、docs（文档更新）、style（代码格式调整）、refactor（重构）、test（测试相关）、chore（构建/工具变动）。
- scope（必填）：必须标明修改的业务模块和技术栈层级。
  * 技术栈标识：[vue] / [java] / [sql] / [config]
  * 业务模块名：如 auth, user-center, payment, dashboard
  * 组合示例：[vue]/dashboard, [java]/auth, [sql]/user-center
- subject（必填）：简明扼要地描述变更内容，不超过50个字符，不使用句号结尾。

## 3. 【Body 规范】
- 换行后撰写详细描述，解释“做了什么”以及“为什么这么做”。
- 每行长度控制在72个字符以内，长文本需自动换行。
- **Vue 专属要求**：如果涉及前端改动，需在 Body 中指明修改的具体组件名称（如 `UserList.vue`）或状态管理模块（如 Pinia Store）。
- **Java 专属要求**：如果涉及后端改动，需在 Body 中指明受影响的 Controller、Service 或 Entity 类名。
- **联调场景**：若一次提交同时包含前后端改动，必须在 Body 中分条列出 `[vue]` 和 `[java]` 各自的改动点。

## 4. 【特殊场景与高危操作处理】
- **数据库表结构变更**：scope 为 [sql]，并在 body 中提供对应的 SQL 脚本摘要或 Migration 文件名，明确提示是否需要手动执行 DDL/DML。
- **破坏性变更（Breaking Changes）**：如涉及 API 接口路径修改、返回值结构变更，必须在 Footer 中以 `BREAKING CHANGE:` 开头明确说明影响范围及前端适配建议。
- **依赖升级**：如更新了 `pom.xml` 中的 Spring Boot 版本或 `package.json` 中的 Vue 生态库，需在 chore 类型中注明升级前后的版本号。

## 5. 【拆分原则】
- 如果用户的暂存区包含多个不相关的改动（例如既改了前端样式，又修了后端 Bug），强烈建议将其拆分为多个独立的 Commit，并为每个 Commit 单独生成符合上述规范的信息。

# Java
**/target/
*.class
*.jar
*.war
*.ear
*.log
*.iml
.idea/
.settings/
.project
.classpath
.factorypath
*.swp

# Node
**/node_modules/
**/dist/
*.local

# System
.DS_Store
Thumbs.db
*.sw?
*.suo
*.ntvs*
*.njsproj
*.sln
.history/

# Secrets
.env
.env.*
!.env.example
*.pem
*.key
*.p12
*.jks

# Docker
.docker/
docker-compose

# Logs
logs/
*.log.*

# Backups
backups/
*.zip
*.tar.gz

# 依赖目录
node_modules/
sunos/
public/l7-mapbox/
.gitignore
