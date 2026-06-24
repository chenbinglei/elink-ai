import {
	ajax
} from '@/utils/request.js';

// 根据用户id查询租户站点列表
export function getTenantSiteList(data) {
	return ajax({
		url: "/devops/homepage/getTenantSiteList",
		nologinRequired:true, 
		data
	})
};
// 根据多个场站id查询设备容量
export function getDeviceCap(data) {
	return ajax({
		url: "/devops/homepage/getDeviceCap",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};
// 查询电能趋势-电桩数据
  export function findAllEnergyPile(data) {
    return ajax({
      url: "/devops/homepage/findAllEnergyPile",
      nologinRequired: true, // 请求接口不需要登录
      data
    })
  }
  // 查询电能趋势-储能数据
  export function findAllEnergyStorage(data) {
    return ajax({
      url: "/devops/homepage/findAllEnergyStorage",
      nologinRequired: true, // 请求接口不需要登录
      data
    })
  }
  // 查询电能趋势-光伏数据
  export function findAllEnergyPv(data) {
    return ajax({
      url: "/devops/homepage/findAllEnergyPv",
      nologinRequired: true, // 请求接口不需要登录
      data
    })
  }
  // 根据场站id查询场站地图信息
  export function getSiteMapBySiteId(data) {
    return ajax({
      url: "/devops/monitor/getSiteMapBySiteId",
      nologinRequired: true, // 请求接口不需要登录
      data
    })
  }
