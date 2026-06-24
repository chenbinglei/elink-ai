// 用户名
export function isvalidUsername(str) {
	const reg = /^[a-zA-Z0-9]{8,40}$/
	return reg.test(str)
}

//汉字 数字 字母 下划线（不包含特殊字符）
export function notCharmap(str) {
	const reg = /^[\u4e00-\u9fa5\w]+$/
	return reg.test(str)
}

//名称（包含常用字符）
export function commonCharName(str) {
	const reg = /^[|'"\.\[/,\]【】+*%~()#@!！?？\u4e00-\u9fa5\w-]{2,20}$/
	return reg.test(str)
}

// 手机号
export function mobile(str) {
	const reg = /^1[3-9]\d{9}$/;
	return reg.test(str)
}

// 密码
export function isvalidPassword(str) {
	const reg =
		/^(?![0-9]+$)(?![A-Z]+$)(?![a-z]+$)(?![`~!@#$%^&.*"“”_+<>{}\/'[\]]+$)[0-9|A-Z|a-z|`~!@#$%^&.*_"“”+<>{}\/'[\]]{8,40}$/;
	return reg.test(str)
}

// 新能源车牌号
export function isLicenseNo(str) {
	const reg =
		/(^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}(([0-9]{5}[DF])|([DF][A-HJ-NP-Z0-9][0-9]{4}))$)/;
	return reg.test(str)
}
// 普通车牌号
export function isOrdinaryLicenseNo(str) {
	const reg = /^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$/;
	return reg.test(str)
}


// 邮箱
export function isvalidEmail(str) {
	const reg = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
	return reg.test(str)
}

// vin码
export function isCheckVIN(str) {
	const reg = /^[A-HJ-NPR-Z\d]{17}$/;
	return reg.test(str)
}

// 数字不能1-255
export function num1to255(str) {
	const reg = /^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))))$/
	return reg.test(str)
}

// 数字0-999
export function num0to999(str) {
	const reg = /^\+?[0-9]\d{0,2}(\.\d*)?$/
	return reg.test(str)
}

// 数字0-9999
export function num0to9999(str) {
	const reg = /^\+?[0-9]\d{0,3}(\.\d*)?$/
	return reg.test(str)
}

// 数字1-9999
export function num1to9999(str) {
	const reg = /^\+?[1-9]\d{0,3}(\.\d*)?$/
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
// 空或数字0-999999
export function nullto999999(str) {
	const reg = /^(?:[0-9]\d{0,5}|0|)$/
	return reg.test(str)
}

// 整数0-9999999
export function integer0to9999999(str) {
	const reg = /^([1-9]\d{0,6}|0)$/
	return reg.test(str)
}

// 整数1-999
export function integer1to999(str) {
	const reg = /^[1-9]\d{0,2}$/
	return reg.test(str)
}

// 整数1-9999999
export function integer1to9999999(str) {
	const reg = /^[1-9]\d{0,6}$/
	return reg.test(str)
}

// 正整数
export function pInteger(str) {
	const reg = /^([1-9]\d*)$/
	return reg.test(str)
}

// 正整数和0
export function pInteger0(str) {
	const reg = /^0|[1-9]\d*$/
	return reg.test(str)
}

// 整数0-999999
export function integer0to999999(str) {
	const reg = /^(0|[1-9]\d{0,5})$/
	return reg.test(str)
}

// 金额
export function money(str) {
	const reg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
	return reg.test(str)
}

// 0<x<=10,最多两位小数
export function multiple0to10(str) {
	const reg = /^(?!0(\.0{1,2})?$)(\d(\.\d{1,2})?|10)$/;
	return reg.test(str)
}

/* 合法uri*/
export function validateURL(textval) {
	const urlregex =
		/^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
	return urlregex.test(textval)
}

/* 小写字母*/
export function validateLowerCase(str) {
	const reg = /^[a-z]+$/
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
