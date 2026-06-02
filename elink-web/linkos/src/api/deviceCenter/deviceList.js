import request from "@/utils/request";

// 获取站点设备树形结构
export function getSiteDeviceTreeList(data) {
    return request({
        url: "/device/device/getSiteDeviceTreeList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 查询设备数据列表
export function queryDeviceList(data) {
    return request({
        url: "/device/device/queryDeviceList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 删除设备数据
export function deleteDeviceById(data) {
    return request({
        url: "/device/device/deleteDeviceById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 新增编辑设备数据
export function saveDevice(data) {
    return request({
        url: "/device/device/saveDevice",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 批量添加设备数据
export function batchInsertDevice(data) {
    return request({
        url: "/device/device/batchInsertDevice",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据站点id查询设备资产父节点数据
export function findDeviceAssetList(data) {
    return request({
        url: "/device/device/findDeviceAssetList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}


// 根据设备类型id获取模型名称列表根据设备类型id获取模型名称列表
export function getModelNameListByTypeId(data) {
    return request({
        url: "/device/device/getModelNameListByTypeId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据模型id获取模型编辑字段列表
export function getModelFieldUpdateListByModelId(data) {
    return request({
        url: "/device/device/getModelFieldUpdateListByModelId",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据设备id查询设备基本信息数据
export function findDeviceBasicInfoById(data) {
    return request({
        url: "/device/device/findDeviceBasicInfoById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据设备id查询设备扩展属性列表数据
export function findDeviceReaListById(data) {
    return request({
        url: "/device/device/findDeviceReaListById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据查询条件查询设备事件列表
export function findDeviceEventList(data) {
    return request({
        url: "/device/device/findDeviceEventList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据设备事件id消除告警
export function updateDeviceEventStatusById(data) {
    return request({
        url: "/device/device/updateDeviceEventStatusById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}


// 根据设备id查询设备节点列表
export function findDeviceNodeListById(data) {
    return request({
        url: "/device/device/findDeviceNodeListById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 查询设备拓扑图编辑列表
export function findDeviceNodeUpdateList(data) {
    return request({
        url: "/device/device/findDeviceNodeUpdateList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}


// 批量绑定设备拓扑图数据
export function batchBindDeviceTopology(data) {
    return request({
        url: "/device/device/batchBindDeviceTopology",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据关联主键id删除设备节点数据
export function deleteDeviceTopologyById(data) {
    return request({
        url: "/device/device/deleteDeviceTopologyById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据设备id查询设备功能属性列表数据
export function findDeviceFunctionListById(data) {
    return request({
        url: "/device/device/findDeviceFunctionListById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 添加设备功能点字段数据
export function saveDeviceFunctionField(data) {
    return request({
        url: "/device/device/saveDeviceFunctionField",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据查询条件查询设备功能属性列表数据
export function queryDeviceFunctionValueList(data) {
    return request({
        url: "/device/device/queryDeviceFunctionValueList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据网关id查询网关子设备列表
export function findGatewaySubDeviceList(data) {
    return request({
        url: "/device/device/findGatewaySubDeviceList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据网关id查询网关下站点子设备列表
export function findSiteSubDeviceList(data) {
    return request({
        url: "/device/device/findSiteSubDeviceList",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 批量修改网关子设备数据
export function batchUpdateGatewaySubDevice(data) {
    return request({
        url: "/device/device/batchUpdateGatewaySubDevice",
        portNum: 60003,
        method: "post",
        data: data,
    });
}