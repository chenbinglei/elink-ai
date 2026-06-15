import request from "@/utils/request";

// 场站统计
export function queryStation(data) {
  return request({
    url: `/together/centralMonitor/statistics`,
    method: "post",
    data: data,
  });
}

// 场站状态总数统计
export function queryStatusTotal(data) {
  return request({
    url: `/together/centralMonitor/statusTotal`,
    method: "post",
    data: data,
  });
}
// 光伏场站分页
export function photovoltaicPage(data) {
  return request({
    url: `/together/photovoltaic/page`,
    method: "post",
    data: data,
  });
}

// 光伏场站详情
export function photovoltaicDetail(id) {
  return request({
    url: `/together/photovoltaic/detail`,
    method: "post",
    data: id,
  });
}

// 储能场站分页
export function energyStoragePage(data) {
  return request({
    url: `/together/energyStorage/page`,
    method: "post",
    data: data,
  });
}

// 储能场站详情
export function energyStorageDetail(id) {
  return request({
    url: `/together/energyStorage/detail`,
    method: "post",
    data: id,
  });
}

// 充电场站分页
export function batterySupplyPage(data) {
  return request({
    url: `/together/batterySupply/page`,
    method: "post",
    data: data,
  });
}

// 充电场站详情
export function batterySupplyDetail(id) {
  return request({
    url: `/together/batterySupply/detail`,
    method: "post",
    data: id,
  });
}

// 换电场站分页
export function batteryChangePage(data) {
  return request({
    url: `/together/batteryChange/page`,
    method: "post",
    data: data,
  });
}

// 换电场站详情
export function batteryChangeDetail(id) {
  return request({
    url: `/together/batteryChange/detail`,
    method: "post",
    data: id,
  });
}

//查询设备告警事件列表
export function warningGetEventList(systemId) {
  return request({
    url: `/together/warning/getEventList`,
    method: "post",
    data: systemId,
  });
}

//查询设备列表
export function getDeviceList(data) {
  return request({
    url: `/together/centralMonitor/getDeviceList`,
    method: "post",
    data
  });
}

//查询告警事件列表
export function searchAlarmEventList(data) {
  return request({
    url: `/together/warning/queryAlarmEventList`,
    method: "post",
    data
  });
}

//修改设备事件忽略状态
export function updateEventIgnoreStatus(data) {
  return request({
    url: `/together/warning/updateEventIgnoreStatus`,
    method: "post",
    data
  });
}


//告警站点统计
export function searchAlarmSiteCount(data) {
  return request({
    url: `/together/warning/alarmSiteCount`,
    method: "post",
    data
  });
}

//告警设备统计
export function searchAlarmDeviceCount(data) {
  return request({
    url: `/together/warning/alarmDeviceCount`,
    method: "post",
    data
  });
}

//告警设备统计
export function findDeviceAlarmEventList(data) {
  return request({
    url: `/together/warning/findDeviceAlarmEventList`,
    method: "post",
    data
  });
}
// 根据站点id查询拓扑节点数据
export function findSiteTopDataListBySiteId(data) {
  return request({
    url: `/together/centralMonitor/findSiteTopDataListBySiteId`,
    method: "post",
    data
  });
}
// 根据站点id查询关口表数据
export function findSiteGateTopBySiteId(data) {
  return request({
    url: `/together/centralMonitor/findSiteGateTopBySiteId`,
    method: "post",
    data
  });
}
// 根据站点id和关口表节点id查询曲线数据
export function findSiteTopCurveList(data) {
  return request({
    url: `/together/centralMonitor/findSiteTopCurveList`,
    method: "post",
    data
  });
}



