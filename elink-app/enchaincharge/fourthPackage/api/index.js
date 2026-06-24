import {
	ajax
} from '@/utils/request.js';

// 查询会员放电钱包账户列表
export function findAppletDisWalletListById(data) {
	return ajax({
		url: "/swebapp/userInfo/findAppletDisWalletListById",
		data
	})
};

// 查询账户交易明细列表
export function findBagTradeDetailList(data) {
	return ajax({
		url: "/swebapp/userInfo/findBagTradeDetailList",
		data
	})
};

export function queryAppletTradeList(data) {
	return ajax({
		url: "/swebapp/userInfo/queryAppletTradeList",
		data
	})
};