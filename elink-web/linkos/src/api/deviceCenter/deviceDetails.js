import request from "@/utils/request";



// ---------------------》充电枪管理

// 根据设备id查询模型枪列表数据
export function findDeviceGunListByDeviceId(data) {
    return request({
        url: "/device/device/findDeviceGunListByDeviceId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 添加设备枪数据
export function saveDeviceGun(data) {
    return request({
        url: "/device/device/saveDeviceGun",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 批量删除设备枪数据
export function deleteAllDeviceGun(data) {
    return request({
        url: "/device/device/deleteAllDeviceGun",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 批量添加设备枪数据
export function saveAllDeviceGun(data) {
    return request({
        url: "/device/device/saveAllDeviceGun",
        portNum: "60003",
        method: "post",
        data: data,
    });
}