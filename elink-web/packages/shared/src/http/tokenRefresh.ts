import axios from "axios";
import qs from "qs";
import type { AxiosInstance, AxiosRequestConfig } from "axios";

/**
 * Token 自动刷新管理器
 *
 * 【工作原理】
 * 1. 业务请求返回 401 时，触发 handleTokenExpired
 * 2. 使用 localStorage 中的 refreshToken 调用 /sauth/oauth/token（grant_type=refresh_token）
 * 3. 刷新期间其他并发请求排队，刷新成功后统一重发
 * 4. 刷新失败（refreshToken 也过期）则跳转登录页
 *
 * 【安全策略】
 * - 后端 refresh_token 一次性使用，刷新后旧 token 立即失效
 * - clientId 必须与原登录一致，跨平台滥用将被拒绝
 *
 * 【重发请求设计】
 * - 重发请求使用原 service 实例（通过 serviceGetter 获取），经过完整拦截器链
 * - 确保重发请求的 401 响应能被响应拦截器捕获，触发跳转登录
 * - 请求拦截器对 FormData 的 userId 注入已做幂等性检查，重发不会重复 append
 */

interface TokenRefreshOptions {
  /** Token 管理器（与 HttpClient 共用） */
  auth: {
    getToken: (cookieKey?: string) => string | undefined;
    setToken: (cookieData: string, cookieKey?: string, expires?: number) => string | undefined;
    removeToken: (cookieKey?: string) => void;
  };
  /** 后端基础地址（如 "/proxy" 或线上地址） */
  baseURL: string;
  /** 刷新失败时的回调（一般用于跳转登录页） */
  onRefreshFailed?: () => void;
  /**
   * 获取 service 实例的函数（延迟获取，避免循环依赖）
   * - 单例模式：返回共享的 service 实例
   * - perRequestIsolation 模式：返回当前请求的 service 实例
   * 若未提供，则回退到全局 axios（不推荐，会绕过响应拦截器）
   */
  serviceGetter?: () => AxiosInstance | ((config: AxiosRequestConfig) => Promise<unknown>);
}

interface ExtendedAxiosRequestConfig extends AxiosRequestConfig {
  _retry?: boolean;
}

let isRefreshing = false;
let pendingQueue: Array<(token: string | null) => void> = [];

/**
 * 创建 Token 刷新处理器
 *
 * 用法：
 * ```ts
 * const handleTokenExpired = createTokenRefreshHandler({
 *   auth,
 *   baseURL,
 *   onRefreshFailed: () => { auth.removeToken(); location.reload(); },
 *   serviceGetter: () => service,  // 传入 service 实例用于重发请求
 * });
 *
 * // 在响应拦截器 401 中调用
 * if (error?.response?.status === 401) {
 *   return handleTokenExpired(error);
 * }
 * ```
 */
export function createTokenRefreshHandler(options: TokenRefreshOptions) {
  const { auth, baseURL, onRefreshFailed, serviceGetter } = options;

  /**
   * 重发请求：优先使用 service 实例（经过完整拦截器链），否则回退到全局 axios
   *
   * 使用 service 实例重发的优势：
   * 1. 重发请求的 401 响应能被响应拦截器捕获，触发 onRefreshFailed 跳转登录
   * 2. 请求拦截器会注入最新的 userId/tenantId（幂等性检查避免 FormData 重复 append）
   * 3. 重复请求检测、用户切换检测等逻辑正常工作
   */
  const retryRequest = (config: AxiosRequestConfig): Promise<unknown> => {
    if (serviceGetter) {
      const service = serviceGetter();
      if (typeof service === "function") {
        // serviceGetter 返回的是 perRequestIsolation 模式下的 request 函数
        return service(config);
      }
      // serviceGetter 返回的是 AxiosInstance
      return (service as AxiosInstance)(config);
    }
    // 回退方案：用全局 axios（不经过响应拦截器，重发后的 401 不会被处理）
    return axios(config);
  };

  return async function handleTokenExpired(error: any): Promise<unknown> {
    const originalRequest = error.config as ExtendedAxiosRequestConfig;

    // 已重试过则直接拒绝，避免死循环
    if (originalRequest._retry) {
      return Promise.reject(error);
    }

    // 从 localStorage 读取 refreshToken
    let refreshToken: string | undefined;
    let userInfo: Record<string, unknown> = {};
    try {
      const userInfoStr = localStorage.getItem("USER_INFO");
      if (userInfoStr) {
        userInfo = JSON.parse(userInfoStr);
        refreshToken = userInfo.refreshToken as string | undefined;
      }
    } catch (_e) {
      refreshToken = undefined;
    }

    if (!refreshToken) {
      onRefreshFailed?.();
      return Promise.reject(error);
    }

    // 并发请求排队：正在刷新时，挂起当前请求
    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        pendingQueue.push((newToken: string | null) => {
          if (newToken) {
            if (!originalRequest.headers) originalRequest.headers = {};
            (originalRequest.headers as Record<string, string>)["Authorization"] = `Bearer ${newToken}`;
            resolve(retryRequest(originalRequest));
          } else {
            reject(error);
          }
        });
      });
    }

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      const clientId = (userInfo.clientId as string) || "";
      const resp = await axios.post(
        `${baseURL}/sauth/oauth/token`,
        qs.stringify({
          grant_type: "refresh_token",
          refresh_token: refreshToken,
          client_id: clientId,
          client_secret: clientId, // 与登录保持一致（client_secret = clientId）
        }),
        { headers: { "Content-Type": "application/x-www-form-urlencoded" } }
      );

      const data = (resp.data?.data || {}) as {
        accessToken?: string;
        refreshToken?: string;
      };
      const newAccessToken = data.accessToken;
      const newRefreshToken = data.refreshToken;

      if (!newAccessToken) {
        throw new Error("刷新令牌失败：响应缺少 accessToken");
      }

      // 更新 Cookie 与 localStorage
      auth.setToken(newAccessToken);
      userInfo.accessToken = newAccessToken;
      if (newRefreshToken) {
        userInfo.refreshToken = newRefreshToken;
      }
      localStorage.setItem("USER_INFO", JSON.stringify(userInfo));

      // 重发排队请求
      pendingQueue.forEach((cb) => cb(newAccessToken));
      pendingQueue = [];

      // 重发原请求
      if (!originalRequest.headers) originalRequest.headers = {};
      (originalRequest.headers as Record<string, string>)["Authorization"] = `Bearer ${newAccessToken}`;
      return retryRequest(originalRequest);
    } catch (refreshError) {
      // 刷新失败，清空队列并跳转登录
      pendingQueue.forEach((cb) => cb(null));
      pendingQueue = [];
      onRefreshFailed?.();
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  };
}
