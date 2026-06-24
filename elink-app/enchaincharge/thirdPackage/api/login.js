import {
	ajax
} from '@/utils/request.js';

// 登录
export function login(data) {
	return ajax({
		data,
		url: "/swebapp/sauth/appletLogin",
		nologinRequired: true, // 请求接口不需要登录
		showLoadingText: "登录中.."
	})
};


// 发送验证码
export function sendSecurityCode(data) {
	return ajax({
		nologinRequired: true, // 请求接口不需要登录
		url: `/swebapp/sms/sendSecurityCode`,
		data
	})
};


//校验验证码
export function checkSecurityCode(data) {
	return ajax({
		nologinRequired: true, // 请求接口不需要登录
		url: `/swebapp/sms/checkSecurityCode`,
		data
	})
};
