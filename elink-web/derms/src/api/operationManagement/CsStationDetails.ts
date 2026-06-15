import request from '@/utils/request';

// 根据站点id查询基本详情数据
export function findSiteInfoById(data) {
    return request({
        url: "/device/siteInfo/findSiteInfoById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 新增或编辑站点数据
export function saveOrUpdateSiteInfo(data) {
    return request({
        url: "/device/siteInfo/saveOrUpdateSiteInfo",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据站点id操作站点图片
export function updateSiteImageById(data) {
    return request({
        url: "/device/siteInfo/updateSiteImageById",
        portNum: 60003,
        method: "post",
        data: data,
    });
}

// 根据坐标获取区域地址
export function getAreaAddressByCoordinates(data) {
    return request({
        url: "/device/siteInfo/getAreaAddressByCoordinates",
        portNum: "60003",
        method: "post",
        data: data,
    });
}

// -------------------------------》 网关管理

// 根据站点id查询网关状态列表数据
export function findGatewayStatusListById(data) {
    return request({
        url: '/together/siteInfo/findGatewayStatusListById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据网关id查询网关子设备列表数据
export function findGatewayChildDeviceById(data) {
    return request({
        url: '/together/siteInfo/findGatewayChildDeviceById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 根据网关id查询关联所有平台信息
export function findGatWayPlatformInfo(data) {
    return request({
        url: '/together/siteInfo/findGatWayPlatformInfo',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 添加网关关联平台信息
export function saveGatWayPlatformInfo(data) {
    return request({
        url: '/together/siteInfo/saveGatWayPlatformInfo',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 网关重启
export function rebootGateWey(data) {
    return request({
        url: '/together/siteInfo/rebootGateWey',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// -------------------------------》 充放电价格

// 根据站点id查询定价记录和生效中的价格配置
export function findFixPriceRecordList(data) {
    return request({
        url: '/together/siteInfo/findFixPriceRecordList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 添加充放电价格信息
export function saveChargerPriceInfo(data) {
    return request({
        url: '/together/siteInfo/saveChargerPriceInfo',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据价格id取消待生效价格信息
export function deletePriceInfoById(data) {
    return request({
        url: '/together/siteInfo/deletePriceInfoById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据价格id查询价格详情
export function findPriceDetailsById(data) {
    return request({
        url: '/together/siteInfo/findPriceDetailsById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据价格id修改价格状态
export function updatePriceStateById(data) {
    return request({
        url: '/together/siteInfo/updatePriceStateById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 应用充放电价格到指定电站下
export function applyPriceInfoById(data) {
    return request({
        url: '/together/siteInfo/applyPriceInfoById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// -------------------------------》 占桩价格

// 根据站点id查询占桩价格信息
export function findOccupyPilePriceInfoById(data) {
    return request({
        url: '/together/siteInfo/findOccupyPilePriceInfoById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 添加修改占桩价格信息
export function saveOccupyPilePriceInfo(data) {
    return request({
        url: '/together/siteInfo/saveOccupyPilePriceInfo',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据占桩id删除指定占桩费率信息
export function deleteOccupyPilePriceById(data) {
    return request({
        url: '/together/siteInfo/deleteOccupyPilePriceById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// --------------------》  白名单列表

// 根据站点id查询白名单信息列表
export function findSiteWhiteRosterList(data) {
    return request({
        url: '/together/siteInfo/findSiteWhiteRosterList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 新增或编辑白名单信息
export function saveWhiteRosterInfo(data) {
    return request({
        url: '/together/siteInfo/saveWhiteRosterInfo',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据白名单id删除相关信息
export function deleteSiteWhiteRosterById(data) {
    return request({
        url: '/together/siteInfo/deleteSiteWhiteRosterById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询站点白名单模式
export function findRosterModeBySiteId(data) {
    return request({
        url: "/together/siteInfo/findRosterModeBySiteId",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 保存或编辑白名单模式
export function saveOrUpdateRosterMode(data) {
    return request({
        url: "/together/siteInfo/saveOrUpdateRosterMode",
        portNum: 60009,
        method: "post",
        data: data,
    });
}

// 根据地址获取经纬度
export function getLonAndLatByAddress(data) {
    return request({
        url: "/device/siteInfo/getLonAndLatByAddress",
        // portNum: 60003,
        method: "post",
        data: data,
    });
}

