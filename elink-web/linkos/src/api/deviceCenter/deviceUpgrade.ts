import request from "@/utils/request";

// 查询设备升级任务列表
export function queryDeviceTaskList(data) {
    return request({
        url: "/device/deviceTask/queryDeviceTaskList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据设备类型id查询固件包数据
export function getFirmwareListByTypeId(data) {
    return request({
        url: "/device/deviceTask/getFirmwareListByTypeId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 获取设备版本列表
export function getDeviceVersionList(data) {
    return request({
        url: "/device/deviceTask/getDeviceVersionList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 查询设备升级任务编辑列表
export function queryDeviceUpdateList(data) {
    return request({
        url: "/device/deviceTask/queryDeviceUpdateList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 创建设备升级任务
export function createDeviceTask(data) {
    return request({
        url: "/device/deviceTask/createDeviceTask",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 删除设备升级任务数据
export function deleteDeviceTaskById(data) {
    return request({
        url: "/device/deviceTask/deleteDeviceTaskById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 查询设备任务记录列表
export function queryDeviceTaskRecordList(data) {
    return request({
        url: "/device/deviceTask/queryDeviceTaskRecordList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}