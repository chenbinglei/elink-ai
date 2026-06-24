import {
	ajax
} from '@/utils/request.js';


// 根据站点记录id查询详情信息
export function querySiteDetailsDataById(data) {
	return ajax({
		url: "/swebapp/findPile/querySiteDetailsById",
		data
	})
};


// 输入终端编号获取设备详情
export function queryDeviceInfoByPileCode(data) {
	return ajax({
		url: "/swebapp/findPile/queryDeviceInfoByPileCode",
		data
	})
};

// 会员启动充放电
export function memberChargeStart(data) {
	return ajax({
		url: "/swebapp/charge/appletChargeStart",
		showLoadingText: "正在启动..",
		data
	})
};

// 根据站点记录id查询站点下电桩详情信息
export function querySitePileDetailsDataById(data) {
	return ajax({
		url: "/swebapp/findPile/querySitePileDetailsById",
		data
	})
};

// 根据站点id查询站点充放电计费策略数据
export function findBillStrategyById(data) {
	return ajax({
		url: "/swebapp/findPile/findBillStrategyById",
		data
	})
};

// 根据设备id查询充放电和占桩价格信息
export function findDevicePriceById(data) {
	return ajax({
		url: "/swebapp/findPile/findDevicePriceById",
		data
	})
};

// 获取会员放电钱包可用余额
export function getMemberBagBalance(data) {
	return ajax({
		url: "/swebapp/scancodecharge/getMemberBagBalance",
		showLoading: true,
		data
	})
};

// // 开始充放电
// export function newAppWebSocket(data) {
// 	return `${uni.getStorageSync('WEB_SOCKET')}/data/newAppWebSocket/${data.pileCode}/${data.gunCode}/${data.uid}`
// };


// 启动中
export function appStartWebSocket(data) {
	return `${uni.getStorageSync('WEB_SOCKET')}/swebapp/appStartWebSocket/${data.pileCode}/${data.gunCode}/${data.memberId}`
};

// 根据会员id查询数据统计信息
export function findDataCountByMemberId(data) {
	return ajax({
		url: "/swebapp/myorder/findDataCountByMemberId",
		data
	})
}