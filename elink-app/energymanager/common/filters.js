import { calcNumberFun,isNumber } from "../utils/index.js";
export default {
	// 金额处理，保留两位小数
	moenyTwoNum (num) {
		let money = num ? num : 0;
		var result = parseFloat(money);
		if (isNaN(result)) {
			return '';
		}
		result = Math.round(money * 100) / 100;
		var s_x = result.toString();
		var pos_decimal = s_x.indexOf('.');
		if (pos_decimal < 0) {
			pos_decimal = s_x.length;
			s_x += '.';
		}
		while (s_x.length <= pos_decimal + 2) {
			s_x += '0';
		}
		return s_x;
	},
	// 放电支付方式
	disPayWay (status) {
		return "V2G钱包";
	},
	// 支付方式
	payWay (status) {
		switch (String(status)) {
			case "1":
				return "免支付";
			case "2":
				return "微信支付";
			case "3":
				return "支付宝支付";
			case "4":
				return "钱包余额";
			default:
				return status ?? '--';
		}
	},
	// 结算状态
	settlementState (status) {
		switch (String(status)) {
			case "0":
				return "未结算";
			case "1":
				return "结算关闭";
			case "2":
				return "结算失败";
			case "3":
				return "结算成功";
			default:
				return status ?? '--';
		}
	},
	// 时段类型
	periodType (data = "") {
		let filterStr = String(data);
		switch (filterStr) {
			case "1":
				return "尖时";
			case "2":
				return "峰时";
			case "3":
				return "平时";
			case "4":
				return "谷时";
			case "5":
				return "深谷";
			case "6":
				return "全天";
			default:
				return filterStr ?? '--';
		}
	},
	//  空数据处理
	numberNull (data) {
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
	// 开票状态
	invoicingState (data = "") {
		let filterStr = String(data);
		switch (filterStr) {
			case "1":
				return "已开发票";
			case "2":
				return "未开发票";
			default:
				return "--";
		}
	},
	// 补单状态
	repairStatus (status) {
		switch (String(status)) {
			case "0":
				return "挂单";
			case "1":
				return "自动恢复";
			case "2":
				return "人工恢复";
			default:
				return status ?? '--';
		}
	},
	// 启动方式
	pileRunMode (data = "") {
		let filterStr = String(data);
		switch (filterStr) {
			case "1":
				return "App";
			case "2":
				return "第三方平台";
			case "3":
				return "电卡";
			case "4":
				return "VIN码";
			case "5":
				return "电桩屏幕强制启动";
			case "6":
				return "有序控制";
			case "7":
				return "离线卡启动";
			default:
				return "未知";
		}
	},
	// 账号类型
	accountType (data = "") {
		let filterStr = String(data);
		switch (filterStr) {
			case "1":
				return "充/放电卡";
			case "2":
				return "VIN码";
			case "3":
				return "手机号";
			default:
				return "--";
		}
	},
	 // 数值超过万进行转换 并且保留两位小数
    numberValue (value) {
        let new_value = value * 1;
        if (isNumber(new_value)) {
            const unitValue = Number(value);
            if (unitValue >= 10000) {
                return this.moneyTwoNum(calcNumberFun(unitValue, 10000, '/'), 2, { toNA: '--', isZeroFill: false });
            } else {
                return this.moneyTwoNum(unitValue, 3, { toNA: '--', isZeroFill: false });
            }
        } else {
            return '--';
        }
    },
		 // 数值超过万 单位 + 万字
    numberUnits (value, unit = '') {
        let new_value = value * 1;
        if (isNumber(new_value)) {
            const unitValue = Number(value);
            if (unitValue >= 10000) {
                return "万" + unit;
            } else {
                return unit;
            }
        } else {
            return unit;
        }
    },
	moneyTwoNum (data) {
		let value = Number(data);
		if (isNaN(value)) return '--';
		return value.toFixed(2);
	},
	numberUnit (value) {
		let num = Number(value);

		// 判断是否为 NaN
		if (isNaN(num)) return '--';

		// 判断是否为 null 或 undefined
		if (value === null || value === undefined) return '--';

		// 如果值大于 10000，格式化为“万”
		if (num > 10000) {
			return (num / 10000).toFixed(2) + '万';
		}

		return num;
	},
	//手机好中间四位变****
	phoneFourRep (phone) {
		let mobile = String(phone);
		if (mobile) {
			return mobile.substr(0, 3) + "****" + mobile.substr(7);
		} else {
			return ''
		}
	},
	// 充电状态
	chargingState (value) {
		switch (String(value)) {
			case '0':
				return "未进行";
			case '1':
				return "充电中";
			case '2':
				return "充电完成";
			case '3':
				return "启动失败";
			case '4':
				return "订单挂起";
			case '5':
				return "订单取消";
			case '6':
				return "预约中";
			default:
				return '--';
		}
	},
	disChargingState (value) {
		switch (String(value)) {
			case '0':
				return "未进行";
			case '1':
				return "放电中";
			case '2':
				return "放电完成";
			case '3':
				return "启动失败";
			case '4':
				return "订单挂起";
			case '5':
				return "订单取消";
			case '6':
				return "预约中";
			default:
				return '--';
		}
	},
	refundStatus (status) {
		switch (String(status)) {
			case "1":
				return "正常退款";
			case "2":
				return "退款异常";
			default:
				return status ?? '--';
		}
	},
	//  空数据处理
	moreData (data) {

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
	emptyReplaceData (data) {
		switch (String(data)) {
			case "null":
				return "";
			case "undefined":
				return "";
			default:
				return data;
		}
	},
	textValue (text, usageWay = 1) {
		let span = usageWay == 1 ? '充' : '放';
		let str = text.replace(/充/g, span);
		if (usageWay == 2) {
			let str1 = str.replace(/满/g, '空');
			return str1
		}
		return str
	},
	// 策略类型
	strategyType (data, type) {
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
	electricPileType (value) {
		switch (String(value)) {
			case '1':
				return "直流";
			case '2':
				return "交流";
			case '3':
				return "V2G";
			default:
				return value;
		}
	},
	// 是否有序充/放电
	whetherOrNot (data = "") {
		let filterStr = String(data);
		switch (filterStr) {
			case "1":
				return "是";
			case "2":
				return "否";
			default:
				return '--';
		}
	},
	// 电桩类型
	pileType (data = "") {
		let filterStr = String(data);
		switch (filterStr) {
			case "28":
				return "交流";
			case "29":
				return "直流";
			case "30":
				return "V2G";
			default:
				return "--";
		}
	},
	// 车辆状态
	vehicleStatus (value) {
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
	nationalStandard (data) {
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
	settlementState (data) {
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
	isOrderly (status) {
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
	enableOrNot (value) {
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
	billingCdmType (value) {
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
	parkCostType (data) {
		switch (String(data)) {
			case "0":
				return "暂未开启";
			case "1":
				return "免费停车";
			case "2":
				return "限时免费";
			case "3":
				return "充电限免";
			case "4":
				return "停车收费";
			default:
				return data;
		}
	},
	// 枪工作状态
	gunWorkState (value) {
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
				status = "暂停";
				break;
			case '9':
				status = "预约中";
				break;
			default:
				status = "未知";
				break;
		}
		return status;
	},
	// 明细类型
	detailType (status) {
		switch (String(status)) {
			case "1":
				return "收入";
			case "2":
				return "支出";
			default:
				return status;
		}
	},
	// 巡检状态
	inspectHandStatus (status) {
		switch (String(status)) {
			case '1':
				return "未分配";
			case "2":
				return "未开启";
			case "3":
				return "巡检中";
			case "4":
				return "待验收";
			case "5":
				return "完结";
			default:
				return status;
		}
	},
	iseventLevel (status) {
		switch (String(status)) {
			case "1":
				return "次要";
			case "2":
				return "重要";
			case "3":
				return "紧急";
			case "4":
				return "提示";
			case "5":
				return "离线";

			default:
				return status;
		}

	},
	// 巡检结果
	inspectHandResult (status) {
		switch (String(status)) {
			case '1':
				return "未开始";
			case "2":
				return "巡检中";
			case "3":
				return "已完成";
			case "4":
				return "已放弃";

			default:
				return status;
		}
	},
	// 放电钱包交易类型
	disWalletTradeType (status) {
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
	billingCdmType (value) {
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
	toChineseNumber (n = 0, monoduplex = true) {
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
	timeDifference (endTime, startTime) { //di作为一个变量传进来
		//如果时间格式是正确的，那下面这一步转化时间格式就可以不用了
		var dateBegin = endTime ? new Date(endTime.replace(/-/g, "/")) : new Date(); //将-转化为/，使用new Date
		var dateEnd = startTime ? new Date(startTime.replace(/-/g, "/")) : new Date(); //获取当前时间
		var dateDiff = dateBegin.getTime() - dateEnd.getTime(); //时间差的毫秒数
		var dayDiff = Math.floor(dateDiff / (24 * 3600 * 1000)); //计算出相差天数
		var leave1 = dateDiff % (24 * 3600 * 1000) //计算天数后剩余的毫秒数
		var hours = Math.floor(leave1 / (3600 * 1000)) //计算出小时数
		//计算相差分钟数
		var leave2 = leave1 % (3600 * 1000) //计算小时数后剩余的毫秒数
		var minutes = Math.floor(leave2 / (60 * 1000)) //计算相差分钟数
		//计算相差秒数
		var leave3 = leave2 % (60 * 1000) //计算分钟数后剩余的毫秒数
		var seconds = Math.round(leave3 / 1000)

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