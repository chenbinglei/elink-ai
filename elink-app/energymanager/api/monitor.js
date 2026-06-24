import {
	ajax
} from '@/utils/request.js';

// 根据用户id查询租户站点列表
export function querySiteList(data) {
	return ajax({
		url: "/devops/monitor/querySiteList",
		nologinRequired:true, // 请求接口不需要登录

		data
	})
};

export function queryDeviceList(data) {
	return ajax({
		url: "/devops/monitor/queryDeviceList",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};
