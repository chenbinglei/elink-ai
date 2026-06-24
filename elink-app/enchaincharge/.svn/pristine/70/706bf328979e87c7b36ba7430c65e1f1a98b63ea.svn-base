import {
	ajax,
	dj_upload
} from '@/utils/request.js';

// 根据会员id查询基本信息
export function queryAppletUserInfoById(data) {
	return ajax({
		url: "/swebapp/userInfo/queryAppletUserInfoById",
		data
	})
};

// 编辑小程序用户
export function saveOrUpdateMemberInfo(data) {
	return ajax({
		url: "/swebapp/userInfo/updateAppletUser",
		data
	})
};

// 根据会员手机号上传头像
export function uploadMemberHeadByPhone(files, formData) {
	return dj_upload({
		url: `/swebapp/myoperation/uploadMemberHeadByPhone`,
		files,
		formData,
	})
};

// 根据会员id查询偏好设置
export function queryPreferenceSettingsInfoByMemberId(data) {
	return ajax({
		url: "/swebapp/settings/queryPreferenceSettingsInfoByMemberId",
		showLoading: true, //接口等待不转圈 
		data
	})
};

// 保存或编辑偏好设置
export function saveOrUpdatePreferenceSettings(data) {
	return ajax({
		url: "/swebapp/settings/saveOrUpdatePreferenceSettings",
		showLoading: true, //接口等待不转圈 
		data
	})
};


// 查询职业列表
export function queryProfessionData(data) {
	return ajax({
		url: "/swebapp/myoperation/queryProfessionData",
		showLoading: true, //接口等待不转圈 
		data
	})
};

// 查询全国省份列表
export function queryProvinceData(data) {
	return ajax({
		url: "/swebapp/myoperation/queryProvinceData",
		showLoading: true, //接口等待不转圈 
		data
	})
};


// 根据全国省编码id查询下面市级数据
export function queryCityDataByProvinceId(data) {
	return ajax({
		url: "/swebapp/myoperation/queryCityDataByProvinceId",
		showLoading: true, //接口等待不转圈 
		data
	})
};

// 根据全国市编码id查询下面区县级数据
export function queryAreaDataByCityId(data) {
	return ajax({
		url: "/swebapp/myoperation/queryAreaDataByCityId",
		showLoading: true, //接口等待不转圈 
		data
	})
};

// 修改会员微信通知设置
export function updateMemberWechatNotice(data) {
	return ajax({
		url: "/swebapp/settings/updateMemberWechatNotice",
		data
	})
};

// 提交小程序用户注销申请
export function submitAppletCancel(data) {
	return ajax({
		url: "/swebapp/userInfo/submitAppletCancel",
		data
	})
};

// 根据小程序用户id更新手机号
export function updateAppletUserPhoneById(data) {
	return ajax({
		url: "/swebapp/userInfo/updateAppletUserPhoneById",
		data
	})
};