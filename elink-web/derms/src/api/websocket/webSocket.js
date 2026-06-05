import { isDev } from "@/utils/env";

// 自己的IP地址
const serverIpAddress = `/proxy`;
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const formalIpAddress = `${ locationProtocol === "http:" ? "ws:" : "wss:" }//${ locationHost }${ locationProtocol === "http:" ? ":21010" : "" }`;
// const formalIpAddress = `${ locationProtocol === "http:" ? "ws:" : "wss:" }//${ locationHost }`;
// const url = isDev() ? 'ws://192.168.2.155:5000' : formalIpAddress;

// const url = isDev() ? 'ws://121.41.109.130:5000' : formalIpAddress;
const url = isDev() ? (import.meta.env.VITE_WS_URL || `${locationProtocol === "http:" ? "ws:" : "wss:"}//${locationHost}${locationProtocol === "http:" ? ":21010" : ""}`) : formalIpAddress;

let userInfo = JSON.parse(localStorage.getItem("USER_INFO")); //当前登录用户信息
console.log("userInfo", userInfo);

// 运营中心 --- 充电站-- 运行实况 --- 获取充电桩所有枪数据列表
export function pileRealWebSocket(data) {
  return `${url}/together/pileRealWebSocket/${data.userId}/${data.pileCode}`;
}
// 大屏幕静态
export function largeStaticWebSocket() {
  return `${url}/together/largeStaticWebSocket/${userInfo.userId}`;
}
// 大屏动态
export function largeRealWebSocket() {
  return `${url}/together/largeRealWebSocket/${userInfo.userId}`;
}
// 首页事实动态数据
export function homeWebSocket() {
  return `${url}/together/homePageWebSocket/${userInfo.userId}`;
}
// 大屏事实动态数据
export function customSystemWebSocket() {
  return `${url}/together/customSystemWebSocket/${userInfo.userId}/2c99698b9ba1747a019c215650e002ab`;
}