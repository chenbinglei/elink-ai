#!/usr/bin/env node

/**
 * elink-web 前端开发环境管理器 v2.0
 * 支持全部/单个项目的启动、停止、重启操作
 * 跨平台进程树管理，确保重启时彻底清理残留进程
 *
 * 用法: node dev-manager.js <command> [project]
 *   全量操作: start-all | stop-all | restart-all | status
 *   单项操作: start <name> | stop <name> | restart <name>
 *   其他:     logs [name] | help
 */

const { spawn, exec, execSync } = require('child_process');
const path = require('path');
const fs = require('fs');
const http = require('http');

// ─── 加载配置 ────────────────────────────────────────────────
const config = require(path.join(__dirname, 'dev.config.js'));

// ─── .env 文件加载 ──────────────────────────────────────────
function loadEnvFile(filePath) {
  try {
    if (!fs.existsSync(filePath)) return;
    const content = fs.readFileSync(filePath, 'utf8');
    for (const line of content.split('\n')) {
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith('#')) continue;
      const eqIndex = trimmed.indexOf('=');
      if (eqIndex === -1) continue;
      const key = trimmed.slice(0, eqIndex).trim();
      const value = trimmed.slice(eqIndex + 1).trim();
      if (!process.env[key]) {
        process.env[key] = value;
      }
    }
  } catch (_) { /* 忽略 */ }
}

// ─── 常量 ────────────────────────────────────────────────────
const PID_DIR = path.join(__dirname, '.dev-pids');
const LOG_DIR = config.log.dir;
const IS_WIN = process.platform === 'win32';
const IS_MAC = process.platform === 'darwin';

// 安全阈值：PID 低于此值的进程视为系统进程，禁止杀灭
const MIN_SAFE_PID = IS_WIN ? 4 : 10;

// ─── 颜色工具 ────────────────────────────────────────────────
const C = {
  reset: '\x1b[0m', bold: '\x1b[1m', dim: '\x1b[2m',
  red: '\x1b[31m', green: '\x1b[32m', yellow: '\x1b[33m',
  blue: '\x1b[34m', cyan: '\x1b[36m', gray: '\x1b[90m',
};
const useColor = config.display.colors ? true : !IS_WIN;
function c(color, text) {
  return useColor ? `${C[color]}${text}${C.reset}` : text;
}

// ─── 日志系统 ────────────────────────────────────────────────
const LogLevel = { debug: 0, info: 1, warn: 2, error: 3 };
const currentLevel = LogLevel[config.log.level] ?? LogLevel.info;

function ts() {
  return new Date().toISOString().replace('T', ' ').replace('Z', '');
}

function writeLog(projectName, message) {
  try {
    if (!fs.existsSync(LOG_DIR)) fs.mkdirSync(LOG_DIR, { recursive: true });
    const date = new Date().toISOString().slice(0, 10);
    fs.appendFileSync(path.join(LOG_DIR, `${projectName}-${date}.log`), `[${ts()}] ${message}\n`);
    // 清理旧日志
    const files = fs.readdirSync(LOG_DIR).filter(f => f.endsWith('.log')).sort();
    while (files.length > config.log.maxFiles) {
      fs.unlinkSync(path.join(LOG_DIR, files.shift()));
    }
  } catch (_) {}
}

function log(level, project, message) {
  if (LogLevel[level] < currentLevel) return;
  const timeStr = config.display.timestamps ? c('dim', `[${ts()}] `) : '';
  const projStr = project ? c('cyan', `[${project}] `) : '';
  const colors = { debug: 'gray', info: 'green', warn: 'yellow', error: 'red' };
  console.log(`${timeStr}${c(colors[level] || 'reset', level.toUpperCase().padEnd(5))} ${projStr}${message}`);
  writeLog(project || 'system', `${level.toUpperCase()} ${message}`);
}

// ─── PID 管理 ────────────────────────────────────────────────
function ensurePidDir() {
  if (!fs.existsSync(PID_DIR)) fs.mkdirSync(PID_DIR, { recursive: true });
}

function pidFile(name) { return path.join(PID_DIR, `${name}.pid`); }
function savePid(name, pid) { ensurePidDir(); fs.writeFileSync(pidFile(name), String(pid)); }
function readPid(name) {
  try { return parseInt(fs.readFileSync(pidFile(name), 'utf8'), 10); }
  catch (_) { return null; }
}
function removePid(name) { try { fs.unlinkSync(pidFile(name)); } catch (_) {} }

// ─── 安全校验 ────────────────────────────────────────────────
function isSafeToKill(pid) {
  if (!pid || isNaN(pid) || pid < MIN_SAFE_PID) {
    log('debug', null, `跳过不安全的目标 PID: ${pid} (低于阈值 ${MIN_SAFE_PID})`);
    return false;
  }
  // Windows 系统进程保护
  if (IS_WIN && (pid === 0 || pid === 4)) return false;
  // Unix init 进程保护
  if (!IS_WIN && pid === 1) return false;
  return true;
}

// ─── 进程状态检查 ────────────────────────────────────────────
function isProcessRunning(pid) {
  if (!pid || isNaN(pid)) return false;
  try {
    if (IS_WIN) {
      const out = execSync(`tasklist /FI "PID eq ${pid}" /NH`, { encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] });
      return out.includes(String(pid));
    } else {
      process.kill(pid, 0);
      return true;
    }
  } catch (_) {
    return false;
  }
}

// ─── 跨平台进程树获取 ────────────────────────────────────────

/**
 * 获取指定 PID 的所有子进程（递归）
 * 返回: [{ pid, ppid, command }]
 */
function getProcessTree(pid) {
  const children = [];
  if (!isSafeToKill(pid)) return children;

  try {
    if (IS_WIN) {
      // Windows: 使用 wmic 获取子进程
      const out = execSync(
        `wmic process where ParentProcessId=${pid} get ProcessId,ParentProcessId,CommandLine /format:list`,
        { encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] }
      );
      // 解析 wmic list 格式输出
      const blocks = out.split(/\r?\n\r?\n/).filter(b => b.trim());
      for (const block of blocks) {
        const fields = {};
        for (const line of block.split(/\r?\n/)) {
          const eq = line.indexOf('=');
          if (eq > 0) fields[line.slice(0, eq).trim()] = line.slice(eq + 1).trim();
        }
        const childPid = parseInt(fields.ProcessId, 10);
        if (childPid && isSafeToKill(childPid)) {
          children.push({ pid: childPid, ppid: pid, command: fields.CommandLine || '' });
          // 递归获取孙子进程
          children.push(...getProcessTree(childPid));
        }
      }
    } else {
      // macOS / Linux: 使用 pgrep -P
      const out = execSync(`pgrep -P ${pid}`, { encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] });
      for (const line of out.trim().split('\n')) {
        const childPid = parseInt(line.trim(), 10);
        if (childPid && isSafeToKill(childPid)) {
          children.push({ pid: childPid, ppid: pid, command: '' });
          // 递归获取孙子进程
          children.push(...getProcessTree(childPid));
        }
      }
    }
  } catch (_) {
    // pgrep/wmic 执行失败（无子进程）时忽略
  }

  return children;
}

/**
 * 获取端口上监听的进程 PID
 * 返回: pid 数组（同一端口可能有多个进程，如 TIME_WAIT 状态）
 */
function findPidsOnPort(port) {
  return new Promise((resolve) => {
    const cmd = IS_WIN
      ? `netstat -aon | findstr :${port} | findstr LISTENING`
      : `lsof -i :${port} -t -sTCP:LISTEN 2>/dev/null`;

    exec(cmd, { encoding: 'utf8' }, (err, stdout) => {
      if (err || !stdout.trim()) return resolve([]);
      const pids = new Set();
      for (const line of stdout.trim().split('\n')) {
        let pid;
        if (IS_WIN) {
          const parts = line.trim().split(/\s+/);
          pid = parseInt(parts[parts.length - 1], 10);
        } else {
          pid = parseInt(line.trim(), 10);
        }
        if (pid && isSafeToKill(pid)) pids.add(pid);
      }
      resolve([...pids]);
    });
  });
}

function isPortInUse(port) {
  return new Promise((resolve) => {
    const server = http.createServer();
    server.once('error', () => resolve(true));
    server.once('listening', () => { server.close(); resolve(false); });
    server.listen(port);
  });
}

// ─── 进程杀灭引擎 ────────────────────────────────────────────

/**
 * 安全杀灭单个进程
 * @param {number} pid - 目标进程 PID
 * @param {string} label - 日志标签
 * @param {object} opts - { force: boolean, timeout: number }
 * @returns {Promise<boolean>}
 */
function killOne(pid, label, opts = {}) {
  const { force = false, timeout = 5000 } = opts;

  return new Promise((resolve) => {
    if (!isSafeToKill(pid)) {
      log('warn', label, `跳过不安全的 PID: ${pid}`);
      return resolve(false);
    }
    if (!isProcessRunning(pid)) {
      return resolve(true);
    }

    const sig = force ? 'SIGKILL' : 'SIGTERM';

    try {
      if (IS_WIN) {
        // Windows: taskkill /T 杀进程树，/F 强制
        const flags = force ? '/T /F' : '/T';
        execSync(`taskkill /PID ${pid} ${flags}`, { encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] });
        resolve(true);
      } else {
        // Linux/macOS: 优先杀进程组
        try {
          process.kill(-pid, sig);
          log('debug', label, `已发送 ${sig} 至进程组 -${pid}`);
        } catch (e) {
          // 进程组不存在，降级杀单个进程
          try {
            process.kill(pid, sig);
            log('debug', label, `已发送 ${sig} 至进程 ${pid}`);
          } catch (__) {
            // 进程已退出
            return resolve(true);
          }
        }

        // 等待进程退出
        const startTime = Date.now();
        const checkInterval = setInterval(() => {
          if (!isProcessRunning(pid) || Date.now() - startTime > timeout) {
            clearInterval(checkInterval);
            if (isProcessRunning(pid)) {
              // 超时后升级为 SIGKILL
              if (sig !== 'SIGKILL') {
                log('warn', label, `进程 ${pid} 未响应 SIGTERM，升级 SIGKILL`);
                try { process.kill(-pid, 'SIGKILL'); } catch (_) {
                  try { process.kill(pid, 'SIGKILL'); } catch (__) {}
                }
              }
              // 再等 2 秒确认
              setTimeout(() => resolve(!isProcessRunning(pid)), 2000);
            } else {
              resolve(true);
            }
          }
        }, 300);
      }
    } catch (err) {
      log('error', label, `杀灭进程 ${pid} 失败: ${err.message}`);
      resolve(false);
    }
  });
}

/**
 * 杀灭整个进程树（主进程 + 所有递归子进程）
 * 策略：先杀叶子（子进程），再杀根（主进程），防止孤儿进程
 */
async function killTree(pid, label) {
  if (!isSafeToKill(pid)) {
    log('warn', label, `跳过不安全的主进程 PID: ${pid}`);
    return false;
  }

  // 1. 获取所有子进程
  const children = getProcessTree(pid);
  const allPids = [pid, ...children.map(c => c.pid)];

  if (children.length > 0) {
    log('info', label, `发现 ${children.length} 个子进程，准备逐个清理...`);
  }

  // 2. 先 SIGTERM 所有子进程（倒序：叶子优先）
  const childPids = children.map(c => c.pid).reverse();
  for (const childPid of childPids) {
    await killOne(childPid, label, { force: false, timeout: 3000 });
  }

  // 3. 再 SIGTERM 主进程（含进程组）
  const mainKilled = await killOne(pid, label, { force: false, timeout: 5000 });

  // 4. 收集残留进程，SIGKILL 兜底
  const survivors = allPids.filter(p => isProcessRunning(p));
  if (survivors.length > 0) {
    log('warn', label, `${survivors.length} 个进程残留，执行强制终止...`);
    for (const p of survivors) {
      await killOne(p, label, { force: true, timeout: 3000 });
    }
  }

  // 5. 最终验证
  const stillAlive = allPids.filter(p => isProcessRunning(p));
  if (stillAlive.length > 0) {
    log('error', label, `以下进程无法终止: [${stillAlive.join(', ')}]`);
    return false;
  }

  return true;
}

// ─── 端口释放等待 ────────────────────────────────────────────

/**
 * 等待端口释放，若端口仍被占用则主动查找并杀灭占用进程
 * @returns {Promise<boolean>} 端口是否成功释放
 */
async function waitForPortRelease(port, label, maxWait = null) {
  maxWait = maxWait || config.process.shutdownTimeout;
  const start = Date.now();
  let lastKillAttempt = 0;

  while (Date.now() - start < maxWait) {
    if (!(await isPortInUse(port))) {
      return true;
    }

    // 每 2 秒尝试一次端口进程清理
    if (Date.now() - lastKillAttempt > 2000) {
      lastKillAttempt = Date.now();
      const portPids = await findPidsOnPort(port);
      for (const p of portPids) {
        log('warn', label, `端口 ${port} 仍被进程 ${p} 占用，正在清理...`);
        await killTree(p, label);
      }
    }

    await new Promise((r) => setTimeout(r, 500));
  }

  // 超时后最终检查
  if (await isPortInUse(port)) {
    const portPids = await findPidsOnPort(port);
    log('error', label, `端口 ${port} 释放超时！残留进程: [${portPids.join(', ') || '未知'}]`);
    return false;
  }
  return true;
}

// ─── 项目工具 ────────────────────────────────────────────────
function getProject(name) { return config.projects.find(p => p.name === name); }
function getProjectDir(project) { return path.resolve(config.rootDir, project.dir); }

function validateProject(name) {
  const project = getProject(name);
  if (!project) {
    log('error', null, `未知项目: ${c('bold', name)}`);
    log('info', null, `可用项目: ${config.projects.map(p => p.name).join(', ')}`);
    process.exit(1);
  }
  return project;
}

// ─── 启动项目 ────────────────────────────────────────────────
async function startProject(project) {
  const dir = getProjectDir(project);

  if (!fs.existsSync(dir)) {
    log('error', project.name, `项目目录不存在: ${dir}`);
    return false;
  }

  if (!fs.existsSync(path.join(dir, 'node_modules'))) {
    log('warn', project.name, 'node_modules 不存在，正在安装依赖...');
    const installOk = await runCommand('npm install', dir, project.name);
    if (!installOk) {
      log('error', project.name, '依赖安装失败，无法启动');
      return false;
    }
  }

  // 检查是否已在运行
  const existingPid = readPid(project.name);
  if (existingPid && isProcessRunning(existingPid)) {
    log('warn', project.name, `项目已在运行中 (PID: ${existingPid})`);
    return true;
  }

  // 检查端口占用
  if (await isPortInUse(project.port)) {
    const portPids = await findPidsOnPort(project.port);
    log('error', project.name, `端口 ${project.port} 已被占用 (PID: [${portPids.join(', ') || '未知'}])`);
    log('info', project.name, `执行 "node dev-manager.js stop ${project.name}" 尝试清理`);
    return false;
  }

  removePid(project.name);
  log('info', project.name, `正在启动... (端口: ${project.port})`);

  // 加载项目的 .env.development
  if (project.envFile) {
    const envPath = path.join(dir, project.envFile);
    loadEnvFile(envPath);
    log('debug', project.name, `已加载环境变量: ${envPath}`);
  }

  // 启动开发服务器（前台 shell 执行命令）
  const child = spawn(project.devCommand, [], {
    cwd: dir,
    env: { ...process.env },
    stdio: ['ignore', 'pipe', 'pipe'],
    detached: !IS_WIN,
    shell: true,
  });

  // 日志流
  if (!fs.existsSync(LOG_DIR)) fs.mkdirSync(LOG_DIR, { recursive: true });
  const logStream = fs.createWriteStream(
    path.join(LOG_DIR, `${project.name}-dev.log`),
    { flags: 'a' }
  );

  child.stdout.on('data', (data) => {
    const msg = data.toString().trim();
    if (msg) {
      logStream.write(`[${ts()}] [OUT] ${msg}\n`);
      if (msg.includes('ready') || msg.includes('Compiled') || msg.includes('Local:') || msg.includes('running at')) {
        log('info', project.name, msg.split('\n').pop());
      }
    }
  });

  child.stderr.on('data', (data) => {
    const msg = data.toString().trim();
    if (msg) {
      logStream.write(`[${ts()}] [ERR] ${msg}\n`);
      if (!msg.includes('DeprecationWarning') && !msg.includes('warning')) {
        log('warn', project.name, msg.split('\n').pop());
      }
    }
  });

  child.on('error', (err) => {
    log('error', project.name, `启动失败: ${err.message}`);
    removePid(project.name);
  });

  child.on('exit', (code) => {
    if (code !== null && code !== 0) {
      log('error', project.name, `进程异常退出 (code: ${code})`);
    }
    removePid(project.name);
    logStream.end();
  });

  savePid(project.name, child.pid);

  // 等待端口就绪
  const started = await waitForPort(project.port, config.process.startupTimeout);
  if (started) {
    log('info', project.name, c('green', `✓ 启动成功 (PID: ${child.pid}, 端口: ${project.port})`));
  } else {
    log('warn', project.name, `启动检测超时 (${config.process.startupTimeout / 1000}s)，进程可能仍在初始化中`);
  }

  return true;
}

function waitForPort(port, timeout) {
  return new Promise((resolve) => {
    const start = Date.now();
    const check = async () => {
      if (Date.now() - start > timeout) return resolve(false);
      if (await isPortInUse(port)) return resolve(true);
      setTimeout(check, 1500);
    };
    check();
  });
}

function runCommand(cmd, cwd, projectName) {
  return new Promise((resolve) => {
    const child = spawn(cmd, [], { cwd, stdio: 'pipe', shell: true });
    child.on('exit', (code) => resolve(code === 0));
    child.on('error', () => resolve(false));
  });
}

// ─── 停止项目（多层保障） ─────────────────────────────────────
async function stopProject(project) {
  const pid = readPid(project.name);
  let stopped = true;

  // ── 第一层：通过 PID 文件杀进程树 ──
  if (pid && isProcessRunning(pid)) {
    log('info', project.name, `正在停止主进程 (PID: ${pid})...`);
    const killed = await killTree(pid, project.name);
    if (killed) {
      log('info', project.name, c('green', '✓ 主进程及子进程已停止'));
    } else {
      log('warn', project.name, '主进程树未完全清理');
      stopped = false;
    }
    removePid(project.name);
  } else if (pid) {
    log('debug', project.name, `PID ${pid} 已不存在，清理 PID 文件`);
    removePid(project.name);
  }

  // ── 第二层：检查端口残留并清理 ──
  const portPids = await findPidsOnPort(project.port);
  if (portPids.length > 0) {
    log('warn', project.name, `端口 ${project.port} 仍有残留进程: [${portPids.join(', ')}]，正在清理...`);
    for (const p of portPids) {
      const killed = await killTree(p, project.name);
      if (!killed) {
        log('error', project.name, `无法终止端口残留进程 ${p}`);
        stopped = false;
      }
    }
  }

  // ── 第三层：验证端口完全释放 ──
  const released = await waitForPortRelease(project.port, project.name);
  if (!released) {
    log('error', project.name, `端口 ${project.port} 未能释放，重启可能失败`);
    stopped = false;
  } else if (portPids.length > 0) {
    log('info', project.name, c('green', `✓ 端口 ${project.port} 已释放`));
  }

  if (!pid && portPids.length === 0) {
    log('info', project.name, '项目未在运行，无需停止');
  }

  return stopped;
}

// ─── 重启项目 ────────────────────────────────────────────────
async function restartProject(project) {
  log('info', project.name, '正在重启...');
  const stopped = await stopProject(project);

  if (!stopped) {
    log('error', project.name, '停止阶段未完全成功，等待额外 3 秒后尝试启动...');
    await new Promise((r) => setTimeout(r, 3000));
  } else {
    // 即使停止成功，也给端口一点释放时间
    await new Promise((r) => setTimeout(r, 1000));
  }

  // 启动前再次确认端口可用
  if (await isPortInUse(project.port)) {
    log('error', project.name, `端口 ${project.port} 仍被占用，启动中止`);
    return false;
  }

  return startProject(project);
}

// ─── 全量操作 ────────────────────────────────────────────────
async function startAll() {
  log('info', null, c('bold', '启动所有前端项目...'));
  const results = {};
  for (const project of config.projects) {
    results[project.name] = await startProject(project);
  }
  printSummary(results, '启动');
}

async function stopAll() {
  log('info', null, c('bold', '停止所有前端项目...'));
  const results = {};
  for (const project of config.projects) {
    results[project.name] = await stopProject(project);
  }
  printSummary(results, '停止');
}

async function restartAll() {
  log('info', null, c('bold', '重启所有前端项目...'));
  const results = {};
  for (const project of config.projects) {
    results[project.name] = await restartProject(project);
  }
  printSummary(results, '重启');
}

function printSummary(results, action) {
  console.log('');
  log('info', null, c('bold', `── ${action}结果汇总 ──`));
  for (const [name, ok] of Object.entries(results)) {
    const status = ok ? c('green', '✓ 成功') : c('red', '✗ 失败');
    const proj = getProject(name);
    const desc = proj && !config.display.compact ? c('dim', ` - ${proj.description}`) : '';
    console.log(`  ${c('bold', name.padEnd(8))} ${status}${desc}`);
  }
  console.log('');
}

// ─── 状态查询 ────────────────────────────────────────────────
async function showStatus() {
  console.log('');
  log('info', null, c('bold', '── 前端项目运行状态 ──'));
  console.log('');

  for (const project of config.projects) {
    const pid = readPid(project.name);
    const running = pid && isProcessRunning(pid);
    const portInUse = await isPortInUse(project.port);
    const portPids = portInUse ? await findPidsOnPort(project.port) : [];

    let statusLine;
    if (running && !portInUse) {
      statusLine = c('yellow', `● PID运行中但端口未监听  PID: ${pid}`);
    } else if (running) {
      statusLine = c('green', `● 运行中  PID: ${pid}  端口: ${project.port}`);
    } else if (portInUse) {
      statusLine = c('yellow', `● 端口占用  端口: ${project.port} (PID: [${portPids.join(', ')}])  本地PID已失效`);
    } else {
      statusLine = c('red', '○ 未运行');
    }

    const typeLabel = c('dim', `[${project.type.toUpperCase()}]`);
    const desc = config.display.compact ? '' : c('dim', `- ${project.description}`);
    console.log(`  ${c('bold', project.name.padEnd(8))} ${typeLabel.padEnd(12)} ${statusLine}  ${desc}`);
  }
  console.log('');
}

// ─── 日志查看 ────────────────────────────────────────────────
function showLogs(projectName) {
  if (!fs.existsSync(LOG_DIR)) {
    log('warn', null, '暂无日志文件');
    return;
  }
  const files = fs.readdirSync(LOG_DIR)
    .filter(f => f.endsWith('.log'))
    .filter(f => !projectName || f.startsWith(projectName))
    .sort()
    .reverse();

  if (files.length === 0) {
    log('warn', null, '暂无日志文件');
    return;
  }
  const latest = files[0];
  const content = fs.readFileSync(path.join(LOG_DIR, latest), 'utf8');
  const lines = content.trim().split('\n').slice(-50);
  console.log('');
  log('info', null, c('bold', `── 最近日志: ${latest} (最后50行) ──`));
  console.log(lines.join('\n'));
  console.log('');
}

// ─── 帮助信息 ────────────────────────────────────────────────
function showHelp() {
  const projects = config.projects.map(p => `    ${p.name.padEnd(8)} - ${p.description} (${p.type}, port:${p.port})`).join('\n');

  console.log(`
${c('bold', 'elink-web 前端开发环境管理器')} v2.0

${c('bold', '用法:')}
  node dev-manager.js <command> [project]

${c('bold', '全量操作命令:')}
  start-all       启动所有前端项目
  stop-all        停止所有前端项目
  restart-all     重启所有前端项目

${c('bold', '单项操作命令:')}
  start <name>    启动指定项目
  stop <name>     停止指定项目
  restart <name>  重启指定项目

${c('bold', '查询命令:')}
  status          查看所有项目运行状态
  logs [name]     查看日志（可选指定项目）

${c('bold', '其他:')}
  help            显示此帮助信息

${c('bold', '可用项目:')}
${projects}

${c('bold', '示例:')}
  node dev-manager.js start-all          # 启动所有项目
  node dev-manager.js start linkos       # 仅启动 linkos
  node dev-manager.js restart derms      # 重启 derms
  node dev-manager.js stop tycvs         # 停止 tycvs
  node dev-manager.js status             # 查看状态
  node dev-manager.js logs linkos        # 查看 linkos 日志
`);
}

// ─── 命令路由 ────────────────────────────────────────────────
async function main() {
  if (!fs.existsSync(LOG_DIR)) fs.mkdirSync(LOG_DIR, { recursive: true });
  ensurePidDir();

  const args = process.argv.slice(2);
  const command = args[0];

  if (!command || command === 'help' || command === '--help' || command === '-h') {
    showHelp();
    return;
  }

  log('info', null, c('dim', `elink-web dev-manager v2.0`));

  switch (command) {
    case 'start-all':
      await startAll();
      break;
    case 'stop-all':
      await stopAll();
      break;
    case 'restart-all':
      await restartAll();
      break;
    case 'start': {
      const project = validateProject(args[1]);
      await startProject(project);
      break;
    }
    case 'stop': {
      const project = validateProject(args[1]);
      await stopProject(project);
      break;
    }
    case 'restart': {
      const project = validateProject(args[1]);
      await restartProject(project);
      break;
    }
    case 'status':
      await showStatus();
      break;
    case 'logs': {
      const name = args[1];
      if (name) validateProject(name);
      showLogs(name);
      break;
    }
    default:
      log('error', null, `未知命令: ${c('bold', command)}`);
      log('info', null, '运行 "node dev-manager.js help" 查看可用命令');
      process.exit(1);
  }
}

main().catch((err) => {
  log('error', null, `致命错误: ${err.message}`);
  process.exit(1);
});
