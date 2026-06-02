// 项目所有WebSocket管理

const myLocationHost = "localhost";
const serverIpAddress = 'ws://192.168.2.158:5000';
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const formalIpAddress = `${locationProtocol === "http:" ? "ws:" : "wss:"}//${locationHost}${locationProtocol === "http:" ? ":5000" : ""}`;
const url = locationHost === myLocationHost ? serverIpAddress : formalIpAddress;


// 设备升级 ----》 设备升级实时进度条连接
export function deviceUpdateWebSocket(data) {
    return `${url}/device/deviceUpdateWebSocket/${data.userId}`;
}