# elink-web 前端开发环境管理器 v1.0

统一管理 linkos、derms、tycvs 三个前端项目的本地开发环境，支持批量与单项操作，内置热更新与进程管理。

---

## 功能说明

| 功能 | 说明 |
|------|------|
| 批量启动 | 一键启动所有前端项目 |
| 批量停止 | 一键停止所有前端项目 |
| 批量重启 | 一键重启所有前端项目 |
| 单项操作 | 对指定项目执行启动/停止/重启 |
| 状态查看 | 实时查看各项目运行状态、PID、端口 |
| 日志查看 | 查看各项目的启动/停止/运行日志 |
| 热更新 | 开发服务器自动检测代码变更并实时刷新，无需手动重启 |
| 进程管理 | 通过 PID 文件精确跟踪进程，支持端口冲突检测 |
| 依赖检测 | 自动检测 node_modules 是否存在，缺失时自动安装 |

### 项目清单

| 项目名 | 说明 | 框架 | 开发命令 | 端口 |
|--------|------|------|----------|------|
| linkos | 设备管理平台 | Vue CLI 5 + Webpack | `npm run serve` | 9000 |
| derms | 运营管理平台 | Vite 6 | `npm run dev` | 9001 |
| tycvs | 可视化组态平台 | Vue CLI 5 + Webpack | `npm run serve` | 9002 |

---

## 安装与配置

### 环境要求

- Node.js >= 14.0.0
- npm >= 6.0.0
- 各前端项目的依赖已安装（如未安装，脚本会自动执行 `npm install`）

### 配置文件

配置文件为 `dev.config.js`，与执行脚本位于同一目录。修改此文件即可调整项目管理器的行为，无需修改主脚本。

```js
module.exports = {
  rootDir: __dirname,           // 项目根目录
  projects: [                   // 项目列表（可增删）
    {
      name: 'linkos',           // 项目名（用于命令参数）
      description: '设备管理平台',
      dir: 'linkos',            // 相对 rootDir 的目录名
      devCommand: 'npm run serve',
      port: 9000,
      type: 'vue-cli',
    },
    // ... 更多项目
  ],
  log: {
    dir: path.join(__dirname, '.dev-logs'),  // 日志目录
    maxFiles: 10,                            // 保留日志文件数
    level: 'info',                           // 日志级别
  },
  process: {
    healthCheckInterval: 5000,   // 健康检查间隔(ms)
    startupTimeout: 30000,       // 启动超时(ms)
    shutdownTimeout: 10000,      // 停止超时(ms)
    retryCount: 3,               // 启动失败重试次数
    retryDelay: 2000,            // 重试间隔(ms)
  },
};
```

#### 添加新项目

在 `dev.config.js` 的 `projects` 数组中新增一项即可：

```js
{
  name: 'new-project',
  description: '新项目描述',
  dir: 'new-project',
  devCommand: 'npm run dev',
  port: 9003,
  type: 'vite',
}
```

同时在 `package.json` 的 `scripts` 中添加对应的快捷命令（可选）。

---

## 命令参数说明

### 基本格式

```bash
node dev-manager.js <command> [project]
```

### 命令列表

| 命令 | 参数 | 说明 |
|------|------|------|
| `start-all` | 无 | 启动所有项目 |
| `stop-all` | 无 | 停止所有项目 |
| `restart-all` | 无 | 重启所有项目 |
| `start` | `<name>` | 启动指定项目 |
| `stop` | `<name>` | 停止指定项目 |
| `restart` | `<name>` | 重启指定项目 |
| `status` | 无 | 查看所有项目运行状态 |
| `logs` | `[name]` | 查看日志（可选指定项目） |
| `help` | 无 | 显示帮助信息 |

---

## 全部操作命令示例

### 启动所有项目

```bash
node dev-manager.js start-all
# 或
npm run dev:start-all
```

### 停止所有项目

```bash
node dev-manager.js stop-all
# 或
npm run dev:stop-all
```

### 重启所有项目

```bash
node dev-manager.js restart-all
# 或
npm run dev:restart-all
```

### 查看所有项目状态

```bash
node dev-manager.js status
# 或
npm run dev:status
```

---

## 单个项目操作命令示例

### 启动指定项目

```bash
# 启动 linkos
node dev-manager.js start linkos
npm run dev:start:linkos

# 启动 derms
node dev-manager.js start derms
npm run dev:start:derms

# 启动 tycvs
node dev-manager.js start tycvs
npm run dev:start:tycvs
```

### 停止指定项目

```bash
# 停止 linkos
node dev-manager.js stop linkos
npm run dev:stop:linkos

# 停止 derms
node dev-manager.js stop derms
npm run dev:stop:derms

# 停止 tycvs
node dev-manager.js stop tycvs
npm run dev:stop:tycvs
```

### 重启指定项目

```bash
# 重启 linkos
node dev-manager.js restart linkos
npm run dev:restart:linkos

# 重启 derms
node dev-manager.js restart derms
npm run dev:restart:derms

# 重启 tycvs
node dev-manager.js restart tycvs
npm run dev:restart:tycvs
```

### 查看指定项目日志

```bash
# 查看 linkos 日志
node dev-manager.js logs linkos
npm run dev:logs:linkos

# 查看所有日志
node dev-manager.js logs
npm run dev:logs
```

---

## 热更新说明

各项目开发服务器已内置热模块替换（HMR）功能：

- **Vue CLI 项目（linkos、tycvs）**：`vue-cli-service serve` 内置 webpack Hot Module Replacement，修改 `.vue`、`.js`、`.css` 文件后浏览器自动刷新
- **Vite 项目（derms）**：Vite 原生 HMR，修改文件后即时更新，无需刷新整个页面

**使用方式**：启动项目后正常编辑代码即可，开发服务器会自动检测文件变更并推送更新到浏览器，无需手动重启。

---

## 文件结构

```
elink-web/
├── dev-manager.js        # 主脚本
├── dev.config.js         # 配置文件（项目列表、参数等）
├── package.json          # npm 快捷命令
├── DEV_MANAGER_GUIDE.md  # 本文档
├── .dev-pids/            # 运行时 PID 文件（自动生成，勿手动删除）
├── .dev-logs/            # 日志目录（自动生成）
├── linkos/               # 设备管理平台
├── derms/                # 运营管理平台
└── tycvs/                # 可视化组态平台
```

---

## 常见问题及解决方法

### 1. 端口被占用

**现象**：启动时报错 `端口 9000 已被占用`

**解决方法**：
```bash
# 查看占用端口的进程
# Linux/macOS
lsof -i :9000

# Windows
netstat -aon | findstr :9000

# 终止占用进程
node dev-manager.js stop linkos   # 先尝试脚本停止
# 或手动终止
kill -9 <PID>                     # Linux/macOS
taskkill /PID <PID> /F            # Windows
```

### 2. 项目启动超时

**现象**：显示 `启动检测超时`，但进程可能仍在初始化

**原因**：首次启动或机器性能较低时，编译可能超过默认 30 秒超时

**解决方法**：
- 在 `dev.config.js` 中增大 `process.startupTimeout`（如 60000）
- 使用 `node dev-manager.js status` 确认项目是否实际已启动

### 3. PID 文件与实际进程不一致

**现象**：状态显示未运行，但实际端口有服务

**原因**：手动杀进程或系统异常退出后 PID 文件未清理

**解决方法**：
```bash
# 先尝试脚本停止（会自动检测端口占用）
node dev-manager.js stop linkos

# 或手动清理 PID 文件
rm .dev-pids/linkos.pid
```

### 4. node_modules 缺失

**现象**：启动时自动执行 `npm install` 但安装失败

**解决方法**：
```bash
# 手动进入项目目录安装依赖
cd linkos && npm install
```

### 5. Windows 下脚本执行权限问题

**现象**：PowerShell 报执行策略错误

**解决方法**：
```powershell
# 以管理员身份执行
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### 6. 多个管理器实例冲突

**现象**：同时运行多个终端执行脚本导致 PID 文件冲突

**解决方法**：确保同一时间只有一个管理器实例在操作同一项目。如果状态混乱，先 `stop-all` 清理。

---

## 跨平台兼容性

本脚本已针对以下平台进行适配：

| 平台 | 支持 | 说明 |
|------|------|------|
| macOS | ✓ | 使用 `lsof` 检测端口，`SIGTERM`/`SIGKILL` 终止进程 |
| Linux | ✓ | 同 macOS |
| Windows | ✓ | 使用 `netstat` 检测端口，`taskkill` 终止进程 |

---

## 扩展指南

### 添加新项目

1. 在 `dev.config.js` 的 `projects` 数组中添加项目配置
2. （可选）在 `package.json` 的 `scripts` 中添加快捷命令
3. 无需修改 `dev-manager.js` 主脚本

### 修改默认端口

编辑 `dev.config.js` 或各项目自身的配置文件：
- Vue CLI：`vue.config.js` 中 `devServer.port`
- Vite：`vite.config.js` 中 `server.port`

### 自定义日志级别

在 `dev.config.js` 中修改 `log.level`：
- `debug`：输出所有调试信息
- `info`：默认级别，输出关键操作信息
- `warn`：仅输出警告和错误
- `error`：仅输出错误
