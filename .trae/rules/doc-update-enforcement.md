# Elink-AI 文档更新强制执行规则

> 本规则为强制执行规则，AI执行任务后必须遵循

## 1. 【强制更新触发条件】

每次执行以下操作后，**必须**触发文档更新流程：
- 完成任何 P1-T* / P2-* 任务
- 回滚任何已执行的操作
- 修改 pom.xml 依赖版本
- 修改 application.yml 配置
- 修改 Docker/docker-compose 配置
- 修改数据库表结构

## 2. 【必须更新的文档清单】

| 优先级 | 文档 | 更新内容 |
|--------|------|----------|
| 1 | `REFACTOR_TASKS.md` | 任务状态标记 + 完成时间 + 进度总览表 |
| 2 | `REFACTOR_EXECUTE.md` | 执行记录章节（过程/问题/方案/验证） |
| 3 | `PROGRESS_REPORT.md` | 进度概览 + 任务明细 + 已完成详情 + 修改记录 |
| 4 | `REFACTOR_PLAN.md` | 进度总览 + 任务状态 + 审计标记 |

## 3. 【更新检查清单】

任务完成后，AI必须自检以下项目：

```
- [ ] REFACTOR_TASKS.md 中任务标题是否已标记 ✅ 已完成
- [ ] REFACTOR_TASKS.md 中是否已记录完成时间
- [ ] REFACTOR_TASKS.md 中进度总览表是否已更新
- [ ] REFACTOR_EXECUTE.md 中是否已新增执行记录
- [ ] REFACTOR_EXECUTE.md 中执行记录是否包含：执行过程、问题、解决方案、验证结果
- [ ] PROGRESS_REPORT.md 中进度概览表是否已更新
- [ ] PROGRESS_REPORT.md 中任务明细表是否已更新
- [ ] PROGRESS_REPORT.md 中是否已新增已完成任务详情
- [ ] PROGRESS_REPORT.md 中修改记录是否已新增一行
- [ ] REFACTOR_PLAN.md 中进度总览表是否已更新
- [ ] REFACTOR_PLAN.md 中任务状态列是否已更新
- [ ] REFACTOR_PLAN.md 中审计问题修复标记是否已更新
- [ ] 所有文档版本号是否已递增
```

## 4. 【禁止行为】

- 禁止在完成任务后跳过文档更新
- 禁止仅更新部分文档（4份必须全部更新）
- 禁止修改验证结果数据（必须如实记录）
- 禁止在未实际验证的情况下标记为已完成
