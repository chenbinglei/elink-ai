import axios from "axios";
import {ElMessage} from "element-plus";

const service = axios.create({
    timeout: 120000, // 请求超时时间
    // baseURL: locationHost === myLocationHost ? serverIpAddress : onlineServerIpAddress
});

let pending = []; //声明一个数组用于存储每个ajax请求的取消函数和ajax标识
let canRequest = 1; //0重复请求 1：正常
let cancelToken = axios.CancelToken;

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
    try {
        if (config.dynamicField && config.dynamicField.requestHeader) {
            let requestHeader = JSON.parse(config.dynamicField.requestHeader);
            Object.assign(config.headers, requestHeader);
        }
    } catch (e) {
        console.log("请求头解析失败")
    }

    // 打包注释
    // 已迁移至环境变量控制，不再硬编码替换

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
            f: c,
            t: new Date().getTime(),
            u: config.url + "&" + config.method + JSON.stringify(config.data)
        });
    });
    return config;
}, (error) => {
    // Do something with request error
    // console.log(error); // for debug
    return Promise.reject(error);
});

// response 拦截器
service.interceptors.response.use((response) => {
        /**
         * code为非20000是抛错 可结合自己业务进行修改
         * */

        const res = response.data;
        if (res.code && res.code !== 20000 && res.code !== 200) {
            ElMessage({message: res.message, type: "error", showClose: true});
            return Promise.reject(response.data);
        } else {
            return response.data;
        }
    }, (error) => {
        // console.log(error); // for debug
        if (JSON.stringify(error).indexOf("CanceledError") !== -1) {
            //return Promise.reject("重复点击请求关闭", error);
            let repeatMes = {code: 88886, message: "重复点击请求关闭"}
            return Promise.reject(repeatMes);
        } else {
            ElMessage({
                type: "error",
                showClose: true,
                message: error.message && error.message.includes("timeout") ? "请求超时，请稍后再试哦" : error.message
            });
            return Promise.reject(error);
        }
    }
);

export default service;
