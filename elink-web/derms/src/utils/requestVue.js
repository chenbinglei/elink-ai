import axios from "axios";
import store from "@/store/index.js";
import { ElMessage } from "element-plus";
import { transform } from "@/utils/transformRequest";
import { getToken, removeToken, setToken } from "@/utils/auth";
import { isDev } from "./env";

// 自己的IP地址
const portNum = ":5000";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const serverIpAddress = `/proxy`;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ""
  }`;

const service = axios.create({
  // api的base_url
  timeout: 120000, // 请求超时时间
  transformRequest: [transform()], // 对上传数据进行处理 后端IP:http://192.168.2.142:5000
  baseURL: isDev() ? serverIpAddress : onlineServerIpAddress,
});

let pending = []; //声明一个数组用于存储每个ajax请求的取消函数和ajax标识
let cancelToken = axios.CancelToken;
let canRequest = 1; //0重复请求 1：正常
let isIdentical = 1; //用户登录状态是否改变

let removePending = (config) => {
  let time = new Date().getTime();
  for (let p in pending) {
    if (
      pending[p].u ===
      config.url + "&" + config.method + JSON.stringify(config.data)
    ) {
      //当前请求在数组中存在时执行函数体
      if (time - pending[p].t < 1500) {
        //当两次相同的请求时间小于1000ms时，取消本次请求
        canRequest = 0;
      } else {
        pending.splice(p, 1); //把这条记录从数组中移除
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
    // 打包注释
    // if(config.portNum){
    //     config.baseURL = config.baseURL.replace("158","251");
    //     config.baseURL = config.baseURL.replace(portNum,`:${ config.portNum }`);
    // }

    let userInfo = null;
    try { userInfo = JSON.parse(localStorage.getItem("USER_INFO")); } catch(e) { userInfo = null; }

    //文件类需更改Content-Type
    if (config.data instanceof FormData) {
      Object.assign(config.headers, { "Content-Type": "multipart/form-data" });
      //文件类的添加 用户token 以及 userId
      if (getToken() && userInfo) {
        config.data.append("userId", userInfo.userId);
        config.data.append("access_token", getToken());
      }
    } else {
      // 让每个请求携带自定义token 请根据实际情况自行修改
      if (getToken() && userInfo) {
        config.data.userId = userInfo.userId;
        config.data.access_token = getToken();
      }
    }

    // 判断用户登录状态是否改变
    if (userInfo && store.getters?.oldUserId && store.getters?.oldUserId !== userInfo?.userId)
      isIdentical = 0;

    removePending(config); //在一个ajax发送前执行一下取消操作
    // console.log(pending)
    config.cancelToken = new cancelToken((c) => {
      // 这里的ajax标识我是用请求地址&请求方式拼接的字符串，当然你可以选择其他的一些方式
      if (isIdentical === 0) {
        //取消请求操作
        // console.log("登录状态已变更，请刷新浏览器！")
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
        //取消请求操作   重复请求
        // console.log("duplicateRequest", config);
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
    // Do something with request error
    console.log(error); // for debug
    return Promise.reject(error);
  }
);

// response 拦截器
service.interceptors.response.use(
  (response) => {
    /**
     * code为非20000是抛错 可结合自己业务进行修改
     */

    if (getToken()) setToken(getToken());
    //每次请求刷新cookie时间
    const res = response.data;

    if (res.code && res.code !== 20000 && res.code !== 200) {
      // 如果是当前接口，不触发全局提示
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
            //登录失效
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
    // console.log(error); // for debug
    if (JSON.stringify(error).indexOf("CanceledError") !== -1) {
      //return Promise.reject("重复点击请求关闭", error);
      let repeatMes = { code: 88886, message: "重复点击请求关闭" };
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

export const cancelAbleService = (config) => {
  let resolve, reject;
  const promise = new Promise((_resolve, _reject) => {
    resolve = _resolve;
    reject = _reject;
  });
  const cancel = (message) => {
    resolve({
      message,
    });
  };
  return {
    cancel,
    run: service({
      ...config,
      cancelToken: {
        promise,
      },
    }),
  };
};

export default service;
