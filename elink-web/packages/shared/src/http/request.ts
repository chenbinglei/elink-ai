import { createTransform } from "../utils/transformRequest.js";
import type { AxiosInstance, AxiosRequestConfig, AxiosStatic, CancelTokenSource } from "axios";
import type { CookiesStatic } from "js-cookie";

interface AuthManager {
  getToken: (cookieKey?: string) => string | undefined;
  setToken: (cookieData: string, cookieKey?: string, expires?: number) => string | undefined;
  removeToken: (cookieKey?: string) => void;
}

interface Ref<T> {
  value: T;
}

interface PendingItem {
  u: string;
  f: (message?: string) => void;
  t: number;
}

interface ServiceState {
  pending: PendingItem[];
  canRequestRef: Ref<number>;
  isIdenticalRef: Ref<number>;
}

interface HttpClientOptions {
  axios: AxiosStatic;
  ElMessage: (options: { message: string; type?: string; showClose?: boolean; duration?: number; onClose?: () => void }) => void;
  qs: { stringify: (obj: Record<string, unknown>) => string };
  auth: AuthManager;
  baseURL: string;
  timeout?: number;
  getStoreGetters?: () => { oldUserId?: string; userInfo?: Record<string, unknown> } | null;
  onAuthExpired?: () => void;
  perRequestIsolation?: boolean;
  enablePortNum?: boolean;
}

interface CancelAbleResult {
  cancel: (message?: string) => void;
  run: Promise<unknown>;
}

interface HttpClientResult {
  request: (config: AxiosRequestConfig & { portNum?: number; noLoginRequired?: boolean }) => Promise<unknown>;
  cancelAbleService: (config: AxiosRequestConfig & { portNum?: number }) => CancelAbleResult;
}

/**
 * 创建 HTTP 客户端（工厂函数）
 *
 * 【依赖注入设计】不再 import axios/element-plus/qs，由调用方注入。
 *
 * 【两种重复请求隔离模式】保持各项目原有行为：
 * - 单例模式（linkos/tycvs，perRequestIsolation=false）: 共享 pending 队列
 * - 每请求隔离（derms，perRequestIsolation=true）: 每请求独立实例 + 独立 pending
 */
export function createHttpClient(options: HttpClientOptions): HttpClientResult {
  const {
    axios,
    ElMessage,
    qs,
    auth,
    baseURL,
    timeout = 120000,
    getStoreGetters,
    onAuthExpired,
    perRequestIsolation = false,
    enablePortNum = false,
  } = options;

  if (!axios) throw new Error("[@elink/shared/http] axios 必须注入");
  if (!ElMessage) throw new Error("[@elink/shared/http] ElMessage 必须注入");
  if (!qs) throw new Error("[@elink/shared/http] qs 必须注入");
  if (!auth) throw new Error("[@elink/shared/http] auth 必须注入");

  const transform = createTransform(qs);

  const createService = (customBaseURL?: string): AxiosInstance =>
    axios.create({
      timeout,
      transformRequest: [transform as (data: unknown, headers?: Record<string, string>) => string | FormData | null | undefined],
      baseURL: customBaseURL || baseURL,
    });

  const setupInterceptors = (service: AxiosInstance, state: ServiceState): void => {
    const { pending, canRequestRef, isIdenticalRef } = state;
    const CancelToken = axios.CancelToken;

    const removePending = (config: AxiosRequestConfig): void => {
      const time = Date.now();
      const key = (config.url || "") + "&" + config.method + JSON.stringify(config.data);
      for (let i = pending.length - 1; i >= 0; i--) {
        if (pending[i] && pending[i].u === key) {
          if (time - pending[i].t < 1500) {
            canRequestRef.value = 0;
          } else {
            pending.splice(i, 1);
            canRequestRef.value = 1;
          }
          return;
        }
      }
      canRequestRef.value = 1;
    };

    service.interceptors.request.use(
      (config) => {
        const noToken =
          config.headers?.["X-No-Token"] === "true" ||
          (config as AxiosRequestConfig & { noLoginRequired?: boolean }).noLoginRequired === true;

        if (config.headers?.["X-No-Token"]) {
          delete config.headers["X-No-Token"];
        }
        if (config.headers?.["X-Skip-Transform"] === "true") {
          delete config.headers["X-Skip-Transform"];
        }

        let userInfo: Record<string, unknown> | null = null;
        try {
          const userInfoStr = localStorage.getItem("USER_INFO");
          if (userInfoStr) userInfo = JSON.parse(userInfoStr);
        } catch (_e) {
          userInfo = null;
        }

        if (config.data instanceof FormData) {
          Object.assign(config.headers, {
            "Content-Type": "multipart/form-data",
          });
          if (auth.getToken() && userInfo) {
            config.data.append("userId", userInfo.userId as string);
            if (!noToken) {
              config.data.append("access_token", auth.getToken()!);
            }
            if (!config.data.get("tenantId") && userInfo.tenantId) {
              config.data.append("tenantId", userInfo.tenantId as string);
            }
          }
        } else if (auth.getToken() && userInfo && config.data) {
          if (typeof config.data === "string") {
            try {
              const dataObj = JSON.parse(config.data);
              dataObj.userId = userInfo.userId;
              if (!noToken) dataObj.access_token = auth.getToken();
              if (!dataObj.tenantId && userInfo.tenantId) {
                dataObj.tenantId = userInfo.tenantId;
              }
              config.data = JSON.stringify(dataObj);
            } catch (_e) {
              // 非 JSON 字符串，跳过
            }
          } else if (typeof config.data === "object") {
            config.data.userId = userInfo.userId;
            if (!noToken) config.data.access_token = auth.getToken();
            if (!config.data.tenantId && userInfo.tenantId) {
              config.data.tenantId = userInfo.tenantId;
            }
          }
        }

        const storeGetters = getStoreGetters ? getStoreGetters() : null;
        if (
          userInfo &&
          storeGetters?.oldUserId &&
          storeGetters.oldUserId !== userInfo.userId
        ) {
          isIdenticalRef.value = 0;
        }

        removePending(config);
        config.cancelToken = new CancelToken((c) => {
          if (isIdenticalRef.value === 0) {
            auth.removeToken();
            ElMessage({
              message: "登录状态已变更，请刷新浏览器！",
              showClose: true,
              type: "error",
              duration: 5000,
            });
            c();
            return;
          }
          if (canRequestRef.value === 0) {
            c();
            return;
          }
          pending.push({
            u: (config.url || "") + "&" + config.method + JSON.stringify(config.data),
            f: c,
            t: Date.now(),
          });
        });
        return config;
      },
      (error) => Promise.reject(error)
    );

    service.interceptors.response.use(
      (response) => {
        if (auth.getToken()) auth.setToken(auth.getToken()!);
        const res = response.data;

        if (res.code && res.code !== 20000 && res.code !== 200) {
          const isSpecialEndpoint =
            response.config.url ===
            "/together/electConfig/applyElectConfigToOtherSite";
          if (!isSpecialEndpoint) {
            ElMessage({
              message: res.message,
              type: "error",
              showClose: true,
              onClose: () => {
                if (res.code === 9999) {
                  if (onAuthExpired) {
                    onAuthExpired();
                  } else if (window.top !== window) {
                    window.top.postMessage({ action: "unAuth" });
                  } else {
                    auth.removeToken();
                    location.reload();
                  }
                }
              },
            });
          }
          return Promise.reject(response.data);
        }
        return response.data;
      },
      (error) => {
        const isCanceled =
          axios.isCancel(error) ||
          (error?.name && String(error.name).includes("Cancel")) ||
          (error?.code === "ERR_CANCELED") ||
          (error?.message && String(error.message).includes("Cancel"));

        if (isCanceled) {
          return Promise.reject({ code: 88886, message: "重复点击请求关闭" });
        }
        const msg = error?.message || "请求失败";
        ElMessage({
          message: msg.includes("timeout") ? "请求超时，请稍后再试哦" : msg,
          type: "error",
          showClose: true,
        });
        return Promise.reject(error);
      }
    );
  };

  const newState = (): ServiceState => ({
    pending: [],
    canRequestRef: { value: 1 },
    isIdenticalRef: { value: 1 },
  });

  const applyPortNum = (config: AxiosRequestConfig & { portNum?: number }): void => {
    if (config.portNum) {
      if (enablePortNum) {
        config.baseURL = (config.baseURL || baseURL).replace(
          /:\d+/,
          `:${config.portNum}`
        );
      }
      delete config.portNum;
    }
  };

  if (perRequestIsolation) {
    const request = (config: AxiosRequestConfig & { portNum?: number; noLoginRequired?: boolean }): Promise<unknown> => {
      applyPortNum(config);
      const instance = createService(config.baseURL || baseURL);
      setupInterceptors(instance, newState());
      return instance(config);
    };

    const cancelAbleService = (config: AxiosRequestConfig & { portNum?: number }): CancelAbleResult => {
      applyPortNum(config);
      let resolve: (value: unknown) => void;
      const promise = new Promise((_resolve) => {
        resolve = _resolve;
      });
      const cancel = (message?: string) => resolve!({ message });
      const instance = createService(config.baseURL || baseURL);
      setupInterceptors(instance, newState());
      return {
        cancel,
        run: instance({ ...config, cancelToken: { promise } } as AxiosRequestConfig),
      };
    };

    return { request, cancelAbleService };
  }

  const service = createService();
  setupInterceptors(service, newState());

  const request = (config: AxiosRequestConfig & { portNum?: number; noLoginRequired?: boolean }): Promise<unknown> => {
    applyPortNum(config);
    return service(config);
  };

  const cancelAbleService = (config: AxiosRequestConfig & { portNum?: number }): CancelAbleResult => {
    applyPortNum(config);
    let resolve: (value: unknown) => void;
    const promise = new Promise((_resolve) => {
      resolve = _resolve;
    });
    const cancel = (message?: string) => resolve!({ message });
    return {
      cancel,
      run: service({ ...config, cancelToken: { promise } } as AxiosRequestConfig),
    };
  };

  return { request, cancelAbleService };
}
