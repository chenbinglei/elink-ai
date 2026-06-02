import axios from "axios";
import store from "@/store/index.js";
import {ElMessage} from "element-plus";
import {transform} from "@/utils/transformRequest";
import {getToken, removeToken, setToken} from "@/utils/auth";
import { isDev } from "./env";

// 自己的IP地址
const portNum = ":21010";
// 请求目标服务器地址   http://192.168.2.158:9534   https://os.enlinkitech.com/   121.41.109.130
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const serverIpAddress = `${locationProtocol}//47.110.235.112${ portNum }`;
// const serverIpAddress = `${locationProtocol}//121.41.109.130${ portNum }`;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ""}`;

const service = axios.create({
    timeout: 120000,
    transformRequest: [transform()],
    baseURL: isDev() ? "/proxy" : onlineServerIpAddress
});

let pending = []; //声明一个数组用于存储每个ajax请求的取消函数和ajax标识
let cancelToken = axios.CancelToken;
let canRequest = 1; //0重复请求 1：正常
let isIdentical = 1; //用户登录状态是否改变

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

    // 打包注释
    // if(config.portNum){
    //     // config.baseURL = config.baseURL.replace("158","251");
    //     config.baseURL = config.baseURL.replace(portNum,`:${ config.portNum }`);
    // }

    let userInfo = JSON.parse(localStorage.getItem("USER_INFO")); //当前登录用户信息
    // console.log(userInfo);

    //文件类需更改Content-Type
    if (config.data instanceof FormData) {
        Object.assign(config.headers, {"Content-Type": "multipart/form-data"});
        //文件类的添加 用户token 以及 userId
        if (getToken()) {
            config.data.append("userId", userInfo.userId);
            config.data.append("access_token", getToken());
            if(!config.data.get("tenantId")) config.data.append("tenantId", userInfo.tenantId);
            // if(config.baseURL.indexOf("251") === -1) config.data.append("access_token", getToken());
        }
    } else {
        // 让每个请求携带自定义token 请根据实际情况自行修改
        if (getToken()) {
            config.data.userId = userInfo.userId;
            config.data.access_token = getToken();
            if(!config.data.tenantId) config.data.tenantId = userInfo.tenantId;
            // if(config.baseURL.indexOf("251") === -1) config.data.access_token = getToken();
        }
    }

    // 判断用户登录状态是否改变
    if (store.getters.oldUserId && store.getters.oldUserId !== userInfo.userId) isIdentical = 0;

    removePending(config); //在一个ajax发送前执行一下取消操作
    // console.log(pending)
    config.cancelToken = new cancelToken((c) => {
        // 这里的ajax标识我是用请求地址&请求方式拼接的字符串，当然你可以选择其他的一些方式
        if (isIdentical === 0) {
            //取消请求操作
            // console.log("登录状态已变更，请刷新浏览器！")
            removeToken();
            ElMessage({message: "登录状态已变更，请刷新浏览器！", showClose: true, type: "error", duration: 5000});
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
            t: new Date().getTime()
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
         */
        if (getToken()) setToken(getToken());
        //每次请求刷新cookie时间
        const res = response.data;
        if (res.code && res.code !== 20000 && res.code !== 200) {
            ElMessage({
                message: res.message, type: "error", showClose: true, onClose: () => {
                    if (res.code === 9999) {
                        //登录失效
                        removeToken()
                        location.reload();
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
            let repeatMes = {code: 88886, message: "重复点击请求关闭"}
            return Promise.reject(repeatMes);
        } else {
            ElMessage({
                message: error.message && error.message.includes("timeout") ? "请求超时，请稍后再试哦" : error.message,
                type: "error",
                showClose: true
            });
            return Promise.reject(error);
        }
    }
);

export default service;
