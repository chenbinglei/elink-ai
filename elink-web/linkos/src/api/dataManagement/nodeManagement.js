import request from "@/utils/request";

// 分页查询计算节点数据
export function findComputeNodeByPage(data) {
    return request({
        url: "/scrontab/computeNode/findComputeNodeByPage",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 根据节点id删除计算节点数据
export function deleteAllComputeNodeById(data) {
    return request({
        url: "/scrontab/computeNode/deleteAllComputeNodeById",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 保存或编辑计算节点信息
export function saveOrUpdateComputeNodeInfo(data) {
    return request({
        url: "/scrontab/computeNode/saveOrUpdateComputeNodeInfo",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 根据站点id查询下面设备列表
export function findSiteDeviceDataById(data) {
    return request({
        url: "/scrontab/computeNode/findSiteDeviceDataById",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 根据站点/设备id查询计算节点列表
export function findComputeNodeListById(data) {
    return request({
        url: "/scrontab/computeNode/findComputeNodeListById",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 根据设备id查询功能点列表
export function findDeviceFunctionListById(data) {
    return request({
        url: "/scrontab/computeNode/findDeviceFunctionListById",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 根据站点/设备id查询计算节点列表 / 根据设备id查询功能点列表
export function findComputeNodeAndFunctionListById(data,searchType = 'gnd') {
    return request({
        url: `/scrontab/computeNode/${ searchType === 'gnd' ? 'findDeviceFunctionListById' : 'findComputeNodeListById' }`,
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 根据id查询计算节点详情
export function findComputeNodeInfoById(data) {
    return request({
        url: "/scrontab/computeNode/findComputeNodeInfoById",
        portNum: 60006,
        method: "post",
        data: data,
    });
}


// 根据多个节点id查询本地缓存数据
export function findLocalCacheDataByIds(data) {
    return request({
        url: "/scrontab/computeNode/findLocalCacheDataByIds",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 分页查询节点日志信息
export function findNodeLogInfoListByPage(data) {
    return request({
        url: "/scrontab/computeNode/findNodeLogInfoListByPage",
        portNum: 60006,
        method: "post",
        data: data,
    });
}

// 清除日志
export function removeNodeLogInfo(data) {
    return request({
        url: "/scrontab/computeNode/removeNodeLogInfo",
        portNum: 60006,
        method: "post",
        data: data,
    });
}
