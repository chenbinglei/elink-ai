import { calcNumberFun } from '@/common/common.js';

export default {
	/*
	 * 金额处理 数值处理
	 * num  当前数值
	 * toNA： 空值转换为
	 * isRetainNum：  保留的位数
	 * isZeroFill:  小数点位数不够是否补0
	 * */
	moneyTwoNum(num, isRetainNum = 2, toNA = "--", isZeroFill = true) {
		if (num === null || num === "" || num === undefined) return toNA;

		let money = num ? num : 0;
		let result = parseFloat(money);
		if (isNaN(result)) return toNA;

		let retainNum = "1";
		while (retainNum.length <= isRetainNum) retainNum += "0";
		result = Math.floor(calcNumberFun(money, retainNum, '*')) / retainNum;
		let s_x = result.toString();
		let pos_decimal = s_x.indexOf(".");
		if (pos_decimal < 0) {
			pos_decimal = s_x.length;
			s_x += ".";
		}

		// 位数不够进行补零
		if (isZeroFill) while (s_x.length <= pos_decimal + isRetainNum) s_x += "0";
		
		return s_x;
	},
	//  空数据处理
	numberNull(data) {
		switch (String(data)) {
			case "":
				return "0";
			case "--":
				return "0";
			case "null":
				return "0";
			case "undefined":
				return "0";
			default:
				return data;
		}
	},
	//手机好中间四位变****
	phoneFourRep(phone) {
		let mobile = String(phone);
		if (mobile) {
			return mobile.substr(0, 3) + "****" + mobile.substr(7);
		} else {
			return ''
		}
	},
	//  空数据处理
	moreData(data) {
		switch (String(data)) {
			case "":
				return "--";
			case "null":
				return "--";
			case "undefined":
				return "--";
			default:
				return data;
		}
	},
	// 空字符展示空
	emptyReplaceData(data) {
		switch (String(data)) {
			case "null":
				return "";
			case "undefined":
				return "";
			default:
				return data;
		}
	},
	textValue(text, usageWay = 1) {
		let span = usageWay == 1 ? '充' : '放';
		let str = text.replace(/充/g, span);
		if (usageWay == 2) {
			let str1 = str.replace(/满/g, '空');
			return str1
		}
		return str
	},
	// 策略类型
	strategyType(data, type) {
		switch (String(data)) {
			case "0":
				return Number(type) === 1 ? "满充" : "放空";
			case "1":
				return "定 SOC";
			case "2":
				return "定金额";
			case "3":
				return "定电量";
			default:
				return data;
		}
	},
	// 电桩类型
	electricPileType(value) {
		switch (String(value)) {
			case '28':
				return "交流";
			case '29':
				return "直流";
			case '30':
				return "V2G";
			default:
				return value;
		}
	},
	// 车辆状态
	vehicleStatus(value) {
		switch (String(value)) {
			case '0':
				return "未认证";
			case '1':
				return "认证";
			default:
				return value;
		}
	},
	// 国家标准
	nationalStandard(data) {
		switch (String(data)) {
			case "1":
				return "2011";
			case "2":
				return "2015";
			case "3":
				return "2011&2015";
			case "4":
				return "2023";
			default:
				return '--';
		}
	},
	// 结算状态
	settlementState(data) {
		switch (String(data)) {
			case "null":
				return "--";
			case "0":
				return "未结算";
			case "1":
				return "结算中";
			case "2":
				return "结算失败";
			case "3":
				return "结算成功";
			default:
				return data;
		}
	},
	isOrderly(status) {
		switch (String(status)) {
			case "1":
				return "是";
			case "2":
				return "否";
			default:
				return status;
		}
	},
	// 是否启用
	enableOrNot(value) {
		switch (String(value)) {
			case "1":
				return "启用";
			case "2":
				return "禁用";
			default:
				return "禁用";
		}
	},
	// 占用计费规则 --》 计费机制
	billingCdmType(value) {
		switch (String(value)) {
			case "1":
				return "自然天计费制";
			case "2":
				return "24小时计费制";
			default:
				return '--';
		}
	},
	// 停车费用类型
	parkCostType(data) {
		switch (String(data)) {
			case "0":
				return "免费停车";
			case "1":
				return "停车收费";
			case "2":
				return "限时免费";
			case "3":
				return "充电限免";
			case "255":
				return "参考场地实际收费标准";
			default:
				return data;
		}
	},
	// 枪工作状态
	gunWorkState(value) {
		let status = "";
		switch (String(value)) {
			case '1':
				status = "充电中";
				break;
			case '2':
				status = "放电中";
				break;
			case '3':
				status = "空闲";
				break;
			case '4':
				status = "占用";
				break;
			case '5':
				status = "故障";
				break;
			case '6':
				status = "离线";
				break;
			case '7':
				status = "未注册";
				break;
			case '8':
				status = "预约中";
				break;
			default:
				status = "未知";
				break;
		}
		return status;
	},
	// 明细类型
	detailType(status) {
		switch (String(status)) {
			case "1":
				return "收入";
			case "2":
				return "支出";
			default:
				return status;
		}
	},
	// 放电钱包交易类型
	disWalletTradeType(status) {
		switch (String(status)) {
			case "1":
				return "放电收入";
			case "2":
				return "充电预付";
			case "3":
				return "充电退款";
			default:
				return status;
		}
	},
	// 占用计费规则 --》 计费机制
	billingCdmType(value) {
		switch (String(value)) {
			case "1":
				return "自然天计费制";
			case "2":
				return "24小时计费制";
			default:
				return '--';
		}
	},
	// 阿拉伯数字“1“转中文数“一“
	toChineseNumber(n = 0, monoduplex = true) {
		if (!Number.isInteger(n) && n < 0) {
			throw Error('请输入自然数');
			return "--";
		}
		const digits = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九'];
		const positions = ['', '十', '百', '千', '万', '十万', '百万', '千万', '亿', '十亿', '百亿', '千亿'];
		const charArray = String(n).split('');
		let result = '';
		let prevIsZero = false;
		//处理0  deal zero
		for (let i = 0; i < charArray.length; i++) {
			const ch = charArray[i];
			if (ch !== '0' && !prevIsZero) {
				result += digits[parseInt(ch)] + positions[charArray.length - i - 1];
			} else if (ch === '0') {
				prevIsZero = true;
			} else if (ch !== '0' && prevIsZero) {
				result += '零' + digits[parseInt(ch)] + positions[charArray.length - i - 1];
			}
		}
		//处理十 deal ten
		if (n < 100) {
			result = result.replace('一十', '十');
		}

		if (monoduplex) {
			if (n === 1) result = "单";
			if (n === 2) result = "双";
		}

		return result;
	},
	//计算两者时间差
	timeDifference(endTime, startTime) { //di作为一个变量传进来
		//如果时间格式是正确的，那下面这一步转化时间格式就可以不用了
		let dateBegin = endTime ? new Date(endTime.replace(/-/g, "/")) : new Date(); //将-转化为/，使用new Date
		let dateEnd = startTime ? new Date(startTime.replace(/-/g, "/")) : new Date(); //获取当前时间
		let dateDiff = dateBegin.getTime() - dateEnd.getTime(); //时间差的毫秒数
		let dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000)); //计算出相差天数
		let leave1 = dateDiff % (24 * 3600 * 1000) //计算天数后剩余的毫秒数
		let hours = Math.floor(leave1 / (3600 * 1000)) //计算出小时数
		//计算相差分钟数
		let leave2 = leave1 % (3600 * 1000) //计算小时数后剩余的毫秒数
		let minutes = Math.floor(leave2 / (60 * 1000)) //计算相差分钟数
		//计算相差秒数
		let leave3 = leave2 % (60 * 1000) //计算分钟数后剩余的毫秒数
		let seconds = Math.round(leave3 / 1000)
		// console.log(" 相差 " + dayDiff + "天 " + hours + "小时 " + minutes + " 分钟" + seconds + " 秒")
		// console.log(dateDiff + "时间差的毫秒数", dayDiff + "计算出相差天数", leave1 + "计算天数后剩余的毫秒数", hours + "计算出小时数", minutes +
		// 	"计算相差分钟数", seconds + "计算相差秒数");

		let string = dayDiff + "天" + hours + "小时" + minutes + "分钟";
		if (dayDiff == 0) {
			string = hours + "小时 " + minutes + " 分钟";
		}
		if (hours == 0) {
			string = minutes + " 分钟";
		}
		return string
	}
}