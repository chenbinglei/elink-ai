import request from "@/utils/request";
// 获取电站VS交流系统
export function getSiteAcSystem (data) {
  return request({
    url: `/together/customSystem/getSiteAcSystem`,
    method: "post",
    data: data,
     headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }
  });
}
// 电站电量统计

export function getSiteQtCurve (data) {
  return request({
    url: `/together/customSystem/getSiteQtCurve`,
    method: "post",
    data: data,
     headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }
  });
}
// 电站告警信息列表

export function getSiteAlarmList (data) {
  return request({
    url: `/together/customSystem/getSiteAlarmList`,
    method: "post",
    data: data,
    headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }
  });
}
// 获取光伏DC/DC设备数据

export function getPvDcDcDeviceData (data) {
  return request({
    url: `/together/customSystem/getPvDcDcDeviceData`,
    method: "post",
    data: data,
     headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }

  });
}
// 获取储能柜(储能DC/DC和电池蔟)设备数据
export function getSeCabinetDeviceData (data) {
  return request({
    url: `/together/customSystem/getSeCabinetDeviceData`,
    method: "post",
    data: data,
     headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }
  });
}
// 获取交流配电柜(关口)设备数据
export function getAcGGDDeviceData (data) {
  return request({
    url: `/together/customSystem/getAcGGDDeviceData`,
    method: "post",
    data: data,
     headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }
  });
}
// 获取直流配电柜(负载)设备数据
export function getDCADDeviceData (data) {
  return request({
    url: `/together/customSystem/getDCADDeviceData`,
    method: "post",
    data: data,
     headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }

  });
}
// 获取直流母线柜(母线柜)设备数据

export function getDCBusDeviceData (data) {
  return request({
    url: `/together/customSystem/getDCBusDeviceData`,
    method: "post",
    data: data,
     headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }

  });
}
export function getSiteSystemNearby (data) {
  return request({
    url: `/together/customSystem/getSiteSystemNearby`,
    method: "post",
    data: data,
     headers: {
      'X-No-Token': 'true' // 添加此头即可跳过token
    }

  });
}