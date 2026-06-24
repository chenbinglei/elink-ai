import {
	ajax,
	dj_upload
} from '@/utils/request.js';


// 查询我的订单列表
export function queryMyOrderListByPage(data) {
	return ajax({
		url: "/swebapp/myOrder/queryMyOrderListByPage",
		data
	})
};


// 查询会员占用订单列表
export function queryAppOrderHolderList(data) {
	return ajax({
		url: "/swebapp/myOrder/queryAppOrderHolderList",
		data
	})
};


// 占用订单支付创建占用订单
export function orderHolderPay(data) {
	return ajax({
		url: "/swebapp/scancodecharge/orderHolderPay",
		data
	})
};

// 根据订单编号获取订单详情
export function queryMyOrderDetailsByOrderNum(data) {
	return ajax({
		url: "/swebapp/myOrder/queryOrderDetailByOrderNum",
		showLoading: data.showLoading,
		data
	})
};

// 根据占用订单id查询占用订单详情
export function findAppOrderHolderById(data) {
	return ajax({
		url: "/swebapp/myorder/findAppOrderHolderById",
		data
	})
};

// 根据会员用户ID查询所关联的车辆信息列表
export function queryCarInfoByMemberId(data) {
	return ajax({
		url: "/swebapp/mycar/queryCarInfoByMemberId",
		data
	})
};

//会员车辆绑定或修改
export function memberCarBind(files, formData) {
	let memberCarBindUrl = "/swebapp/mycar/memberCarBind";
	if (files && files.length) {
		return dj_upload({
			url: memberCarBindUrl,
			files,
			formData,
		})
	} else {
		return ajax({
			url: memberCarBindUrl,
			data: formData
		})
	}
};

// 会员车辆解绑
export function memberCarUnbind(data) {
	return ajax({
		url: "/swebapp/mycar/memberCarUnbind",
		data
	})
};

// 查询所有车辆品牌列表
export function getCarBrandList(data) {
	return ajax({
		url: "/swebapp/mycar/getCarBrandList",
		data
	})
};

// 根据车辆品牌id查询车系数据
export function getSeriesListByBrandId(data) {
	return ajax({
		url: "/swebapp/mycar/getSeriesListByBrandId",
		data
	})
};

// 根据车系id查询车型数据
export function getCarTypeListBySeriesId(data) {
	return ajax({
		url: "/swebapp/mycar/getCarTypeListBySeriesId",
		data
	})
};

//解析车辆行驶证内容
export function parseCarLicense(files, formData) {
	return dj_upload({
		url: "/swebapp/mycar/parseCarLicense",
		files,
		formData,
	})
};
// --------------
// 充电开票
export function findOrderAppShowList(data) {
	return ajax({
		url: "/swebapp/invoice/findOrderAppShowList",
		data
	})
}
// 申请开票
export function applyInvoice(data) {
	return ajax({
		url: "/swebapp/invoice/applyInvoice",
		data
	})
}
// 根据小程序用户id查询发票抬头信息列表
export function findInvoiceTitleList(data) {
	return ajax({
		url: "/swebapp/invoice/findInvoiceTitleList",
		data
	})
}
// 添加或编辑发票抬头
export function saveInvoiceTitle(data) {
	return ajax({
		url: "/swebapp/invoice/saveInvoiceTitle",
		data
	})
}

export function findInvoiceDetailById(data) {
	return ajax({
		url: "/swebapp/invoice/findInvoiceDetailById",
		data
	})
}
// 开票记录

export function findInvoiceRecordList(data) {
	return ajax({
		url: "/swebapp/invoice/findInvoiceRecordList",
		data
	})
}
// 撤销申请
export function revokeInvoice(data) {
	return ajax({
		url: "/swebapp/invoice/revokeInvoice",
		data
	})
}