import axios from "axios";
import { ElMessage } from "element-plus";
import { transform } from "@elink/shared/utils/transformRequest";

/**
 * 创建axios请求实例（工厂函数）
 * 合并3个项目的request.js拦截器逻辑
 *
 * @param {object} options
 * @param {object} options.auth - auth管理器 { getToken, setToken, removeToken }
 * @param {string} options.baseURL - 基础URL
 * @param {number} [options.timeout=120000] - 超时时间
 * @param {Function} [options.getStoreGetters] - 获取store getters的函数，用于登录状态检测
 * @param {Function} [options.onAuthExpired] - 登录失效回调（code=9999时触发）
 * @returns {{ request, cancelAbleService }}
 */
export function createHttpClient(options) {
  const {
    auth,
    baseURL,
    timeout = 120000,
    getStoreGetters,
    onAuthExpired,
  } = options;

  const service = axios.create({
    timeout,
    transformRequest: [transform()],
    baseURL,
  });

  let pending = [];
  const cancelToken = axios.CancelToken;
  let canRequest = 1;
  let isIdentical = 1;

  const removePending = (config) => {
    const time = new Date().getTime();
    for (let p in pending) {
      if (
        pending[p].u ===
        config.url + "&" + config.method + JSON.stringify(config.data)
      ) {
        if (time - pending[p].t < 1500) {
          canRequest = 0;
        } else {
          pending.splice(p, 1);
          canRequest = 1;
        }
        return;
      } else {
        canRequest = 1;
      }
    }
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
        userInfo = JSON.parse(localStorage.getItem("USER_INFO"));
      } catch (e) {
        userInfo = null;
      }

      // 文件类需更改Content-Type
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
      } else {
        if (auth.getToken() && userInfo) {
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
        isIdentical = 0;
      }

      removePending(config);
      config.cancelToken = new cancelToken((c) => {
        if (isIdentical === 0) {
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

        if (canRequest === 0) {
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

        return Promise.reject(response.data);
      } else {
        return response.data;
      }
    },
    (error) => {
      if (JSON.stringify(error).indexOf("CanceledError") !== -1) {
        const repeatMes = { code: 88886, message: "重复点击请求关闭" };
        return Promise.reject(repeatMes);
      } else {
        ElMessage({
          message:
            error.message && error.message.includes("timeout")
              ? "请求超时，请稍后再试哦"
              : error.message,
          type: "error",
          showClose: true,
        });
        return Promise.reject(error);
      }
    }
  );

  const request = (config) => service(config);

  const cancelAbleService = (config) => {
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
