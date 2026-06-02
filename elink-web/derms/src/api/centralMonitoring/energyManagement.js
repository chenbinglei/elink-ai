import request from '@/utils/request';

// 查询策略模板列表数据
export function queryTemplateList(data) {
    return request({
        url: '/together/strategy/queryTemplateList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 新增或编辑策略模板数据
export function saveOrUpdateTemplate(data) {
    return request({
        url: '/together/strategy/saveOrUpdateTemplate',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据主键id删除策略模板数据
export function deleteTemplateById(data) {
    return request({
        url: '/together/strategy/deleteTemplateById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查看策略模板文件内容
export function parseTemplateContent(data) {
    return request({
        url: '/together/strategy/parseTemplateContent',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 根据id查询策略模板详情
export function findTemplateById(data) {
    return request({
        url: '/together/strategy/findTemplateById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 控制策略-------------------------------》》》

// 根据站点id查询网关数据
export function findGatewayDataBySiteId(data) {
    return request({
        url: '/together/strategy/findGatewayDataBySiteId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 查询策略管理列表
export function queryStrategyList(data) {
    return request({
        url: '/together/strategy/queryStrategyList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 新增策略数据
export function saveStrategy(data) {
    return request({
        url: '/together/strategy/saveStrategy',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据策略id和类型查询策略配置
export function findStrategyById(data) {
    return request({
        url: '/together/strategy/findStrategyById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据策略id删除策略数据
export function deleteStrategyById(data) {
    return request({
        url: '/together/strategy/deleteStrategyById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 克隆策略数据
export function cloneStrategy(data) {
    return request({
        url: '/together/strategy/cloneStrategy',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 编辑策略数据
export function updateStrategy(data) {
    return request({
        url: '/together/strategy/updateStrategy',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 策略配置下发
export function issuedStrategy(data) {
    return request({
        url: '/together/strategy/issuedStrategy',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 状态监控 ----------------------------------------------  start》

// 查询系统设备列表
export function querySystemDeviceList(data) {
    return request({
        url: '/together/assetOverview/querySystemDeviceList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 查询模型标准功能列表
export function queryFunctionList(data) {
    return request({
        url: '/device/function/queryFunctionList',
        portNum: 60003,
        method: 'post',
        data: data
    });
}

// 查询储能、光伏、配电设备功能点曲线数据数据
export function queryDeviceFunCurveData(data,activeType) {
    return request({
        url: `/together/assetOverview/${ activeType === "dianZhuangXiTong"  ? "queryPileFunCurveData" : "queryDeviceFunCurveData" }`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询设备功能点实时数据
export function findDeviceFunctionListById(data) {
    return request({
        url: '/device/device/findDeviceFunctionListById',
        portNum: 60003,
        method: 'post',
        data: data
    });
}
// 状态监控 ----------------------------------------------  end》