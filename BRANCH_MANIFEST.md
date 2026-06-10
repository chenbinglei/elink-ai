# Elink-AI Git 分支清单

> 版本：v1.0 | 编制日期：2026-06-10 | 状态：**已建立**
>
> 基于 REFACTOR_PLAN.md v2.0 和 AI_DIRECTIVES.md v2.0 的分支方案梳理

---

## 一、分支总览

### 1.1 方案定义分支

| 序号 | 分支名称 | 用途 | 基础分支 | 创建状态 |
|------|----------|------|----------|----------|
| 1 | `main` | 生产分支，存放稳定发布版本 | - | ✅ 已存在（本地+远程） |
| 2 | `develop` | 开发集成分支，日常开发合并 | main | ✅ 已存在（本地+远程） |
| 3 | `refactor/phase-0-emergency-fix` | PHASE-0：紧急修复与数据校正 | main | ✅ 已创建 |
| 4 | `refactor/phase-1-security-hardening` | PHASE-1：安全加固与紧急修复 | main | ✅ 已存在（本地+远程） |
| 5 | `refactor/phase-2-framework-upgrade` | PHASE-2：框架升级与核心重构 | refactor/phase-1-security-hardening | ✅ 已创建 |
| 6 | `refactor/phase-3-code-quality` | PHASE-3：代码质量与性能优化 | main | ✅ 已创建 |
| 7 | `refactor/phase-4-frontend-modernize` | PHASE-4：前端现代化改造 | main | ✅ 已创建 |
| 8 | `refactor/phase-5-devops` | PHASE-5：构建部署与持续优化 | main | ✅ 已创建 |

### 1.2 历史遗留分支（非方案定义）

| 分支名称 | 用途 | 说明 |
|----------|------|------|
| `refactor/p1-fastjson-to-jackson` | 旧命名风格的 Fastjson 迁移试验分支 | 属于早期探索性分支，与 `refactor/phase-1-security-hardening` 内容重复，建议后续清理 |
| `refactor/p2-springboot-upgrade` | 旧命名风格的 Spring Boot 升级试验分支 | 同上，属于早期探索性分支，建议后续清理 |
| `refactor/p3-architecture-optimization` | 旧命名风格的架构优化试验分支 | 同上，属于早期探索性分支，建议后续清理 |

### 1.3 方案定义标签

| 标签名称 | 用途 | 创建状态 |
|----------|------|----------|
| `v3.0.0-baseline` | 重构前基线快照 | ✅ 已存在 |
| `v3.0-phase1` | PHASE-1 正式完成版 | ✅ 已存在 |
| `v3.0-phase1-rc1` | PHASE-1 候选版本 | ❌ 待创建 |
| `v3.0-phase{N}-hotfix1` | 各阶段紧急修复 | ❌ 按需创建 |
| `v3.0-phase2` ~ `v3.0-phase5` | PHASE-2~5 正式完成版 | ❌ 待阶段完成后创建 |
| `v3.0` | 全量重构最终完成版 | ❌ 待 P5-D 完成后创建 |

---

## 二、分支关系图

```
main (c8aa1f3 — 基线快照)
├── refactor/phase-0-emergency-fix          ← 基于 main 创建
├── refactor/phase-1-security-hardening     ← 基于 main，已完成（当前分支）
│   └── refactor/phase-2-framework-upgrade  ← 基于 phase-1 创建（继承已完成工作）
├── refactor/phase-3-code-quality           ← 基于 main 创建
├── refactor/phase-4-frontend-modernize     ← 基于 main 创建
└── refactor/phase-5-devops                 ← 基于 main 创建

develop (历史开发分支)
```

---

## 三、分支基点详情

| 分支 | 基点 Commit | 基点说明 | 说明 |
|------|-------------|----------|------|
| `main` | `c8aa1f3` | 初始基线提交 | 重构前代码快照 |
| `develop` | `0efdd99` | 补充文档提交 | 独立于 main 的分支 |
| `refactor/phase-0-emergency-fix` | `c8aa1f3` | main 最新 | 待开始 |
| `refactor/phase-1-security-hardening` | `c8aa1f3` | main 最新 | 包含 PHASE-0~P2-2c-3 全部已完成工作（603c2bf） |
| `refactor/phase-2-framework-upgrade` | `603c2bf` | phase-1 最新 | 继承 PHASE-1 的全部已完成工作 |
| `refactor/phase-3-code-quality` | `c8aa1f3` | main 最新 | 待开始，执行前需合并 PHASE-1/2 到 main |
| `refactor/phase-4-frontend-modernize` | `c8aa1f3` | main 最新 | 待开始，同上 |
| `refactor/phase-5-devops` | `c8aa1f3` | main 最新 | 待开始，同上 |

---

## 四、创建记录

| 操作 | 时间 | 执行人 | 说明 |
|------|------|--------|------|
| 创建 `refactor/phase-0-emergency-fix` | 2026-06-10 | AI Agent | 基于 `main` 创建，已验证 |
| 创建 `refactor/phase-2-framework-upgrade` | 2026-06-10 | AI Agent | 基于 `refactor/phase-1-security-hardening` 创建（继承已完成工作） |
| 创建 `refactor/phase-3-code-quality` | 2026-06-10 | AI Agent | 基于 `main` 创建，已验证 |
| 创建 `refactor/phase-4-frontend-modernize` | 2026-06-10 | AI Agent | 基于 `main` 创建，已验证 |
| 创建 `refactor/phase-5-devops` | 2026-06-10 | AI Agent | 基于 `main` 创建，已验证 |

---

## 五、注意事项

1. **分支基点说明**：`refactor/phase-3-code-quality`、`refactor/phase-4-frontend-modernize`、`refactor/phase-5-devops` 当前基于 `main` 的初始基线。当 PHASE-1/2 被合并至 `main` 后，这些分支应从更新后的 `main` 重新创建或 rebase，以继承已完成的重构工作。
2. **phase-2 分支的特殊处理**：`refactor/phase-2-framework-upgrade` 基于 `refactor/phase-1-security-hardening` 创建而非 `main`，这样可以直接在已完成工作基础上继续推进 P2-2c-4。
3. **历史分支清理建议**：`refactor/p1-fastjson-to-jackson`、`refactor/p2-springboot-upgrade`、`refactor/p3-architecture-optimization` 三个分支并非方案定义分支，建议在 PHASE-1 合并至 `main` 后删除。
4. **标签创建**：各阶段完成标签（`v3.0-phase{N}`）在阶段正式完成并合并至 `main` 后创建，候选标签（`v3.0-phase{N}-rc1`）在阶段 PR 提审时创建。