/* 有关时间的方法 */

/**
 * 获取本周、本季度、本月、上月的开始日期、结束日期
 */
let now = new Date(); //当前日期 
let nowDayOfWeek = now.getDay(); //今天本周的第几天 
let nowDay = now.getDate(); //当前日 
let nowMonth = now.getMonth(); //当前月 
let nowYear = now.getYear(); //当前年 
nowYear += (nowYear < 2000) ? 1900 : 0; //

let lastMonthDate = new Date(); //上月日期
lastMonthDate.setDate(1);
lastMonthDate.setMonth(lastMonthDate.getMonth() - 1);
let lastYear = lastMonthDate.getYear();
let lastMonth = lastMonthDate.getMonth();


//获取本年开始-当前时间
let currentYearEndDate = now; //当前时间
let currentYear = now.getFullYear(); //获得当前年份4位年
let currentYearFirstDate = new Date(currentYear, 0, 1); //本年第一天
let startTime = currentYearFirstDate.getFullYear() + '-' + (currentYearFirstDate.getMonth() + 1) + '-' +
	currentYearFirstDate.getDate() + '' + currentYearFirstDate.getHours() + ':' + currentYearFirstDate.getMinutes() +
	':' + currentYearFirstDate.getSeconds(); //格式化本年第一天日期


//格式化日期：yyyy-MM-dd 
function formatDate(date) {
	let myyear = date.getFullYear();
	let mymonth = date.getMonth() + 1;
	let myweekday = date.getDate();

	if (mymonth < 10) {
		mymonth = "0" + mymonth;
	}
	if (myweekday < 10) {
		myweekday = "0" + myweekday;
	}
	return (myyear + "-" + mymonth + "-" + myweekday);
}

//获得某月的天数 
export function getMonthDays(myMonth) {
	let monthStartDate = new Date(nowYear, myMonth, 1);
	let monthEndDate = new Date(nowYear, myMonth + 1, 1);
	let days = (monthEndDate - monthStartDate) / (1000 * 60 * 60 * 24);
	return days;
}

//获得本季度的开始月份 
export function getQuarterStartMonth() {
	let quarterStartMonth = 0;
	if (nowMonth < 3) {
		quarterStartMonth = 0;
	}
	if (2 < nowMonth && nowMonth < 6) {
		quarterStartMonth = 3;
	}
	if (5 < nowMonth && nowMonth < 9) {
		quarterStartMonth = 6;
	}
	if (nowMonth > 8) {
		quarterStartMonth = 9;
	}
	return quarterStartMonth;
}

//获得本周的开始日期 
export function getWeekStartDate() {
	let weekStartDate = new Date(nowYear, nowMonth, nowDay - nowDayOfWeek + 1);
	return formatDate(weekStartDate);
}

//获得本周的结束日期 
export function getWeekEndDate() {
	let weekEndDate = new Date(nowYear, nowMonth, nowDay + (7 - nowDayOfWeek));
	return formatDate(weekEndDate);
}

//获得本月的开始日期 
export function getMonthStartDate() {
	let monthStartDate = new Date(nowYear, nowMonth, 1);
	return formatDate(monthStartDate);
}

//获得本月的结束日期 
export function getMonthEndDate() {
	let days = getMonthDays(nowMonth); //获取当月总共有多少天
	let monthEndDate = new Date(nowYear, nowMonth, days);
	return formatDate(monthEndDate); //返回当月结束时间
}

//获得上月开始时间
export function getLastMonthStartDate() {
	let lastMonthStartDate = new Date(nowYear, lastMonth, 1);
	return formatDate(lastMonthStartDate);
}

//获得上月结束时间
export function getLastMonthEndDate() {
	let lastMonthEndDate = new Date(nowYear, lastMonth, getMonthDays(lastMonth));
	return formatDate(lastMonthEndDate);
}

//获得本季度的开始日期 
export function getQuarterStartDate() {
	let quarterStartDate = new Date(nowYear, getQuarterStartMonth(), 1);
	return formatDate(quarterStartDate);
}

//或的本季度的结束日期 
export function getQuarterEndDate() {
	let quarterEndMonth = getQuarterStartMonth() + 2;
	let quarterStartDate = new Date(nowYear, quarterEndMonth, getMonthDays(quarterEndMonth));
	return formatDate(quarterStartDate);
}

//获取当前时间 yyy-MM-DD
export function getNowDate(day, type = 0, symbol = "-") {
	let date = new Date(); //1. js获取当前时间
	if (day) date = new Date(day);
	let y = date.getFullYear();
	let m = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
	let d = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
	let formatdate = y + symbol + m + symbol + d;
	if (type) formatdate = y + symbol + m;
	return formatdate;
}

//获取日期 time:距离当前时间的天数  index  1: 年月 0：年月日
export function getDaysFromCurrentTime(time = 0, index = 0) {
	let date = new Date(new Date().getTime() + time * 8.64e7);
	let year = date.getFullYear();
	let month = date.getMonth() + 1;
	let day = date.getDate();
	if (index == 1) {
		return year + "-" + (month > 9 ? month : "0" + month);
	} else {
		return year + "-" + (month > 9 ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day);
	}
}

//获取距离当天前多少天 （不包含当天）   年月日
export function getDistanceNowDate(otherDay = 0, isContainDay = true) {
	let date = new Date(); //1. js获取当前时间

	if (!isContainDay) {
		otherDay += 1;
		date.setTime(endTime.getTime() - 3600 * 1000 * 24);
	}

	date.setTime(date.getTime() - 3600 * 1000 * 24 * otherDay);
	return timestampToTime(date.getTime());
}

//默认一个月  年月日 时分秒
export function pickerDateOneMonthDay(time = 30) {
	let monthsTime = time * 24 * 3600 * 1000; //一个月的毫秒
	let stratTime = new Date(+new Date() + 8 * 3600 * 1000 - monthsTime).toJSON().substr(0, 19).replace("T", " ");
	let endTime = new Date(+new Date() + 8 * 3600 * 1000).toJSON().substr(0, 19).replace("T", " ")
	return [stratTime, endTime]
}

//获取当前时间前后分钟 0-60   -60 - 0
export function getNowDateMin(minute = 0) {
	let date = new Date(); //1. js获取当前时间
	let min = date.getMinutes(); //2. 获取当前分钟
	date.setMinutes(min + minute); //3. 设置当前时间+10分钟：把当前分钟数+10后的值重新设置为date对象的分钟数
	let y = date.getFullYear();
	let m = (date.getMonth() + 1) < 10 ? ("0" + (date.getMonth() + 1)) : (date.getMonth() + 1);
	let d = date.getDate() < 10 ? ("0" + date.getDate()) : date.getDate();
	let h = date.getHours() < 10 ? ('0' + date.getHours()) : date.getHours();
	let f = date.getMinutes() < 10 ? ('0' + date.getMinutes()) : date.getMinutes();
	let s = date.getSeconds() < 10 ? ('0' + date.getSeconds()) : date.getSeconds();
	let formatdate = y + '-' + m + '-' + d + " " + h + ":" + f + ":" + s;

	return formatdate
}

//比较两个时间的大小 第一个时间 第二个时间  returnType 返回的数据类型  1： 返回时间   2返回 true false
export function compareTime(oneTime, twoTime, returnType = 1) {
	let oDate1 = new Date(oneTime);
	let oDate2 = new Date(twoTime);
	if (oDate1 > oDate2) {
		if (returnType == 1) {
			return {
				startTime: twoTime,
				endTime: oneTime
			}
		} else {
			return false
		}
	} else {
		if (returnType == 1) {
			return {
				startTime: oneTime,
				endTime: twoTime
			}
		} else {
			return true
		}
	}
}
// 计算近一年的周期范围
export function getOneYearRange() {
  const now = new Date();
  const oneYearAgo = new Date(now);
  oneYearAgo.setFullYear(now.getFullYear() - 1);

  // 格式化为 YYYY-MM-DD
  function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  return [formatDate(oneYearAgo), formatDate(now)];
}



//获取距离当天前多少天 （不包含当天）   年月日
export function pickerDateToYear(otherDay = 0, isContainDay = false) {
	const stratTime = new Date();
	const endTime = new Date();

	if (!isContainDay) {
		otherDay += 1;
		endTime.setTime(endTime.getTime() - 3600 * 1000 * 24);
	}

	stratTime.setTime(stratTime.getTime() - 3600 * 1000 * 24 * otherDay);
	return [timestampToTime(stratTime.getTime()), timestampToTime(endTime.getTime())];
}

// 时间戳转 年月日 或者 年月日时分秒
export function timestampToTime(timestamp, hms = false) {
	let date = new Date(timestamp); //时间戳为10位需*1000，时间戳为13位的话不需乘1000
	let Y = date.getFullYear() + '-';
	let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
	let D = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
	let h = ' ' + date.getHours() + ':';
	let m = date.getMinutes() + ':';
	let s = date.getSeconds();
	return hms ? Y + M + D + h + m + s : Y + M + D;
}

//获取当前时间 yyy/MM/DD h:m:s
export function getNowDateAll(day,type) {
    let date = new Date();
    if (day) date = new Date(day); //1. js获取当前时间
    let y = date.getFullYear();
    let m = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
    let d = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
    let hh = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ':';
    let mm = (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) + ':';
    let ss = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
    let formatdate = "";
    formatdate = y + "-" + m + "-" + d + " " + hh + mm + ss;
    if (type)formatdate = y + "-" + m + "-" + d + " " + hh + mm + "00";
    return formatdate;
}


//获取当天时间起始时间或结束时间 yyy/MM/DD h:m:s
export function getNowDateStartOrEnd(day,text = "start") {
    let date = new Date();
    if (day) date = new Date(day); //1. js获取当前时间
    let y = date.getFullYear();
    let m = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
    let d = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
    let hh = text === "start" ? "00" + ':' : "23" + ':';
    let mm = text === "start" ? "00" + ':' : "59" + ':';
    let ss = text === "start" ? "00" : "59";
    return y + "-" + m + "-" + d + " " + hh + mm + ss;
}


// 获取某一月的第一天
export function getCurrentMonthFirstDay(data){
    const date = new Date(data);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从0开始计算，需要加1
    const lastDay = "01";

    return getNowDateAll(year + '-' + month + '-' + lastDay + " 00:00:00")
}

// 获取某一月的最后一天
export function getCurrentMonthLastDay(data){
    const date = new Date(data);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从0开始计算，需要加1
    const lastDay = new Date(year, month, 0).getDate();

    return getNowDateAll(year + '-' + month + '-' + lastDay + " 23:59:59")
}