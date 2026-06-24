// 此页方法都为全局方法
// 直接在组件中使用  this.方法名（） 调用就可以
// html中直接使用 @click="$noMultipleClicks(commitWork(id))"

import store from '@/store/index.js';
import { queryDeviceInfoByPileCode } from '@/firstPackage/api/index.js';


// 阻止按钮多次点击  使用：页面data中 添加 noClick:true,
export function $noMultipleClicks(methods, ...args) {
	let that = this;
	if (that.noClick) {
		that.noClick = false;
		methods(...args);
		setTimeout(function() {
			that.noClick = true;
		}, 2000)
	} else {
		console.log("请稍后点击");
	}
}


// 查看用户是否登录 
// operationType 操作类型 
//     1: 返回登录状态 是否为登录  2：未登录直接跳转登录页面
//     uniShowModal :是否展示提示框
export function $lockUserIfLogin(operationType = 1, uniShowModal = false) {

	// let loginUserStatus = true;
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
export function $scanCodeEvent(res, isReturnCode = false) {
	console.log(res);				// 3101020211228047  3101020211228037

	// res = {
	// 	type:"scanCode",
	// 	result:"https://cli.im?cid=3101020211228037&gid=0"
	// }

	let pileCode = '', gunCode = ''; // 当前桩编号
	if (res.type === 'terminalCode') {
		pileCode = res.result;

		if (pileCode === '' || pileCode === undefined) {
			uni.showToast({
				icon: 'none',
				title: '请输入正确的设备编号！',
				success: () => {
					this.isFang = true;
				}
			});
			return;
		}
	}

	if (res.type === 'scanCode') {

		// console.log(res.result);
		let urlParame = getParameterByName(res.result);

		// 根据@符号解析
		if (JSON.stringify(urlParame).indexOf("@") !== -1) urlParame = getParameterByName(res.result, "@");

		pileCode = urlParame.cid;
		gunCode = urlParame.gid || (isNaN(urlParame.data * 1) ? urlParame.data : urlParame.data * 1);
		// console.log(urlParame);
		if (pileCode === '' || pileCode === undefined) {
			uni.showToast({
				icon: 'none',
				title: '请扫描正确的二维码',
				success: () => {
					this.isFang = true;
				}
			});
			return;
		}
	}

	// 直接返回 桩编码 或者 枪编码
	if (isReturnCode) return { pileCode: pileCode, gunCode: gunCode };
	
	queryDeviceInfoByPileCode({ pileCode: pileCode }).then(result => {
		uni.redirectTo({
			url: `/firstPackage/pages/startDisAndcharging?pileCode=${pileCode}&gunCode=${gunCode}&startMode=${ this.startMode }`
		});
	}).catch(() => {
		this.isFang = true;
	})
}

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

// js  计算两数值不精准处理
export function calcNumberFun(num1, num2, calcStr) {
    let str1, // 转换为字符串的数字
        str2,
        ws1 = 0,// ws1，ws2 用来存储传入的num的小数点后的数字的位数
        ws2 = 0,// 赋默认值，解决当整数和小数运算时倍数计算错误导致的结果误差 
        bigger,// bigger和smaller用于加，减，除法找出小的那个数字，给后面补0，解决位数不对从而造成的计算错误的问题；乘法需要将结果除两个数字的倍数之和
        smaller,// 例如：加减除法中1.001 + 2.03 ，如果不给2.03进行补0，最后会变成1001+203，数字错位导致结果错误；乘法中1.12*1.1会放大为112*11，所以结果需要除以1000才会是正确的结果，112*11/1000=1.232
        zeroCount, // 需要补充0的个数
        isExistDot1, // 传入的数字是否存在小数点
        isExistDot2,
        sum,
        beishu = 1;
    // 将数字转换为字符串
    str1 = num1.toString();
    str2 = num2.toString();
    // 是否存在小数点（判断需要计算的数字是不是包含小数）
    isExistDot1 = str1.indexOf('.') !== -1;
    isExistDot2 = str2.indexOf('.') !== -1;
    // 取小数点后面的位数
    if (isExistDot1) {
        ws1 = str1.split('.')[1].length;
    }

    if (isExistDot2) {
        ws2 = str2.split('.')[1].length;
    }
    // 如ws1 和 ws2 无默认值，如果num1 或 num2 不是小数的话则 ws1 或 ws2 的值将为 undefined 
    // bigger 和 smaller 的值会和预期不符
    bigger = ws1 > ws2 ? ws1 : ws2;
    smaller = ws1 < ws2 ? ws1 : ws2;

    switch (calcStr) {
        // 加减法找出小的那个数字，给后面补0，解决位数不对从而造成的计算错误的问题
        // 例如：1.001 + 2.03 ，如果不给2.03进行补0，最后会变成1001+203，数字错位导致结果错误
        case "+":
        case "-":
        case "/":
            zeroCount = bigger - smaller;
            for (let i = 0; i < zeroCount; i++) {
                if (ws1 === smaller) {
                    str1 += "0";
                } else {
                    str2 += "0";
                }
            }
            break;
        case "*":
            // 乘法需要将结果除两个数字的倍数之和
            bigger = bigger + smaller;
            break;
        default:
            return "暂不支持的计算类型，现已支持的有加法、减法、乘法、除法";
    }

    // 去除数字中的小数点
    str1 = str1.replace('.', '');
    str2 = str2.replace('.', '');

    // 计算倍数，例如：1.001小数点后有三位，则需要乘 1000 变成 1001，变成整数后精度丢失问题则不会存在
    for (let i = 0; i < bigger; i++) {
        beishu *= 10; // 等价于beishu = beishu * 10;
    }
    num1 = parseInt(str1);
    num2 = parseInt(str2);
    // 进行最终计算并除相应倍数
    switch (calcStr) {
        case "+":
            sum = (num1 + num2) / beishu;
            break;
        case "-":
            sum = (num1 - num2) / beishu;
            break;
        case "*":
            sum = (num1 * num2) / beishu;
            break;
        case "/":
            sum = num1 / num2;
            /* 除数与被除数同时放大一定倍数，不影响结果，
            所以对数字进行放大对应倍数并进行补0操作后不用另对倍数做处理 */
            break;
        default:
            return "暂不支持的计算类型，现已支持的有加法、减法、乘法、除法";
    }

    return sum;
}

export default {
	$noMultipleClicks,
	$lockUserIfLogin,
	$clearUserInfo,
	$scanCodeEvent
}