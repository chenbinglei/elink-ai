import {
	ajax
} from '@/utils/request.js';

// 查询地图站点列表
export function queryMapSiteList(data) {
	return ajax({
		url: "/swebapp/findPile/querySiteList",
		nologinRequired: true, // 请求接口不需要登录
		data
	})
};

// 启动充电桩
export function pileStart(data) {
	return ajax({
		url: "/swebapp/charge/pileStart",
		showLoadingText: "启动中..",
		data
	})
};

// 停止充电桩
export function pileStop(data) {
	return ajax({
		url: "/swebapp/charge/pileStop",
		showLoading: true,
		data
	})
};

// 根据会员id查询会员进行中的订单列表
export function findAppInHandOrderListByMemberId(data) {
	return ajax({
		url: "/swebapp/myorder/findAppInHandOrderListByMemberId",
		showLoading: true,
		data
	})
};

// 充放电中数据
export function appRealWebSocket(data) {
	return `${uni.getStorageSync('WEB_SOCKET')}/swebapp/appRealWebSocket/${data.memberId}/${data.orderType}`
};


// 根据小程序id查询小程序数据
export function findAppletByAppletCode(data) {
	return ajax({
		url: "/swebapp/userInfo/findAppletByAppletCode",
		nologinRequired: true, // 请求接口不需要登录
		showLoading: true,
		data
	})
};