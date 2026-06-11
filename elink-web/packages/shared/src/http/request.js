import axios from "axios";
import { ElMessage } from "element-plus";
import { transform } from "@elink/shared/utils/transformRequest";

/**
 * 创建axios请求实例（工厂函数）
 * 合并3个项目的request.js拦截器逻辑
 *
 * 【重要】支持两种重复请求取消模式，保持各项目原行为：
 * - 单例模式（linkos）: 所有请求共享 pending 队列
 * - 每请求隔离模式（derms）: 每次 request() 调用创建独立实例、独立 pending
 *   → 并发请求不会被互相取消（derms 原有模式）
 *
 * @param {object} options
 * @param {object} options.auth - auth管理器 { getToken, setToken, removeToken }
 * @param {string} options.baseURL - 基础URL
 * @param {number} [options.timeout=120000] - 超时时间
 * @param {Function} [options.getStoreGetters] - 获取store getters的函数，用于登录状态检测
 * @param {Function} [options.onAuthExpired] - 登录失效回调（code=9999时触发）
 * @param {boolean} [options.perRequestIsolation=false] - 每请求隔离模式（derms true）
 * @returns {{ request, cancelAbleService }}
 */
export function createHttpClient(options) {
  const {
    auth,
    baseURL,
    timeout = 120000,
    getStoreGetters,
    onAuthExpired,
    perRequestIsolation = false,  // 默认 linkos 模式
  } = options;

  // 创建基础 axios 实例配置
  const createService = (customBaseURL) => {
    return axios.create({
      timeout,
      transformRequest: [transform()],
      baseURL: customBaseURL || baseURL,
    });
  };

  // 配置拦截器（复用逻辑，可用于多个实例）
  const setupInterceptors = (service, state) => {
    const { pending, cancelToken, canRequestRef, isIdenticalRef } = state;

    const removePending = (config) => {
      const time = new Date().getTime();
      const key =
        config.url + "&" + config.method + JSON.stringify(config.data);
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

    // request拦截器
    service.interceptors.request.use(
      (config) => {
        const noToken =
          config.headers?.["X-No-Token"] === "true" ||
          config.noLoginRequired === true;

        // 清理自定义头
        if (config.headers?.["X-No-Token"]) {
          delete config.headers["X-No-Token"];
        }
        if (config.headers?.["X-Skip-Transform"] === "true") {
          delete config.headers["X-Skip-Transform"];
        }

        let userInfo = null;
        try {
          const userInfoStr = localStorage.getItem("USER_INFO");
          if (userInfoStr) {
            userInfo = JSON.parse(userInfoStr);
          }
        } catch (e) {
          userInfo = null;
        }

        // 文件类需更改Content-Type
        if (config.data instanceof FormData) {
          Object.assign(config.headers, {
            "Content-Type": "multipart/form-data",
          });
          // 【关键修复】加上 userInfo 非空判断，防止 localStorage 清空时 TypeError 导致黑屏
          if (auth.getToken() && userInfo) {
            config.data.append("userId", userInfo.userId);
            if (!noToken) {
              config.data.append("access_token", auth.getToken());
            }
            if (!config.data.get("tenantId") && userInfo.tenantId) {
              config.data.append("tenantId", userInfo.tenantId);
            }
          }
        } else {
          if (auth.getToken() && userInfo && config.data) {
            if (typeof config.data === "string") {
              try {
                const dataObj = JSON.parse(config.data);
                dataObj.userId = userInfo.userId;
                if (!noToken) {
                  dataObj.access_token = auth.getToken();
                }
                if (!dataObj.tenantId && userInfo.tenantId) {
                  dataObj.tenantId = userInfo.tenantId;
                }
                config.data = JSON.stringify(dataObj);
              } catch (e) {
                // 非JSON字符串，跳过
              }
            } else if (typeof config.data === "object") {
              config.data.userId = userInfo.userId;
              if (!noToken) {
                config.data.access_token = auth.getToken();
              }
              if (!config.data.tenantId && userInfo.tenantId) {
                config.data.tenantId = userInfo.tenantId;
              }
            }
          }
        }

        // 判断用户登录状态是否改变
        const storeGetters = getStoreGetters ? getStoreGetters() : null;
        if (
          userInfo &&
          storeGetters?.oldUserId &&
          storeGetters.oldUserId !== userInfo.userId
        ) {
          isIdenticalRef.value = 0;
        }

        removePending(config);
        config.cancelToken = new cancelToken((c) => {
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
            t: new Date().getTime(),
          });
        });
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // response拦截器
    service.interceptors.response.use(
      (response) => {
        if (auth.getToken()) auth.setToken(auth.getToken());
        const res = response.data;

        if (res.code && res.code !== 20000 && res.code !== 200) {
          // 【兼容derms旧逻辑】特殊端点不做错误弹窗
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
        } else {
          return response.data;
        }
      },
      (error) => {
        // 【关键修复】使用 axios.isCancel 官方 API 代替脆弱的字符串匹配
        // 兼容旧版 axios CanceledError / ERR_CANCELED 多种情况
        const isCanceled =
          axios.isCancel(error) ||
          (error.name && error.name.includes("Cancel")) ||
          (error.code && error.code === "ERR_CANCELED") ||
          (error.message && error.message.includes("Cancel"));

        if (isCanceled) {
          const repeatMes = { code: 88886, message: "重复点击请求关闭" };
          return Promise.reject(repeatMes);
        } else {
          ElMessage({
            message:
              error.message && error.message.includes("timeout")
                ? "请求超时，请稍后再试哦"
                : error.message || "请求失败",
            type: "error",
            showClose: true,
          });
          return Promise.reject(error);
        }
      }
    );
  };

  // 【关键架构决策】
  // derms 旧代码是每请求独立实例/独立 pending → 并发请求不会被取消
  // linkos 旧代码是单例共享 pending → 只有真正的重复请求被取消
  if (perRequestIsolation) {
    // derms 模式：每请求创建独立实例、独立 pending 队列
    const request = (config) => {
      const requestBaseURL = config.portNum
        ? baseURL.replace(/:\d+/, `:${config.portNum}`)
        : baseURL;
      if (config.portNum) delete config.portNum;

      const instanceState = {
        pending: [],
        cancelToken: axios.CancelToken,
        canRequestRef: { value: 1 },
        isIdenticalRef: { value: 1 },
      };
      const instance = createService(requestBaseURL);
      setupInterceptors(instance, instanceState);
      return instance(config);
    };

    const cancelAbleService = (config) => {
      const requestBaseURL = config.portNum
        ? baseURL.replace(/:\d+/, `:${config.portNum}`)
        : baseURL;
      if (config.portNum) delete config.portNum;

      let resolve, reject;
      const promise = new Promise((_resolve, _reject) => {
        resolve = _resolve;
        reject = _reject;
      });
      const cancel = (message) => {
        resolve({ message });
      };

      const instanceState = {
        pending: [],
        cancelToken: axios.CancelToken,
        canRequestRef: { value: 1 },
        isIdenticalRef: { value: 1 },
      };
      const instance = createService(requestBaseURL);
      setupInterceptors(instance, instanceState);

      return {
        cancel,
        run: instance({
          ...config,
          cancelToken: { promise },
        }),
      };
    };

    return { request, cancelAbleService };
  } else {
    // linkos 模式：单例共享 pending 队列（原有模式）
    const sharedState = {
      pending: [],
      cancelToken: axios.CancelToken,
      canRequestRef: { value: 1 },
      isIdenticalRef: { value: 1 },
    };

    const service = createService();
    setupInterceptors(service, sharedState);

    const request = (config) => {
      // 支持 portNum 动态端口路由
      if (config.portNum) {
        config.baseURL = (config.baseURL || baseURL).replace(
          /:\d+/,
          `:${config.portNum}`
        );
        delete config.portNum;
      }
      return service(config);
    };

    const cancelAbleService = (config) => {
      if (config.portNum) {
        config.baseURL = (config.baseURL || baseURL).replace(
          /:\d+/,
          `:${config.portNum}`
        );
        delete config.portNum;
      }

      let resolve, reject;
      const promise = new Promise((_resolve, _reject) => {
        resolve = _resolve;
        reject = _reject;
      });
      const cancel = (message) => {
        resolve({ message });
      };

      return {
        cancel,
        run: service({
          ...config,
          cancelToken: { promise },
        }),
      };
    };

    return { request, cancelAbleService };
  }
}
