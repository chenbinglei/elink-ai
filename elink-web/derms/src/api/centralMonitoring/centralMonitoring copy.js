import request from '@/utils/request';

// 查询站点列表
export function findSiteListByUserId(data) {
    return request({
        url: '/device/siteInfo/findSiteListByUserId',
        // portNum: 60003,
        method: 'post',
        data: data
    });
}


// 根据设备id查询图形关联列表
export function findGraphRelevancyListByDeviceId(data) {
    return request({
        url: '/device/visual/findGraphRelevancyListByDeviceId',
        // portNum: 60003,
        method: 'post',
        data: data
    });
}






// ----------------------》 光伏电站监测 start《-----------------------------
// 查询光伏站点监测数据
export function findPvSiteMonitorData(data) {
    return request({
        url: '/together/centralMonitor/findPvSiteMonitorData',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 根据经纬度获取天气预报数据
export function findWeatherForecast(data) {
    return request({
        url: '/together/centralMonitor/findWeatherForecast',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// ----------------------》 光伏电站监测  end《-----------------------------





// ----------------------》 光伏逆变器监测 start《-----------------------------
// 查询光伏逆变器列表
export function findPvInverterList(data) {
    return request({
        url: '/together/centralMonitor/findPvInverterList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// ----------------------》 光伏逆变器监测  end《-----------------------------







// ----------------------》 充电站监测 start《-----------------------------
// 查询充电站监测数据
export function findChargeSiteMonitorData(data) {
    return request({
        url: '/together/centralMonitor/findChargeSiteMonitorData',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// ----------------------》 充电站监测  end《-----------------------------







// ----------------------》 电桩监控 start《-----------------------------
// 查询电桩设备列表
export function findPileDeviceList(data) {
    return request({
        url: '/together/centralMonitor/findPileDeviceList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// ----------------------》 电桩监控  end《-----------------------------





// ----------------------》 储能站监测 start《-----------------------------
// 查询储能站点监测数据
export function findStorageMonitorData(data) {
    return request({
        url: '/together/centralMonitor/findStorageMonitorData',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// ----------------------》 储能站监测  end《-----------------------------



// ----------------------》 储能PCS监控 start《-----------------------------
// 查询PCS设备监测列表数据
export function findPcsMonitorList(data) {
    return request({
        url: '/together/centralMonitor/findPcsMonitorList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// ----------------------》 储能PCS监控  end《-----------------------------





// ----------------------》 储能电池监控 start《-----------------------------
// 查询电池簇设备监测列表数据
export function findBatteryMonitorList(data) {
    return request({
        url: '/together/centralMonitor/findBatteryMonitorList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 分页查询电芯列表
export function findCellListByPage(data) {
    return request({
        url: '/together/centralMonitor/findCellListByPage',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// ----------------------》 储能电池监控  end《-----------------------------




// ----------------------》 储能辅助监控 start《-----------------------------
// 查询辅助设备监测列表数据
export function findAuxiliaryMonitorList(data) {
    return request({
        url: '/together/centralMonitor/findAuxiliaryMonitorList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}
// ----------------------》 储能辅助监控  end《-----------------------------




// ----------------------》 公共 start《-----------------------------

// 根据设备id查询设备所有未恢复事件告警数据
export function findNotRecoveEventList(data) {
    return request({
        url: '/together/centralMonitor/findNotRecoveEventList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询系统变量曲线数据   or  查询设备功能点曲线值数据
export function findSystemVarOrFunctionCurveData(data,isRequestType = 1) {
    let requestLink = "findSystemVarCurveData";  // 查询系统变量曲线数据
    if(isRequestType === 2) requestLink = "queryDeviceFunctionCurveData"; // 查询设备功能点曲线值数据
    if(isRequestType === 3) requestLink = "findElecCountCurveData";  // 查询充电站电量统计曲线数据

    if(isRequestType === 4) requestLink = "findPvSitePowerCurve";  // 查询光伏站点功率曲线数据
    if(isRequestType === 5) requestLink = "findPvSiteQtCurve";  // 查询光伏站点发电量曲线数据

    if(isRequestType === 6) requestLink = "findStorageSitePowerCurve";  // 查询储能站点功率曲线数据
    if(isRequestType === 7) requestLink = "findStorageSiteQtCurve";  // 查询储能站点充放电量曲线数据

    if(isRequestType === 8) requestLink = "findPcsChargeQtCurve";  // 查询PCS设备充放电量曲线数据

    if(isRequestType === 9) requestLink = "findChargeSitePowerCurve";  // 查询充电站点功率曲线数据

    return request({
        url: `/together/centralMonitor/${ requestLink }`,
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 查询设备遥测字段数据列表
export function findDeviceTelemetryList(data) {
    return request({
        url: '/together/centralMonitor/findDeviceTelemetryList',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// 添加设备字段设置
export function saveDeviceDeviceFieldSet(data) {
    return request({
        url: '/together/centralMonitor/saveDeviceDeviceFieldSet',
        portNum: 60009,
        method: 'post',
        data: data
    });
}


// 根据站点/设备id查询所关联所有系统变量数据列表
export function findSystemVarDataListById(data) {
    return request({
        url: '/together/centralMonitor/findSystemVarDataListById',
        portNum: 60009,
        method: 'post',
        data: data
    });
}

// ----------------------》 公共  end《-----------------------------
// 根据站点id获取天气预报
export function getWeatherDayListBySiteId(data) {
    return request({
        url: '/energy/centralMonitor/getWeatherDayListBySiteId',
        portNum: 60009,
        method: 'post',
        data: data
    });
}