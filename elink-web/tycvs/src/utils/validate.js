// 用户名
export function isValidUsername(str) {
	const reg = /^[a-zA-Z0-9]{2,25}$/
	return reg.test(str)
}

// 不能存在空格
export function hasWhiteSpace(str){
	const reg = /\s/g;
	return reg.test(str)
}

// 手机号
export function mobile(str) {
	const reg = /^1[3-9]\d{9}$/;
	return reg.test(str)
}

// 密码
export function isValidPassword(str) {
	const reg = /^(?![0-9]+$)(?![A-Z]+$)(?![a-z]+$)(?![`~!@#$%^&.*"“”_+<>{}\/'[\]]+$)[0-9|A-Z|a-z|`~!@#$%^&.*_"“”+<>{}\/'[\]]{6,18}$/;
	return reg.test(str)
}

// 银行账号
export function isValidBankAccount(str) {
	const reg = /^\d{16}|\d{19}$/;
	return reg.test(str)
}

// 邮箱
export function isValidEmail(str) {
	const reg = /^([a-zA-Z]|[0-9])(\w|-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
	return reg.test(str)
}

// 汉字
export function CNName(str) {
	const reg = /^[\u4e00-\u9fa5]+$/
	return reg.test(str)
}

// 汉字、字母或数字开头，支持下划线、&及括号的组合
export function someCharMap(str) {
	const reg = /^[\u4e00-\u9fa5a-zA-Z0-9][\u4e00-\u9fa5\w{}\[\]【】()（）&]+$/
	return reg.test(str)
}

// 汉字、字母的组合
export function CNENCharMap(str) {
	const reg = /^[\u4e00-\u9fa5a-zA-Z]+$/
	return reg.test(str)
}

//汉字 数字 字母 下划线（不包含特殊字符）
export function notCharMap(str) {
	const reg = /^[\u4e00-\u9fa5\w]+$/
	return reg.test(str)
}

//名称（包含常用字符）
export function commonCharName(str) {
	const reg = /^[|'".\[/,\]【】+*%~()#@!！?？\u4e00-\u9fa5\w-]{2,20}$/
	return reg.test(str)
}

// 空或正整数或0
export function null0to(str) {
	const reg = /^\d*$/
	return reg.test(str)
}

// 字母 跟 数字 组合
export function letterAndNumber(str){
	const reg = /^(?=.*\d)(?=.*[a-zA-Z])[\da-zA-Z]{2,}$/
	return reg.test(str)
}

// 数字1-255
export function num1to255(str) {
	const reg = /^([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))$/
	return reg.test(str)
}

// 数字0-99999
export function num0to999(str) {
	const reg = /^\+?[0-9]\d{0,2}(\.\d*)?$/
	return reg.test(str)
}

// 数字1-9999
export function num1to9999(str) {
	const reg = /^\+?[1-9]\d{0,3}(\.\d*)?$/
	return reg.test(str)
}

// 数字1-999999
export function num1to999999(str) {
	const reg = /^\+?[1-9]\d{0,5}(\.\d*)?$/
	return reg.test(str)
}

// 数字0-99999
export function num0to99999(str) {
	const reg = /^\+?[0-9]\d{0,4}(\.\d*)?$/
	return reg.test(str)
}

// 数字0-99999999  八位
export function num0to9999999(str) {
	const reg = /^\+?[0-9]\d{0,7}(\.\d*)?$/
	return reg.test(str)
}

// 数字0-999999
export function num0to999999(str) {
	const reg = /^\+?[0-9]\d{0,5}(\.\d*)?$/
	return reg.test(str)
}

// 数字0-9999带小数
export function num0to9999dot(str) {
	const reg = /^([1-9]\d{0,3}|0)(\.\d{0,2})?$/
	return reg.test(str)
}

// 数字0-99999带小数
export function num0to99999dot(str) {
	const reg = /^([1-9]\d{0,4}|0)(\.\d{0,2})?$/
	return reg.test(str)
}

// 数字0-9999999带小数
export function num0to9999999dot(str) {
	const reg = /^([1-9]\d{0,6}|0)(\.\d{0,3})?$/
	return reg.test(str)
}

// 数字0.01-9999999带小数
export function greater0to9999999dot(str) {
	if (str / 1 === 0) {
		return false;
	} else {
		const reg = /(^[1-9]([0-9]{0,6})?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/
		return reg.test(str)
	}
}

// 数字0.01-999999带小数
export function greater0to999999dot(str) {
	if (str / 1 === 0) {
		return false;
	} else {
		const reg = /(^[1-9]([0-9]{0,5})?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/
		return reg.test(str)
	}
}

// -999999.99-0
export function minus0to999999dot(str) {
	if (str * 1 === 0) {
		return true;
	} else {
		const reg = /(^\-[1-9]([0-9]{0,5})?(\.[0-9]{1,2})?$)|(^\-[0-9]\.[0-9]([0-9])?$)/;
		return reg.test(str)
	}
}

// -999999.99-999999.99
export function abs0to999999dot(str) {
	if (str * 1 === 0) {
		return true;
	} else {
		const reg = /(^-?[1-9]([0-9]{0,5})?(\.[0-9]{1,2})?$)|(^\?-[0-9]\.[0-9]([0-9])?$)/;
		return reg.test(str)
	}
}

// 整数0-9999999
export function integer0to9999999(str) {
	const reg = /^([1-9]\d{0,6}|0)$/
	return reg.test(str)
}

// 整数0-999999
export function integer0to999999(str) {
	const reg = /^(0|[1-9]\d{0,5})$/
	return reg.test(str)
}

// 整数1-999999
export function integer1to999999(str) {
	const reg = /^([1-9]\d{0,5})$/
	return reg.test(str)
}

// 整数1-999999999
export function integer1to999999999(str) {
	const reg = /^([1-9]\d{0,8})$/
	return reg.test(str)
}

// 整数1-999
export function integer1to999(str) {
	const reg = /^([1-9]\d{0,2})$/
	return reg.test(str)
}

// 整数0-100
export function integer0to100(str) {
	const reg = /^(?:100|0?\d{1,2})$/;
	return reg.test(str)
}

// 整数1-99
export function integer1to99(str) {
	const reg = /^([1-9]\d?)$/
	return reg.test(str)
}

// 1-x位的整数
export function integer1tox(str, x) {
	var reg = new RegExp(`^([1-9]\\d{0,${x - 1}})$`);
	return reg.test(str)
}

// 空或数字0-999999
export function nullto999999(str) {
	const reg = /^(?:[0-9]\d{0,5}|0|)$/
	return reg.test(str)
}

// 数字空或1-255
export function nullOr1to255(str) {
	const reg = /^$|^([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))$/
	return reg.test(str)
}

// 金额
export function money(str) {
	const reg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
	return reg.test(str)
}

// x位的金额，包括0
export function money0tox(str, x) {
	var reg = new RegExp(`^([1-9][\\d]{0,${x - 1}}|0)(\\.[\\d]{1,2})?$`);
	return reg.test(str)
}

// 可负数的金额
export function money_minus(str) {
	const reg = /^\-?([1-9][\d]{0,7}|0)(\.[\d]{1,2})?$/;
	return reg.test(str)
}

// 0<x<=10,最多两位小数
export function multiple0to10(str) {
	const reg = /^(?!0(\.0{1,2})?$)(\d(\.\d{1,2})?|10)$/;
	return reg.test(str)
}

// 0<x<=10,最多一位小数
export function multiple0to10Yi(str) {
	const reg = /^(?!0(\.0{1,1})?$)(\d(\.\d{1,1})?|10)$/;
	return reg.test(str)
}

//最多两位小数
export function multiple2decimal(str) {
	const reg = /^$|^(([1-9][0-9]*)|([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2}))$/;
	return reg.test(str)
}

/* 合法uri*/
export function validateURL(textval) {
	const urlregex =
		/^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
	return urlregex.test(textval)
}


//验证IP地址或域名格式
export function serviceAddressVerification(value) {
	let reg = /^((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])|(([a-zA-Z\d][a-zA-Z\d-_]+\.)+[a-zA-Z\d-_][^ ]*))$/;
	return reg.test(value);
}

/* 小写字母*/
export function validateLowerCase(str) {
	const reg = /^[a-z]+$/
	return reg.test(str)
}

/* 小写字母开头，支持小写字母、数字和下划线的组合*/
export function validateStartLowerCaseAndCompose(str) {
	const reg = /^[a-z][a-z0-9_]+$/
	return reg.test(str)
}

/* 大写字母*/
export function validateUpperCase(str) {
	const reg = /^[A-Z]+$/
	return reg.test(str)
}

/* 大小写字母*/
export function validatAlphabets(str) {
	const reg = /^[A-Za-z]+$/
	return reg.test(str)
}

/* 纯数字*/
export function onlyNum(str) {
	const reg = /^\d+$/
	return reg.test(str)
}

/* 正整数*/
export function positiveInt(str) {
	const reg = /^[1-9]\d*$/
	return reg.test(str)
}
/* 数字、-和,*/
export function dotNum(str) {
	const reg = /^[\d,，-]+$/
	return reg.test(str)
}

/* 字母数字下划线和-*/
export function letterNumLine(str) {
	const reg = /^[\w-]+$/
	return reg.test(str)
}

/* 字母数字下划线*/
export function databaseName(str) {
	const reg = /^[\w]+$/
	return reg.test(str)
}

// 字母或数字
export function letterNum(str) {
	const reg = /^[\dA-Za-z]+$/
	return reg.test(str)
}

// vin码
export function isCheckVIN(str) {
	const reg = /^[A-HJ-NPR-Z\d]{17}$/;
	return reg.test(str)
}

// 新能源车牌号
export function isLicenseNo(str) {
	const reg =
		// /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-HJ-NP-Z](?:((\d{5}[A-HJK])|([A-HJK][A-HJ-NP-Z0-9][0-9]{4}))|[A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳])$/;
		/(^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}(([0-9]{5}[DF])|([DF][A-HJ-NP-Z0-9][0-9]{4}))$)/;
	return reg.test(str)
}

// 普通车牌号
export function isOrdinaryLicenseNo(str) {
	const reg = /^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$/;
	return reg.test(str)
}

// 英文字母或数字开头，支持下划线和&的组合
export function letterNumStartAndCompose(str) {
	const reg = /^[\dA-Za-z][\w_&]+$/
	return reg.test(str)
}

export function checkNumber(e) {
	let ev = e || event;
	return (/[\d]/.test(String.fromCharCode(ev.keyCode)))
}

//验证IP地址
export function isValidIP(ip) {
    var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
    return reg.test(ip);
}

//是否是版本号  1.0.0
export function versions(str) {
	// /^(V)(\d+(.\d+){0,10})$/  带有大写V的正则
	const reg = /^(\d+(.\d+){0,10})$/
	return reg.test(str)
}


//匹配url链接
export function linkName(str) {
	const strRegex = '^((https|http|ftp)://)?' //(https或http或ftp):// 可有可无
		+
		'(([\\w_!~*\'()\\.&=+$%-]+: )?[\\w_!~*\'()\\.&=+$%-]+@)?' //ftp的user@ 可有可无
		+
		'(([0-9]{1,3}\\.){3}[0-9]{1,3}' // IP形式的URL- 3位数字.3位数字.3位数字.3位数字
		+
		'|' // 允许IP和DOMAIN（域名）
		+
		'(localhost)|' //匹配localhost
		+
		'([\\w_!~*\'()-]+\\.)*' // 域名- 至少一个[英文或数字_!~*\'()-]加上.
		+
		'\\w+\\.' // 一级域名 -英文或数字 加上.
		+
		'[a-zA-Z]{1,6})' // 顶级域名- 1-6位英文
		+
		'(:[0-9]{1,5})?' // 端口- :80 ,1-5位数字
		+
		'((/?)|' // url无参数结尾 - 斜杆或这没有
		+
		'(/[\\w_!~*\'()\\.;?:@&=+$,%#-]+)+/?)$'; //请求参数结尾- 英文或数字和[]内的各种字符
	const re = new RegExp(strRegex, 'i'); // 大小写不敏感
	return re.test(encodeURI(str));
}


//营业执照代码
export function checkLicense(code) {
	var tip = "OK";
	var pass = true;

	var patrn = /^[0-9A-Z]+$/;
	//18位校验及大写校验
	if (!code || (code.length !== 18) || (patrn.test(code) === false)) {
		// alert("不是有效的统一社会信用编码！");
		return false
	}
	else {
		var Ancode;//统一社会信用代码的每一个值
		var Ancodevalue;//统一社会信用代码每一个值的权重
		var total = 0;
		var weightedfactors = [1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28];//加权因子
		var str = '0123456789ABCDEFGHJKLMNPQRTUWXY';
		//不用I、O、S、V、Z
		for (var i = 0; i < code.length - 1; i++) {
			Ancode = code.substring(i, i + 1);
			Ancodevalue = str.indexOf(Ancode);
			total = total + Ancodevalue * weightedfactors[i];
			//权重与加权因子相乘之和
		}

		// alert(total);

		var logiccheckcode = 31 - total % 31;
		if (logiccheckcode === 31) {
			logiccheckcode = 0;
		}
		var Str = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,J,K,L,M,N,P,Q,R,T,U,W,X,Y";
		var Array_Str = Str.split(',');
		logiccheckcode = Array_Str[logiccheckcode];
		// alert(logiccheckcode);


		var checkcode = code.substring(17, 18);
		if (logiccheckcode !== checkcode) {
			pass = false;
			tip = "不是有效的统一社会信用编码！";
		}
	}
	// return { 'errCode': pass, 'errMsg': tip };

	return  pass
}
