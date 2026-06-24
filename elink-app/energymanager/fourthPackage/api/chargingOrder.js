import {
	ajax
} from '@/utils/request.js';
// 分页查询订单记录列表信息
export function findOrderRecordListByPage(data) {
	return ajax({
		url: "/together/order/findOrderRecordListByPage",
		nologinRequired:true, 
		data
	})
};
// 订单退款
export function orderRefund(data) {
	return ajax({
		url: "/together/order/order/orderRefund",
		nologinRequired:true, 
		data
	})
};
// 更新订单状态
export function updateOrderStatus(data) {
	return ajax({
		url: "/together/order/updateOrderStatus",
		nologinRequired:true, 
		data
	})
};
// 查询订单详情
export function findOrderRecordInfoById(data) {
	return ajax({
		url: "/together/order/findOrderRecordInfoById",
		nologinRequired:true, 
		data
	})
};
// 过程数据
export function findProcessAnalysisByOrderId(data) {
	return ajax({
		url: "/together/order/findProcessAnalysisByOrderId",
		nologinRequired:true, 
		data
	})
};
// 运营分析
export function countOperationCurve(data) {
	return ajax({
		url: "/together/operationAnalysis/countOperationCurve",
		nologinRequired:true, 
		data
	})
};
// 运营分析
export function countOperationOverview(data) {
	return ajax({
		url: "/together/operationAnalysis/countOperationOverview",
		nologinRequired:true, 
		data
	})
};

