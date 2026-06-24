import store from '@/store' //引入vuex
import { $lockUserIfLogin } from "@/common/common.js";

export function base_info(data) {
	const base_info = {};

	try {
		// #ifdef APP-PLUS
		base_info.clientId = uni.getStorageSync('USER_CLIENTINFO').clientid;
		base_info.imei = plus.device.imei;
		base_info.uuid = plus.device.uuid;
		base_info.os = (plus.os.name == 'iOS') ? 2 : (plus.os.name == 'Android') ? 1 : 0; //操作系统，如：0(其它，默认)，1(安卓)，2(IOS)
		// #endif

		// #ifdef APP-PLUS || H5
		base_info.platform = 'app'; //平台信息
		// #endif

		// #ifdef MP-WEIXIN
		base_info.platform = 'wechat'; //平台信息
		// #endif

		// #ifdef MP-ALIPAY    
		base_info.platform = 'alipay'; //平台信息
		// #endif
	} catch (e) {
		//TODO handle the exception
		base_info.message = "平台信息获取失败！！！";
	}

	try {
		const res = uni.getSystemInfoSync();
		base_info.model = res.model;
		base_info.osv = res.version;
		base_info.h = res.screenWidth;
		base_info.w = res.screenHeight;
	} catch (e) {
		//TODO handle the exception
		base_info.message = "系统信息获取失败！！！";
	}

	try {
		const accountInfo = uni.getAccountInfoSync();
		base_info.appletKey = accountInfo.miniProgram?.appId; // 小程序appid
		base_info.timestamp = new Date().getTime();
	} catch (e) {
		//TODO handle the exception
		base_info.message = "小程序配置信息获取失败！！！";
	}

	try {
		if ($lockUserIfLogin()) {
			// base_info.memberId = "UJiOXhp6DfmzD6eADRIW2Q==";
			// base_info.phone = "ygCn5nQFYazUmp6m47n0-g==";
			base_info.phoneNum = store.state.userInfo.phone;
			base_info.appletUserId = store.state.userInfo.memberId;
			base_info.access_token = store.state.userInfo.accessToken;
		}
	} catch (e) {
		//TODO handle the exception
		base_info.message = "登录信息获取失败！！！";
	}

	return base_info
};