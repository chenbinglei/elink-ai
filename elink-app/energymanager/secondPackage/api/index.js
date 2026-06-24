import {
	ajax
} from '@/utils/request.js';

// 根据用户id查询租户站点列表
export function findSiteOverview(data) {
	return ajax({
		url: "/devops/monitor/findSiteOverview",
		nologinRequired:true, 
		data
	})
};
// 查询电站关口设备列表
export function findSiteGwDeviceList(data) {
	return ajax({
		url: "/devops/monitor/findSiteGwDeviceList",
		nologinRequired:true, 
		data
	})
};
// 查询电站关口总览曲线数据

export function findSiteGwCurveData(data) {
	return ajax({
		url: "/devops/monitor/findSiteGwCurveData",
		nologinRequired:true, 
		data
	})
};
// 查询电站关口总览静态数据
export function findSiteGwStaticData(data) {
	return ajax({
		url: "/devops/monitor/findSiteGwStaticData",
		nologinRequired:true, 
		data
	})
};

// 光伏
// 查询电站光伏概览曲线数据
export function findSitePvCurveData(data) {
	return ajax({
		url: "/devops/monitor/findSitePvCurveData",
		nologinRequired:true, 
		data
	})
};
// 查询电站光伏概览静态数据
export function findSitePvStaticData(data) {
	return ajax({
		url: "/devops/monitor/findSitePvStaticData",
		nologinRequired:true, 
		data
	})
};
// 储能
// 查询电站储能概览曲线数据
export function findSiteSeCurveData(data) {
	return ajax({
		url: "/devops/monitor/findSiteSeCurveData",
		nologinRequired:true, 
		data
	})
};
// 查询电站储能概览静态数据
export function findSiteSeStaticData(data) {
	return ajax({
		url: "/devops/monitor/findSiteSeStaticData",
		nologinRequired:true, 
		data
	})
};
// 电桩
// 查询电站电桩概览曲线数据
export function findSitePileCurveData(data) {
	return ajax({
		url: "/devops/monitor/findSitePileCurveData",
		nologinRequired:true, 
		data
	})
};
// 查询电站电桩概览静态数据
export function findSitePileStaticData(data) {
	return ajax({
		url: "/devops/monitor/findSitePileStaticData",
		nologinRequired:true, 
		data
	})
};
