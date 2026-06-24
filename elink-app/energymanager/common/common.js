// 此页方法都为全局方法
// 直接在组件中使用  this.方法名（） 调用就可以
// html中直接使用 @click="$noMultipleClicks(commitWork(id))"

import store from '@/store/index.js';
// import { queryDeviceInfoByPileCode } from '@/firstPackage/api/index.js';


// 阻止按钮多次点击  使用：页面data中 添加 noClick:true,
export function $noMultipleClicks(methods, ...args) {
	let that = this;
	if (that.noClick) {
		that.noClick = false;
		methods(...args);
		setTimeout(function() {
			that.noClick = true;
		}, 1000)
	} else {
		console.log("请稍后点击");
	}
}


// 查看用户是否登录 
// operationType 操作类型 
//     1: 返回登录状态 是否为登录  2：未登录直接跳转登录页面
//     uniShowModal :是否展示提示框
export function $lockUserIfLogin(operationType = 1, uniShowModal = false) {

	// return  true

	let loginUserStatus = store.state.userInfo ? true : false;

	// 用户是否登录 
	if (operationType === 1) return loginUserStatus

	if (operationType === 2) {

		if (!loginUserStatus) {

			if (!uniShowModal) {
				uni.navigateTo({
					url: "/thirdPackage/login/login"
				});

				return false
			}

			uni.showModal({
				title: '提示',
				content: "您还未登录，是否前往登录?",
				confirmColor: '#3296FA',
				showCancel: false,
				success: (res) => {
					if (res.confirm) {
						uni.navigateTo({
							url: "/pages/login/login"
						});
					}
				}
			});

			return false
		}

		return true
	}
}

// 清除用户登录信息
export function $clearUserInfo(toastText = "") {
	uni.removeStorageSync('USER_INFO');
	store.dispatch('alterUserInfo', null);

	uni.switchTab({
		url: '/pages/station/index',
		success: () => {
			uni.showToast({
				icon: 'none',
				title: toastText ? toastText : '退出登录成功。'
			});
		}
	});
}


// 扫码调用公共方法  调转单枪 或者多枪详情
// export function $scanCodeEvent(res, isReturnCode = false) {
// 	// console.log(res);				// 3101020211228047  3101020211228037

// 	// res = {
// 	// 	type:"scanCode",
// 	// 	result:"https://cli.im?cid=3101020211228037&gid=0"
// 	// }

// 	let pileCode = '', gunCode = ''; // 当前桩编号
// 	if (res.type === 'terminalCode') {
// 		pileCode = res.result;

// 		if (pileCode === '' || pileCode === undefined) {
// 			uni.showToast({
// 				icon: 'none',
// 				title: '请输入正确的设备编号！',
// 				success: () => {
// 					this.isFang = true;
// 				}
// 			});
// 			return;
// 		}
// 	}

// 	if (res.type === 'scanCode') {

// 		// console.log(res.result);
// 		let urlParame = getParameterByName(res.result);

// 		// 根据@符号解析
// 		if (JSON.stringify(urlParame).indexOf("@") !== -1) urlParame = getParameterByName(res.result, "@");

// 		pileCode = urlParame.cid;
// 		gunCode = urlParame.gid || (isNaN(urlParame.data * 1) ? urlParame.data : urlParame.data * 1);
// 		// console.log(urlParame);
// 		if (pileCode === '' || pileCode === undefined) {
// 			uni.showToast({
// 				icon: 'none',
// 				title: '请扫描正确的二维码',
// 				success: () => {
// 					this.isFang = true;
// 				}
// 			});
// 			return;
// 		}
// 	}

// 	// 直接返回 桩编码 或者 枪编码
// 	if (isReturnCode) return { pileCode: pileCode, gunCode: gunCode };
	
// 	queryDeviceInfoByPileCode({ pileCode: pileCode }).then(result => {
// 		uni.redirectTo({
// 			url: `/firstPackage/pages/startDisAndcharging?pileCode=${pileCode}&gunCode=${gunCode}&startMode=${ this.startMode }`
// 		});
// 	}).catch(() => {
// 		this.isFang = true;
// 	})
// }

// a对象中的属性值赋值给b(若b中存在该属性)
export function $getValue(a, b) {
	Object.keys(a).forEach(key => {
		if (b.hasOwnProperty(key)) {
			b[key] = a[key];
		}
	})
}


//数组对象去重  根据 key 属性字段名称
export function $deWeight(arr, key) {
	for (var i = 0; i < arr.length - 1; i++) {
		for (var j = i + 1; j < arr.length; j++) {
			if (arr[i][key] == arr[j][key]) {
				arr.splice(j, 1);
				//因为数组长度减小1，所以直接 j++ 会漏掉一个元素，所以要 j--
				j--;
			}
		}
	}
	return arr;
}

/* 获取字符串url参数 根据 & 符 进行截取 */
export function getParameterByName(url, symbol = "&") {
	let theRequest = new Object();
	if (url.indexOf("?") != -1 && url.indexOf("=") != -1) {
		let strs = url.split('?')[1].split(symbol);
		for (let i = 0; i < strs.length; i++) {
			theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
		}
	}
	return theRequest
}

/**
 * css 颜色 hex 格式 转 rgba
 */
export function colorHexTurnRgba(hex, transparent) {
    if (hex[0] !== '#') return hex
    let repairLength = 9 - hex.length;
    for (let i = 0; i < repairLength; i++) hex = hex + "0";
    let r = parseInt(hex.slice(1, 3), 16)
    let g = parseInt(hex.slice(3, 5), 16)
    let b = parseInt(hex.slice(5, 7), 16)
    let a = parseInt(hex.slice(7, 9), 16)
    return 'rgba(' + r + ',' + g + ',' + b + ',' + (transparent ? transparent : a) + ')'
}

//枪的提示语
export function gunStatusToastTitle(value) {
	let status = "";
	switch (String(value)) {
		case "1":
			status = "当前设备正在充电中！";
			break;
		case "2":
			status = "当前设备正在进行放电！";
			break;
		case "3":
			status = "请先插入枪重新启动设备！";
			break;
		case "4":
			status = "当前设备未工作，无法手动停止！";
			break;
		case "5":
			status = "当前设备发生故障啦！";
			break;
		case "6":
			status = "当前设备离线啦！";
			break;
		case "7":
			status = "当前设备平台未进行注册！";
			break;
		case "8":
			status = "当前设备已暂停使用！";
			break;
		case "9":
			status = "当前设备已被预约，请更换！";
			break;
		default:
			status = "获取枪状态失败，请返回上一级重试！";
			break;
	}
	return status
}


export default {
	$noMultipleClicks,
	$lockUserIfLogin,
	$clearUserInfo,
	// $scanCodeEvent
}