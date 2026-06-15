import request from '@/utils/request'

// 根据图模id查询图模文件数据
export function findGraphById(data) {
    return request({
        url: '/configure/graph/findGraphById',
        portNum: 60008,
        method: 'post',
        data: data
    })
}

// 根据站点id查询站点的设备数据
export function findSiteDeviceListBySiteId(data) {
    return request({
        url: '/configure/graph/findSiteDeviceListBySiteId',
        portNum: 60008,
        method: 'post',
        data: data
    })
}

// 查询图元管理数据
export function findPelList(data) {
    return request({
        url: '/configure/pel/findPelList',
        portNum: 60008,
        method: 'post',
        data: data
    })
}


// ----------------------------------------------------------------- 》数据源数据

// 根据图模id查询关联数据源数据
export function findGraphSourceByGraphId(data) {
    return request({
        url: '/configure/graph/findGraphSourceByGraphId',
        portNum: 60008,
        method: 'post',
        data: data
    })
}

// 校验数据源是否调通
export function checkDataSource(data) {
    return request({
        url: '/configure/graph/checkDataSource',
        portNum: 60008,
        method: 'post',
        data: data
    })
}

// 图模关联数据源
export function relationDataSource(data) {
    return request({
        url: '/configure/graph/relationDataSource',
        portNum: 60008,
        method: 'post',
        data: data
    })
}
// ------------------------------------------------------------------- 》数据源数据


// -------------------------------------------------------------------- 》变量管理

// 根据图模id查询关联变量数据
export function findGraphVariableByGraphId(data) {
    return request({
        url: '/configure/graph/findGraphVariableByGraphId',
        portNum: 60008,
        method: 'post',
        data: data
    })
}

// 根据多个id批量删除图模变量数据
export function deleteGraphVariableByIds(data) {
    return request({
        url: '/configure/graph/deleteGraphVariableByIds',
        portNum: 60008,
        method: 'post',
        data: data
    })
}

// 新增或编辑图模变量数据
export function saveGraphVariable(data) {
    return request({
        url: '/configure/graph/saveGraphVariable',
        portNum: 60008,
        method: 'post',
        data: data
    })
}

// 批量编辑图模变量数据
export function saveAllGraphVariable(data) {
    return request({
        url: '/configure/graph/saveAllGraphVariable',
        portNum: 60008,
        method: 'post',
        data: data
    })
}
// ---------------------------------------------------------------------- 》变量管理



// 根据站点/设备id查询系统变量列表
export function findSystemVarListByDeviceId(data) {
    return request({
        url: '/configure/graph/findSystemVarListByDeviceId',
        portNum: 60008,
        method: 'post',
        data: data
    })
}
export function findComputeNodeListByDeviceId(data) {
    return request({
        url: '/configure/graph/findComputeNodeListByDeviceId',
        portNum: 60008,
        method: 'post',
        data: data
    })
}


// 根据站点/设备id查询功能点数据
export function findFunctionListByDeviceId(data) {
    return request({
        url: '/configure/graph/findFunctionListByDeviceId',
        portNum: 60008,
        method: 'post',
        data: data
    })
}