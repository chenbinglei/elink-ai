/**
 * 前端开发环境配置文件
 * 修改此文件即可调整项目管理器的行为，无需修改主脚本
 *
 * ================================
 * 环境变量配置规范
 * ================================
 *
 * 每个前端项目有两个环境变量文件：
 *
 * 1. .env（本地开发专用，不提交到 Git）
 *    - 包含所有环境变量（含敏感密钥和 IP 地址）
 *    - 被 .gitignore 排除，不会泄露到版本控制
 *    - 开发时由框架自动加载（Vue CLI / Vite 均自动加载 .env）
 *
 * 2. .env.example（配置模板，提交到 Git）
 *    - 包含所有环境变量定义，敏感值使用占位符
 *    - 不被框架加载，仅作模板参考
 *    - 新成员克隆项目后运行：npm run setup:env
 *      （自动从 .env.example 复制为 .env）
 *
 * 变量命名规范：
 * - Vue CLI 项目（linkos/tycvs）：VUE_APP_ 前缀
 * - Vite 项目（derms）：VITE_ 前缀
 *
 * 同步规则：
 * - 新增变量时，必须同时更新 .env.example 和 .env
 * - .env.example 中的敏感值使用占位符（如 your_xxx）
 * - .env 中填写实际开发值
 *
 * 代码引用规范：
 * - Vue CLI 项目：process.env.VUE_APP_XXX
 * - Vite 项目：import.meta.env.VITE_XXX
 * - vite.config.js 中可使用 process.env.VITE_XXX（需 import process）
 * - vue.config.js 中可使用 process.env.VUE_APP_XXX
 */

const path = require('path');

module.exports = {
  // 项目根目录（默认为 elink-web 目录）
  rootDir: __dirname,

  // 项目配置列表
  projects: [
    {
      name: 'linkos',
      description: '设备管理平台',
      dir: 'linkos',
      devCommand: 'npm run serve',
      port: 9000,
      type: 'vue-cli',      // vue-cli | vite
      envFile: '.env',
      // 环境变量清单（.env 和 .env.example 须同步）：
      // VUE_APP_PROXY_TARGET     - 后端代理目标地址 (string, 默认: http://localhost:5000)
      // VUE_APP_API_HOST         - 后端API主机地址 (string, 默认: localhost:5000)
      // VUE_APP_WS_URL           - WebSocket 连接地址 (string, 默认: ws://localhost:5000)
      // VUE_APP_MAPBOX_ACCESS_TOKEN - Mapbox 地图 Access Token (string)
    },
    {
      name: 'derms',
      description: '运营管理平台',
      dir: 'derms',
      devCommand: 'npm run dev',
      port: 9001,
      type: 'vite',
      envFile: '.env',
      // 环境变量清单（.env 和 .env.example 须同步）：
      // VITE_PROXY_TARGET        - 后端代理目标地址 (string, 默认: http://localhost:5000)
      // VITE_WS_URL              - WebSocket 连接地址 (string, 默认: ws://localhost:5000)
      // VITE_MAPBOX_ACCESS_TOKEN - Mapbox 地图 Access Token (string)
    },
    {
      name: 'tycvs',
      description: '可视化组态平台',
      dir: 'tycvs',
      devCommand: 'npm run serve',
      port: 9002,
      type: 'vue-cli',
      envFile: '.env',
      // 环境变量清单（.env 和 .env.example 须同步）：
      // VUE_APP_PROXY_TARGET          - 后端代理目标地址 (string, 默认: http://localhost:5000)
      // VUE_APP_API_HOST              - 后端API主机地址 (string, 默认: localhost:5000)
      // VUE_APP_WS_URL                - WebSocket 连接地址 (string, 默认: ws://localhost:5000)
      // VUE_APP_OSS_BUCKET_NAME       - 阿里云 OSS Bucket 名称 (string)
      // VUE_APP_OSS_REGION            - 阿里云 OSS Region (string, 默认: oss-cn-hangzhou)
      // VUE_APP_ALIYUN_ACCESS_KEY_ID  - 阿里云 AccessKey ID (string)
      // VUE_APP_ALIYUN_ACCESS_KEY_SECRET - 阿里云 AccessKey Secret (string)
    },
  ],

  // 日志配置
  log: {
    dir: path.join(__dirname, '.dev-logs'),
    maxFiles: 10,            // 保留最近日志文件数
    level: 'info',           // debug | info | warn | error
  },

  // 进程管理配置
  process: {
    healthCheckInterval: 5000,   // 健康检查间隔(ms)
    startupTimeout: 30000,       // 启动超时(ms)
    shutdownTimeout: 10000,      // 停止超时(ms)
    retryCount: 3,               // 启动失败重试次数
    retryDelay: 2000,            // 重试间隔(ms)
  },

  // 控制台输出配置
  display: {
    colors: true,                // 启用彩色输出
    timestamps: true,           // 显示时间戳
    compact: false,             // 紧凑模式（不显示项目描述）
  },
};
