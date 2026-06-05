/**
 * 前端开发环境配置文件
 * 修改此文件即可调整项目管理器的行为，无需修改主脚本
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
      envFile: '.env.development',
    },
    {
      name: 'derms',
      description: '运营管理平台',
      dir: 'derms',
      devCommand: 'npm run dev',
      port: 9001,
      type: 'vite',
      envFile: '.env.development',
    },
    {
      name: 'tycvs',
      description: '可视化组态平台',
      dir: 'tycvs',
      devCommand: 'npm run serve',
      port: 9002,
      type: 'vue-cli',
      envFile: '.env.development',
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
