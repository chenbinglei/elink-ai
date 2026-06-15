import request from "@/utils/request";

// 根据模型id查询模型绑定标准功能列表
export function queryFunctionList(data) {
    return request({
        url: "/device/function/queryFunctionList",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 新增编辑模型标准功能
export function saveFunction(data,isFunctionType = 1) {
    return request({
        url: `/device/${ isFunctionType === 4 ? 'model/updateModelFunctionValue' : 'function/saveFunction'}`,
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 删除模型标准功能属性
export function deleteFunctionById(data) {
    return request({
        url: "/device/function/deleteFunctionById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 根据主键id查询标准功能详情
export function findFunctionDetailById(data) {
    return request({
        url: "/device/function/findFunctionDetailById",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// 获取电桩协议字段列表
export function getPileRealFieldList(data) {
    return request({
        url: "/device/function/getPileRealFieldList",
        portNum: "60003",
        method: "post",
        data: data,
    });
}
