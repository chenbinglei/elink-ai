/**
 * 创建token管理器（工厂函数）
 * 不同项目使用不同的cookie前缀
 *
 * 【依赖注入设计】不再 import js-cookie，由调用方注入 Cookies 实例。
 * 这样 @elink/shared 包是零外部依赖的纯函数库，避免 monorepo 中
 * 跨工作空间的模块解析问题。
 *
 * @param {object} Cookies - js-cookie 模块实例（由调用方注入）
 * @param {string} prefix - cookie前缀，如 "SUN_OS_"、"IEMS_PF_"、"TY_CANVAS_"
 * @param {string} [defaultKey] - 默认token cookie键名
 * @returns {{ getToken, setToken, removeToken }}
 */
export function createAuthManager(Cookies, prefix, defaultKey) {
  if (!Cookies || typeof Cookies.get !== "function") {
    throw new Error(
      "[@elink/shared/auth] Cookies 实例必须由调用方注入（import Cookies from 'js-cookie'）"
    );
  }
  const TokenKey = defaultKey || prefix + "AdminToken";

  function getToken(cookieKey) {
    return Cookies.get(cookieKey ? prefix + cookieKey : TokenKey);
  }

  function setToken(cookieData, cookieKey, expires) {
    return Cookies.set(cookieKey ? prefix + cookieKey : TokenKey, cookieData, {
      expires: expires ? expires : 24,
    });
  }

  function removeToken(cookieKey) {
    return Cookies.remove(cookieKey ? prefix + cookieKey : TokenKey);
  }

  return { getToken, setToken, removeToken };
}

// 预设各项目的 cookie 前缀常量，方便调用方使用
export const AUTH_PREFIX = {
  LINKOS: "SUN_OS_",
  DERMS: "IEMS_PF_",
  TYCVS: "TY_CANVAS_",
};
