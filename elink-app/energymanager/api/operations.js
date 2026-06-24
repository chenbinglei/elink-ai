import {
	ajax
} from '@/utils/request.js';

// 查询地图站点列表
export function findAppInspectHandTaskList(data) {
	return ajax({
		url: "/devops/inspect/findAppInspectHandTaskList",
		nologinRequired:true, // 请求接口不需要登录
		data
	})
};

// 查询告警列表
export function queryAlarmList (data) {
	return ajax({
		url: "/devops/alarm/queryAlarmList",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};

// 查询告警列表
export function getAssetTypeList (data) {
	return ajax({
		url: "/devops/alarm/getAssetTypeList",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};