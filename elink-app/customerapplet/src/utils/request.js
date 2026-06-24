import axios from "axios";
import {Notify} from "vant";
import {transform} from "@/utils/transformRequest";

// 自己的IP地址
const mylocationHost = "192.168.2.139";
// 请求目标服务器地址   http://192.168.2.158:5000   https://seom.sunmaxxtech.com  192.168.58.106  8.136.233.149  120.55.52.37
const serverIpAddress = 'http://8.136.233.149:5000';
const locationHost = location.hostname;
const locationProtocol = location.protocol;

const service = axios.create({
    // api的base_url
    baseURL: locationHost === mylocationHost ? serverIpAddress : `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? ":5000" : ""}`,
    timeout: 120000, // 请求超时时间
    transformRequest: [transform()],  // 对上传数据进行处理
});

let pending = []; //声明一个数组用于存储每个ajax请求的取消函数和ajax标识
let cancelToken = axios.CancelToken;
let canRequest = 1; //0重复请求 1：正常

let removePending = (config) => {
    let time = new Date().getTime();
    for (let p in pending) {
        if (pending[p].u === config.url + "&" + config.method + JSON.stringify(config.data)) {
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
service.interceptors.request.use((config) => {

    // 小程序又部分接口要根据小程序地址来
    // if(config.data && config.data.requestUrl)config.baseURL = config.data.requestUrl;

    //文件类需更改Content-Type
    if (config.data instanceof FormData) {
        Object.assign(config.headers, {"Content-Type": "multipart/form-data"});
        //文件类的添加 用户token 以及 userId
    }

    removePending(config); //在一个ajax发送前执行一下取消操作
    // console.log(pending)
    config.cancelToken = new cancelToken((c) => {
        // 这里的ajax标识我是用请求地址&请求方式拼接的字符串，当然你可以选择其他的一些方式
        if (canRequest === 0) {
            //取消请求操作   重复请求
            // console.log("duplicateRequest", config);
            c();
            return;
        }
        pending.push({
            u: config.url + "&" + config.method + JSON.stringify(config.data),
            f: c,
            t: new Date().getTime()
        });
    });
    return config;
}, (error) => {
    // Do something with request error
    console.log(error); // for debug
    Promise.reject(error);
});

// respone拦截器
  
service.interceptors.response.use((response) => {
        // code为非20000是抛错 可结合自己业务进行修改
        const res = response.data;
        if (res.code && res.code !== 20000 && res.code !== 200) {
            Notify({ type: "danger", message: res.message,onClose: () => {
                    if (res.code === 9999) {
                        //登录失效
                        uni.postMessage({ data: { type: "loginFailure",...response.data } });
                        uni.navigateTo({ url: "/fourthPackage/pages/reportDownload" });
                    }
                }
            });
            return Promise.reject(response.data);
        } else {
            return response.data;
        }
    }, (error) => {
        // console.log(error); // for debug
        if (JSON.stringify(error).indexOf("CanceledError") !== -1) {
            //return Promise.reject("重复点击请求关闭", error);
            let repeatMes = { code: 88886, message: "重复点击请求关闭" };
            return Promise.reject(repeatMes);
        } else {
            Notify({
                type: "danger",
                message: error.message && error.message.includes("timeout") ? "请求超时，请稍后再试哦" : error.message
            });
            return Promise.reject(error);
        }
    }
);

export default service;
