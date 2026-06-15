import request from "@/utils/request";

// 查询数据转发列表数据
export function queryDataForwardList(data) {
    return request({
        url: "/system/configureCenter/queryDataForwardList",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 根据主键id删除数据转发数据
export function deleteDataForwardById(data) {
    return request({
        url: "/system/configureCenter/deleteDataForwardById",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 获取接入协议列表
export function getProtocolList(data) {
    return request({
        url: "/system/configureCenter/getProtocolList",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 根据协议标识查询协议字段列表
export function findProtocolFieldByCode(data) {
    return request({
        url: "/system/configureCenter/findProtocolFieldByCode",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 新建或编辑数据转发数据
export function saveDataForward(data) {
    return request({
        url: "/system/configureCenter/saveDataForward",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 根据主键id查询数据转发详情数据
export function findDataForwardById(data) {
    return request({
        url: "/system/configureCenter/findDataForwardById",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 更改数据转发状态
export function updateForwardStatus(data) {
    return request({
        url: "/system/configureCenter/updateForwardStatus",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 根据数据转发id查询数据配置数据
export function findDataConfigByForwardId(data) {
    return request({
        url: "/system/configureCenter/findDataConfigByForwardId",
        portNum: 60002,
        method: "post",
        data: data,
    });
}

// 批量编辑数据配置数据
export function batchUpdateDataConfig(data) {
    return request({
        url: "/system/configureCenter/batchUpdateDataConfig",
        portNum: 60002,
        method: "post",
        data: data,
    });
}