import axios from "axios";
import store from "@/store/index.js";
import { ElMessage } from "element-plus";
import { transform } from "@/utils/transformRequest";
import { getToken, removeToken, setToken } from "@/utils/auth";
import { isDev } from "./env";

// 自己的IP地址
const portNum = ":21010";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const serverIpAddress = `/proxy`;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ""}`;

// 创建基础的 axios 配置
const createAxiosInstance = (config = {}) => {
  const baseConfig = {
    timeout: 120000,
    baseURL: isDev() ? serverIpAddress : onlineServerIpAddress,
  };

  // 检查是否需要跳过 transform
  const shouldSkipTransform = config.headers && config.headers['X-Skip-Transform'] === 'true';

  baseConfig.transformRequest = [transform()];

  return axios.create(baseConfig);
};

let pending = [];
let cancelToken = axios.CancelToken;
let canRequest = 1;
let isIdentical = 1;

let removePending = (config) => {
  let time = new Date().getTime();
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

// 通用的请求拦截器逻辑
const setupRequestInterceptor = (instance) => {
  instance.interceptors.request.use(
    (config) => {
      // 判断是否需要跳过 token
      const noToken = config.headers?.['X-No-Token'] === 'true';
      
      // 用完删掉自定义头
      if (config.headers?.['X-No-Token']) {
        delete config.headers['X-No-Token'];
      }

      // 如果存在 X-Skip-Transform 头，在发送前删除它
      if (config.headers['X-Skip-Transform'] === 'true') {
        delete config.headers['X-Skip-Transform'];
      }

      let userInfo = JSON.parse(localStorage.getItem("USER_INFO"));

      // 文件类需更改Content-Type
      if (config.data instanceof FormData) {
        Object.assign(config.headers, { "Content-Type": "multipart/form-data" });
        if (getToken()) {
          // userId 始终带
          config.data.append("userId", userInfo.userId);

          // 不带 X-No-Token 才带 token
          if (!noToken) {
            config.data.append("access_token", getToken());
            if (config.baseURL.indexOf("251") === -1) {
              config.data.append("access_token", getToken());
            }
          }
        }
      } else {
        if (getToken()) {
          // 对于 JSON 数据，确保是对象格式
          if (typeof config.data === 'string') {
            try {
              const dataObj = JSON.parse(config.data);
              // userId 始终带
              dataObj.userId = userInfo.userId;
              // 不带 X-No-Token 才带 token
              if (!noToken) {
                dataObj.access_token = getToken();
              }
              config.data = JSON.stringify(dataObj);
            } catch (e) {
              console.warn('无法解析请求数据:', e);
            }
          } else if (typeof config.data === 'object') {
            // userId 始终带
            config.data.userId = userInfo.userId;
            // 不带 X-No-Token 才带 token
            if (!noToken) {
              config.data.access_token = getToken();
            }
          }
        }
      }

      // 判断用户登录状态是否改变
      if (store.getters?.oldUserId && store.getters?.oldUserId !== userInfo?.userId) {
        isIdentical = 0;
      }

      removePending(config);
      config.cancelToken = new cancelToken((c) => {
        if (isIdentical === 0) {
          removeToken();
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
      console.log(error);
      return Promise.reject(error);
    }
  );
};

// 通用的响应拦截器逻辑
const setupResponseInterceptor = (instance) => {
  instance.interceptors.response.use(
    (response) => {
      if (getToken()) setToken(getToken());
      const res = response.data;

      if (res.code && res.code !== 20000 && res.code !== 200) {
        if (response.config.url === '/together/electConfig/applyElectConfigToOtherSite') {
          return Promise.reject(response.data);
        }

        ElMessage({
          message: res.message,
          type: "error",
          showClose: true,
          onClose: () => {
            if (res.code === 9999) {
              if (window.top !== window) {
                window.top.postMessage({
                  action: "unAuth",
                });
                return;
              }
              removeToken();
              location.reload();
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
        let repeatMes = { code: 88886, message: "重复点击请求关闭" };
        return Promise.reject(repeatMes);
      } else {
        ElMessage({
          message: error.message && error.message.includes("timeout") 
            ? "请求超时，请稍后再试哦" 
            : error.message,
          type: "error",
          showClose: true,
        });
        return Promise.reject(error);
      }
    }
  );
};

// 主要的 request 函数
const request = (config) => {
  const instance = createAxiosInstance(config);
  setupRequestInterceptor(instance);
  setupResponseInterceptor(instance);
  return instance(config);
};

export const cancelAbleService = (config) => {
  let resolve, reject;
  const promise = new Promise((_resolve, _reject) => {
    resolve = _resolve;
    reject = _reject;
  });
  const cancel = (message) => {
    resolve({ message });
  };

  const instance = createAxiosInstance(config);
  setupRequestInterceptor(instance);
  setupResponseInterceptor(instance);

  return {
    cancel,
    run: instance({
      ...config,
      cancelToken: { promise },
    }),
  };
};

export default request;