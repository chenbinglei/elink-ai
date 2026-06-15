import request from "@/utils/request";


// 查询模型列表
export function queryModelList(data) {
    return request({
        url: "/device/model/queryModelList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 新增编辑模型数据
export function saveModel(data) {
    return request({
        url: "/device/model/saveModel",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 获取设备类型列表
export function getDeviceTypeList(data) {
    return request({
        url: "/device/model/getDeviceTypeList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 获取资产分类列表
export function getAssetTypeList(data) {
    return request({
        url: "/device/model/getAssetTypeList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 删除模型数据
export function deleteModelById(data) {
    return request({
        url: "/device/model/deleteModelById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 模型发布(更新模型状态)
export function updateModelStatus(data) {
    return request({
        url: "/device/model/updateModelStatus",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据模型id查询模型详情数据
export function findModelDetailById(data) {
    return request({
        url: "/device/model/findModelDetailById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据模型id查询模型下面设备列表
export function findModelDeviceListByModelId(data) {
    return request({
        url: "/device/model/findModelDeviceListByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 绑定模型设备功能显示设置
export function bindModelFunctionData(data) {
    return request({
        url: "/device/model/bindModelFunctionData",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据模型id查询模型关联标准功能数据
export function findModelFunctionListByModelId(data) {
    return request({
        url: "/device/model/findModelFunctionListByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据模型id查询模型绑定标准功能列表
export function findModelBindFunctionByModelId(data) {
    return request({
        url: "/device/model/findModelBindFunctionByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 模型绑定标准功能数据
export function modelBindFunctionData(data) {
    return request({
        url: "/device/model/modelBindFunctionData",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据查询条件查询模型事件列表
export function findModelEventList(data) {
    return request({
        url: "/device/model/findModelEventList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 批量删除模型事件数据
export function batchDeleteModelEventByIds(data) {
    return request({
        url: "/device/model/batchDeleteModelEventByIds",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 获取模型事件相关可用功能点数据列表
export function getModelEventFunctionList(data) {
    return request({
        url: "/device/model/getModelEventFunctionList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据模型事件id查询模型事件详情
export function findModelEventListByModelId(data) {
    return request({
        url: "/device/model/findModelEventListByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 添加模型事件
export function saveModelEvent(data) {
    return request({
        url: "/device/model/saveModelEvent",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// --------------------》 扩展属性

// 根据模型id查询模型关联扩展属性数据
export function findModelReaListByModelId(data) {
    return request({
        url: "/device/model/findModelReaListByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 模型绑定扩展属性数据
export function modelBindReaData(data) {
    return request({
        url: "/device/model/modelBindReaData",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据模型id查询模型绑定扩展属性列表
export function findModelBindReaByModelId(data) {
    return request({
        url: "/device/model/findModelBindReaByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 编辑模型扩展属性默认值
export function updateModelReaValue(data) {
    return request({
        url: "/device/model/updateModelReaValue",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// --------------------》 扩展属性



// 根据模型id查询模型拓扑节点列表
export function findModelTopologyListByModelId(data) {
    return request({
        url: "/device/model/findModelTopologyListByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据拓扑节点id删除模型拓扑节点数据
export function deleteModelTopologyById(data) {
    return request({
        url: "/device/model/deleteModelTopologyById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 添加模型拓扑节点
export function saveModelTopology(data) {
    return request({
        url: "/device/model/saveModelTopology",
        portNum: 60003,
        method: "post",
        data: data,
    });
}


// ---------------------》故障定义

// 根据模型id查询电桩告警故障定义列表
export function findPileFaultListByModelId(data) {
    return request({
        url: "/device/model/findPileFaultListByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 导入电桩故障数据
export function importPileFaultList(data) {
    return request({
        url: "/device/model/importPileFaultList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 新增或编辑电桩故障定义数据
export function savePileFault(data) {
    return request({
        url: "/device/model/savePileFault",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据主键id删除电桩故障
export function deletePileFaultById(data) {
    return request({
        url: "/device/model/deletePileFaultById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}