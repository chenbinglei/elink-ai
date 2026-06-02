import request from '@/utils/request';

// 查询资产站点列表
export function querySiteNum(data) {
    return request({
        url: `/together/assetOverview/querySiteNum`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询资产统计和功率曲线数据
export function findPSCAssetCountData(data, scenarioType = 1) {
    let assetCountDataUrl = "findPvAssetCountData";  // 查询光伏资产统计和功率曲线数据
    if (scenarioType === 2) assetCountDataUrl = "findStorageAssetCountData";  // 查询储能资产统计和功率曲线数据
    if (scenarioType === 3) assetCountDataUrl = "findChargeAssetCountData";  // 查询充放电资产统计和功率曲线数据
    return request({
        url: `/together/assetOverview/${assetCountDataUrl}`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询光伏站点功率曲线数据
export function findPvSitePowerCurve(data) {
    return request({
        url: '/together/centralMonitor/findPvSitePowerCurve',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询光伏站点发电量曲线数据
export function findPvSiteQtCurve(data) {
    return request({
        url: '/together/centralMonitor/findPvSiteQtCurve',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询储能资产总览站点功率曲线数据
export function findStorageOverviewPowerCurve(data) {
    return request({
        url: '/together/assetOverview/findStorageOverviewPowerCurve',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询储能站点充放电量曲线数据
export function findStorageSiteQtCurve(data) {
    return request({
        url: '/together/centralMonitor/findStorageSiteQtCurve',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 查询充电站点功率曲线数据
export function findChargeSitePowerCurve(data) {
    return request({
        url: '/together/centralMonitor/findChargeSitePowerCurve',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询充电站电量统计曲线数据
export function findElecCountCurveData(data) {
    return request({
        url: '/together/centralMonitor/findElecCountCurveData',
        portNum: 60009,
        method: 'post',
        data: data
    });
}