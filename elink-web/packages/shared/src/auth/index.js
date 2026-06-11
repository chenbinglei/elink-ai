import Cookies from "js-cookie";

/**
 * 创建token管理器（工厂函数）
 * 不同项目使用不同的cookie前缀
 *
 * @param {string} prefix - cookie前缀，如 "SUN_OS_"、"IEMS_PF_"、"TY_CANVAS_"
 * @param {string} defaultKey - 默认token cookie键名
 * @returns {{ getToken, setToken, removeToken }}
 */
export function createAuthManager(prefix, defaultKey) {
  const TokenKey = defaultKey || (prefix + "AdminToken");

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

// 预置各项目的auth管理器
export const linkosAuth = createAuthManager("SUN_OS_", "SUN_OS_AdminToken");
export const dermsAuth = createAuthManager("IEMS_PF_", "IEMS_PF_AdminToken");
export const tycvsAuth = createAuthManager("TY_CANVAS_", "TY_CANVAS_AdminToken");
