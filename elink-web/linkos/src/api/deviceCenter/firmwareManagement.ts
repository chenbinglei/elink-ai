import request from "@/utils/request";

// 获取设备型号列表
export function getEquipmentModelList(data) {
    return request({
        url: "/device/firmware/getEquipmentModelList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 解析固件包数据
export function parseFirmwareData(data) {
    return request({
        url: "/device/firmware/parseFirmwareData",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 上传或编辑固件包数据
export function uploadOrEditFirmware(data) {
    return request({
        url: "/device/firmware/uploadOrEditFirmware",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 查询固件包列表
export function queryFirmwareList(data) {
    return request({
        url: "/device/firmware/queryFirmwareList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 删除固件包数据
export function deleteFirmwareById(data) {
    return request({
        url: "/device/firmware/deleteFirmwareById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}