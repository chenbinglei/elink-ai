import { createTransform } from "../utils/transformRequest.js";

/**
 * 创建 HTTP 客户端（工厂函数）
 *
 * 【依赖注入设计】不再 import axios/element-plus/qs，由调用方注入。
 * 这样 @elink/shared 包是零外部依赖的纯函数库，避免 monorepo 中
 * 跨工作空间的模块解析问题，且各项目可使用自己版本的依赖。
 *
 * 【两种重复请求隔离模式】保持各项目原有行为：
 * - 单例模式（linkos/tycvs，perRequestIsolation=false）: 共享 pending 队列
 * - 每请求隔离（derms，perRequestIsolation=true）: 每请求独立实例 + 独立 pending
 *
 * @param {object} options
 * @param {object} options.axios - axios 模块实例（必传）
 * @param {object} options.ElMessage - element-plus ElMessage（必传）
 * @param {object} options.qs - qs 模块实例（必传）
 * @param {object} options.auth - auth管理器 { getToken, setToken, removeToken }
 * @param {string} options.baseURL - 基础URL
 * @param {number} [options.timeout=120000] - 超时时间
 * @param {Function} [options.getStoreGetters] - 获取store getters的函数
 * @param {Function} [options.onAuthExpired] - 登录失效回调（code=9999时触发）
 * @param {boolean} [options.perRequestIsolation=false] - 每请求隔离模式
 * @param {boolean} [options.enablePortNum=false] - 是否启用 portNum 动态端口路由（derms 启用，linkos/tycvs 关闭）
 * @returns {{ request, cancelAbleService }}
 */
export function createHttpClient(options) {
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

  // 创建基础 axios 实例
  const createService = (customBaseURL) =>
    axios.create({
      timeout,
      transformRequest: [transform],
      baseURL: customBaseURL || baseURL,
    });

  // 配置拦截器
  const setupInterceptors = (service, state) => {
    const { pending, canRequestRef, isIdenticalRef } = state;
    const CancelToken = axios.CancelToken;

    const removePending = (config) => {
      const time = Date.now();
      const key = config.url + "&" + config.method + JSON.stringify(config.data);
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

    // request 拦截器
    service.interceptors.request.use(
      (config) => {
        const noToken =
          config.headers?.["X-No-Token"] === "true" ||
          config.noLoginRequired === true;

        if (config.headers?.["X-No-Token"]) {
          delete config.headers["X-No-Token"];
        }
        if (config.headers?.["X-Skip-Transform"] === "true") {
          delete config.headers["X-Skip-Transform"];
        }

        let userInfo = null;
        try {
          const userInfoStr = localStorage.getItem("USER_INFO");
          if (userInfoStr) userInfo = JSON.parse(userInfoStr);
        } catch (e) {
          userInfo = null;
        }

        // 文件类需更改 Content-Type
        if (config.data instanceof FormData) {
          Object.assign(config.headers, {
            "Content-Type": "multipart/form-data",
          });
          if (auth.getToken() && userInfo) {
            config.data.append("userId", userInfo.userId);
            if (!noToken) {
              config.data.append("access_token", auth.getToken());
            }
            if (!config.data.get("tenantId") && userInfo.tenantId) {
              config.data.append("tenantId", userInfo.tenantId);
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
            } catch (e) {
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

        // 检测用户登录状态切换
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
            u: config.url + "&" + config.method + JSON.stringify(config.data),
            f: c,
            t: Date.now(),
          });
        });
        return config;
      },
      (error) => Promise.reject(error)
    );

    // response 拦截器
    service.interceptors.response.use(
      (response) => {
        if (auth.getToken()) auth.setToken(auth.getToken());
        const res = response.data;

        if (res.code && res.code !== 20000 && res.code !== 200) {
          // 兼容 derms 旧逻辑：特殊端点不弹错误窗
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
        // 使用 axios.isCancel 官方 API 检测取消
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

  const newState = () => ({
    pending: [],
    canRequestRef: { value: 1 },
    isIdenticalRef: { value: 1 },
  });

  // 动态端口路由：仅当 enablePortNum=true 时启用
  // - derms：旧逻辑生效，根据 config.portNum 替换 baseURL 端口
  // - linkos/tycvs：旧逻辑已被注释（所有请求统一走网关:5000），保持禁用
  // 始终移除 config.portNum，避免污染 axios config
  const applyPortNum = (config) => {
    if (config.portNum) {
      if (enablePortNum) {
        config.baseURL = (config.baseURL || baseURL).replace(
          /:\d+/,
          `:${config.portNum}`
        );
      }
      delete config.portNum;
    }
    return config;
  };

  if (perRequestIsolation) {
    // derms 模式：每请求独立实例 + 独立 pending
    const request = (config) => {
      applyPortNum(config);
      const instance = createService(config.baseURL || baseURL);
      setupInterceptors(instance, newState());
      return instance(config);
    };

    const cancelAbleService = (config) => {
      applyPortNum(config);
      let resolve;
      const promise = new Promise((_resolve) => {
        resolve = _resolve;
      });
      const cancel = (message) => resolve({ message });
      const instance = createService(config.baseURL || baseURL);
      setupInterceptors(instance, newState());
      return {
        cancel,
        run: instance({ ...config, cancelToken: { promise } }),
      };
    };

    return { request, cancelAbleService };
  }

  // linkos/tycvs 模式：单例 + 共享 pending
  const service = createService();
  setupInterceptors(service, newState());

  const request = (config) => {
    applyPortNum(config);
    return service(config);
  };

  const cancelAbleService = (config) => {
    applyPortNum(config);
    let resolve;
    const promise = new Promise((_resolve) => {
      resolve = _resolve;
    });
    const cancel = (message) => resolve({ message });
    return {
      cancel,
      run: service({ ...config, cancelToken: { promise } }),
    };
  };

  return { request, cancelAbleService };
}
