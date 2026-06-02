import request from "@/utils/request";

// 根据设备id查询设备通道信息列表
export function findChannelInfoListByDeviceId(data) {
    return request({
        url: "/device/access/findChannelInfoListByDeviceId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据通道id删除通道数据
export function deleteChannelById(data) {
    return request({
        url: "/device/access/deleteChannelById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据设备id查询设备接入详情
export function findAccessDetailByDeviceId(data) {
    return request({
        url: "/device/access/findAccessDetailByDeviceId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}


// 设备注册注销
export function updateDeviceStatus(data) {
    return request({
        url: "/device/access/updateDeviceStatus",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 新增或编辑通道数据
export function saveChannel(data) {
    return request({
        url: "/device/access/saveChannel",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据通道id查询点表数据列表
export function findPointTableListByChannelId(data) {
    return request({
        url: "/device/access/findPointTableListByChannelId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据设备id查询网关子设备功能点列表
export function findSubDeviceFunctionListByDeviceId(data) {
    return request({
        url: "/device/access/findSubDeviceFunctionListByDeviceId",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 新增或编辑或删除点表数据
export function savePointTable(data) {
    return request({
        url: "/device/access/savePointTable",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 导入点表数据
export function importPointTableData(data) {
    return request({
        url: "/device/access/importPointTableData",
        portNum: "60003",
        method: "post",
        data: data,
    });
}
