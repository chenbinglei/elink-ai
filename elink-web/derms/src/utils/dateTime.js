// 有关时间的方法
import moment from "moment";
// 是否今天
export function isToday (param) {
    let currentStamp = new Date().setHours(0, 0, 0, 0);		// 当天日期，转换为时间部分为0的时间戳
    let paramStamp = new Date(param).setHours(0, 0, 0, 0);	// 传入时间戳，将时间部分转换为0
    // 若两个时间戳相等，说明传入的时间戳即今天
    return currentStamp === paramStamp;
}

// 是否今年今月
export function isMonth (param) {
    let nowDate = new Date();
    let date = new Date(param);
    return ((nowDate.getFullYear() === date.getFullYear()) && (nowDate.getMonth() === date.getMonth()));
}

// 是否今年
export function isYear (param) {
    let nowDate = new Date();
    let date = new Date(param);
    return (nowDate.getFullYear() === date.getFullYear());
}

//获取当前时间前后分钟 0-60   -60 - 0
export function getNowDateMin (minute = 0, isfAnds = true) {
    let date = new Date(); //1. js获取当前时间
    let min = date.getMinutes(); //2. 获取当前分钟
    date.setMinutes(min + minute); //3. 设置当前时间+10分钟：把当前分钟数+10后的值重新设置为date对象的分钟数
    let y = date.getFullYear();
    let m = (date.getMonth() + 1) < 10 ? ("0" + (date.getMonth() + 1)) : (date.getMonth() + 1);
    let d = date.getDate() < 10 ? ("0" + date.getDate()) : date.getDate();
    let h = date.getHours() < 10 ? ('0' + date.getHours()) : date.getHours();
    let f = date.getMinutes() < 10 ? ('0' + date.getMinutes()) : date.getMinutes();
    let s = date.getSeconds() < 10 ? ('0' + date.getSeconds()) : date.getSeconds();

    let timeStr = y + '-' + m + '-' + d + " " + h;
    if (isfAnds) timeStr = timeStr + ":" + f + ":" + s;

    return timeStr;
}

// 返回 一个数字 区间数组
export function makeRange (start, end = 24) {
    const result = [];
    for (let i = start; i <= end; i++) {
        result.push(i);
    }
    return result;
}

// 获取当前时间的时间戳
export function getActiveTimeStampFun () {
    return new Date().getTime();
}

//获取当前时间 yyy-MM-DD
export function getNowDate (day, type) {
    let date = new Date();
    if (day) date = new Date(day); //1. js获取当前时间
    let y = date.getFullYear();
    let m = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
    let d = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
    let formatDate = y + "-" + m + "-" + d;
    if (type) formatDate = y + "-" + m;
    return formatDate;
}
//获取当前时间 yyy-MM
export function getMonth () {
    const date = new Date();
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    return `${year}-${month}`;
}
//获取当前时间 yyy
export function getYear () {
    const date = new Date();
    const year = date.getFullYear();
    return `${year}`;
}

// 获取当前时间的 前一天  yyy-MM-DD
export function getTheDayBeforeDate (day) {
    let date = day ? new Date(day) : new Date();
    let before_date = new Date(date - 86400000);
    return getNowDate(before_date);
}

//获取当前时间 yyy/MM/DD h:m:s
export function getNowDateAll (day = null, obj = { isSs: true }) {
    let date = new Date();
    if (day) date = new Date(day); //1. js获取当前时间
    let y = date.getFullYear();
    let m = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
    let d = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
    let hh = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ':';
    let mm = (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) + ':';
    let ss = "00";
    if (obj.isSs) ss = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
    return y + "-" + m + "-" + d + " " + hh + mm + ss;
}

//获取日期 time:距离当前时间的天数  index  2: 年  1: 年月  0：年月日
export function getDaysFromCurrentTime (time = 0, index = 0, day = null) {
    let date = new Date(new Date().getTime() + time * 8.64e7);
    if (day) date = new Date(new Date(day).getTime() + time * 8.64e7);

    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let d = date.getDate();

    if (index === 2) return String(year);
    if (index === 1) return year + "-" + (month > 9 ? month : "0" + month);
    return year + "-" + (month > 9 ? month : "0" + month) + "-" + (d > 9 ? d : "0" + d);
}

//比较两个时间的大小 第一个时间 第二个时间  returnType 返回的数据类型  1： 返回时间   2返回 true false
export function compareTimer (oneTime, twoTime) {
    let oDate1 = new Date(oneTime);
    let oDate2 = new Date(twoTime);
    let startTime = oneTime, endTime = twoTime;

    if (oDate1 > oDate2) {
        startTime = twoTime;
        endTime = oneTime;
    }

    return { startTime: startTime, endTime: endTime };
}

// 获取某年的最后一天
export function getCurrentYearLastDay (data = "") {
    let date = new Date();
    if (data) date = new Date(data);
    const year = date.getFullYear();
    const month = new Date(year, 12, 0).getMonth() + 1; // 月份从0开始计算，需要加1
    const lastDay = new Date(year, month, 0).getDate();

    return getNowDate(year + '-' + month + '-' + lastDay);
}

// 获取某年的第一天
export function getCurrentYearFirstDay (data = "") {
    let date = new Date();
    if (data) date = new Date(data);
    const year = date.getFullYear();
    return getNowDate(year + '-' + "01" + '-' + "01");
}

// 获取某一月的第一天
export function getCurrentMonthFirstDay (data = "") {
    let date = new Date();
    if (data) date = new Date(data);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从0开始计算，需要加1
    const firstDay = "01";
    return getNowDate(year + '-' + month + '-' + firstDay);
}

// 获取某一月的最后一天
export function getCurrentMonthLastDay (data = "") {
    let date = new Date();
    if (data) date = new Date(data);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从0开始计算，需要加1
    const lastDay = new Date(year, month, 0).getDate();

    return getNowDate(year + '-' + month + '-' + lastDay);
}


// 获取上一月的第一天
export function getLastMonthFirstDay () {
    let today = new Date();
    let lastMonthDate = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    return getCurrentMonthFirstDay(lastMonthDate);
}

// 获取上一月的最后一天
export function getLastMonthLastDay () {
    let today = new Date();
    let lastMonthDate = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    return getCurrentMonthLastDay(lastMonthDate);
}

// 获取某一周的最后一天
export function getCurrentWeekLastDay (data, setHMSzero = true) {
    let date = data ? new Date(data) : new Date();
    if (setHMSzero) {
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
    }
    let weekFirstDay = new Date(date - (date.getDay() - 1) * 86400000);
    let weekLastDay = new Date((weekFirstDay / 1000 + 6 * 86400) * 1000);
    return getNowDateAll(weekLastDay);
}

// 获取上一周的最后一天
export function getUpperWeekLastDay (data, setHMSzero = true) {
    let date = data ? new Date(data) : new Date();
    if (setHMSzero) {
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
    }
    let weekFirstDay = new Date(date - (date.getDay()) * 86400000);
    return getNowDateAll(weekFirstDay);
}

// 根据日期获取当前是星期几
export function queryDateWeekdayFun (date) {
    const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    const weekday = new Date(date).getDay();
    return weekdays[weekday];
}

// 获取当前月的天数
export function getDaysInMonth () {
    let date = new Date();
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从0开始计算，需要加1
    const nextMonthFirstDay = new Date(year, month, 1);
    // 将下个月的第一天设置为前一天，即得到当前月的最后一天
    nextMonthFirstDay.setDate(0);
    // 返回当前月最后一天的日期部分
    return nextMonthFirstDay.getDate();
}

// 转换时间格式 间隔符替换  splitTally： 1：-  2：/  3文字
export function setTimerSplitTallyFun (day = "", splitTally = 1, timeFormat = "yyyy-MM-dd") {
    let date = new Date();
    if (day) date = new Date(day); //1. js获取当前时间
    let y = date.getFullYear();
    let m = (date.getMonth() + 1) < 10 ? ("0" + (date.getMonth() + 1)) : (date.getMonth() + 1);
    let d = date.getDate() < 10 ? ("0" + date.getDate()) : date.getDate();
    let h = date.getHours() < 10 ? ('0' + date.getHours()) : date.getHours();
    let f = date.getMinutes() < 10 ? ('0' + date.getMinutes()) : date.getMinutes();
    let s = date.getSeconds() < 10 ? ('0' + date.getSeconds()) : date.getSeconds();

    if (timeFormat === "yyyy-MM-dd") {
        if (splitTally === 1) return y + "-" + m + "-" + d;
        if (splitTally === 2) return y + "/" + m + "/" + d;
        if (splitTally === 3) return y + "年" + m + "月" + d + "日 ";
    }

    if (timeFormat === "yyyy-MM-dd HH:mm") {
        if (splitTally === 1) return y + "-" + m + "-" + d + " " + h + ':' + f;
        if (splitTally === 2) return y + "/" + m + "/" + d + " " + h + ':' + f;
        if (splitTally === 3) return y + "年" + m + "月" + d + "日 " + h + '时' + f + '分';
    }

    if (splitTally === 1) return y + "-" + m + "-" + d + " " + h + ':' + f + ':' + s;
    if (splitTally === 2) return y + "/" + m + "/" + d + " " + h + ':' + f + ':' + s;
    if (splitTally === 3) return y + "年" + m + "月" + d + "日 " + h + '时' + f + '分' + s + '秒';
}

// 开始时间6个月前,结束时间默认当天 时间格式 2020-12-09
export function pickerDateSixMonth (start = 0, end = 0) {
    const startSix = new Date();
    startSix.setTime(startSix.getTime() - 3600 * 1000 * 24 * 30 * 6);
    return [new Date(Date.UTC(startSix.getFullYear(), startSix.getMonth(), startSix.getDate()) + start).toISOString()
        .slice(0, 10), new Date(Date.UTC(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()) +
            end).toISOString().slice(0, 10)
    ];
}

// 开始时间6个月前,结束时间默认当天 时间格式 2020-12-09
export function pickerDateSixMonth2 (start = 0, end = 0) {
    const startSix = new Date();
    startSix.setTime(startSix.getTime() - 3600 * 1000 * 24 * 30 * 6);
    return [
        new Date(Date.UTC(startSix.getFullYear(), startSix.getMonth(), startSix.getDate()) + start).toISOString().slice(0, 10),
        new Date(Date.UTC(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()) + end).toISOString().slice(0, 10),
    ];
}

//时间选择不能小于当前时间 几分钟之后
export function pickerOptionsMinutesTimer (minutesNum = 0) {
    let selectTimer = "";
    return {
        // 年月日限制
        disabledDate (time, selectTime) {
            selectTimer = selectTime;
            return time.getTime() >= new Date().getTime() + minutesNum * 60000;
        },
        // 小时限制
        disabledHours (type) {
            if (isToday(selectTimer)) {
                // console.log("为今天");
                let hour = new Date(getNowDateMin(minutesNum)).getHours();
                return makeRange(hour + 1, 24);
            }
        },
        disabledMinutes (selectedHour) {
            if (isToday(selectTimer)) {
                let date = new Date(getNowDateMin(minutesNum));
                let hour = date.getHours();
                if (hour === selectedHour) {
                    let minutes = date.getMinutes();
                    return makeRange(minutes + 1, 60);
                }
            }
        },
        disabledSeconds (selectedHour, selectedMinute) {
            if (isToday(selectTimer)) {
                let date = new Date(getNowDateMin(minutesNum));
                let hour = date.getHours();
                let minutes = date.getMinutes();
                if (hour === selectedHour && minutes === selectedMinute) {
                    let seconds = date.getSeconds();
                    return makeRange(seconds + 1, 60);
                }
            }
        }
    };
}

//时间选择不能小于当前时间 几分钟之后
export function pickerOptionsDHSMTimer (minutesNum = 0) {
    let selectTimer = "";
    return {
        // 年月日限制
        disabledDate (time, selectTime) {
            selectTimer = selectTime;
            return time.getTime() < new Date().getTime() - 8.64e7 + minutesNum * 60000;
        },
        // 小时限制
        disabledHours () {
            if (isToday(selectTimer)) {
                // console.log("为今天");
                let hour = new Date(getNowDateMin(minutesNum)).getHours();
                return makeRange(0, hour - 1);
            }
        },
        disabledMinutes (selectedHour) {
            if (isToday(selectTimer)) {
                let date = new Date(getNowDateMin(minutesNum));
                let hour = date.getHours();
                if (hour === selectedHour) {
                    let minutes = date.getMinutes();
                    return makeRange(0, minutes + 1);
                }
            }
        },
        disabledSeconds (selectedHour, selectedMinute) {
            if (isToday(selectTimer)) {
                let date = new Date(getNowDateMin(minutesNum));
                let hour = date.getHours();
                let minutes = date.getMinutes();
                if (hour === selectedHour && minutes === selectedMinute) {
                    let seconds = date.getSeconds();
                    return makeRange(0, seconds + 1);
                }
            }
            return [];
        },
        shortcuts: [
            {
                text: "最近30分钟",
                value: () => {
                    const end = getNowDateMin(0);
                    const start = getNowDateMin(-30);
                    return [start, end];
                }
            },
            {
                text: "最近1小时",
                value: () => {
                    const end = getNowDateMin(0);
                    const start = getNowDateMin(-60);
                    return [start, end];
                }
            },
            {
                text: "最近24小时",
                value: () => {
                    const end = getNowDateMin(0);
                    const start = getNowDateMin(-1440);
                    return [start, end];
                }
            }
        ]
    };
}

//时间选择不能大于otherDay 默认当天，最多6个月
export function pickerOptionsSixMonth (otherDay = 0) {
    return {
        disabledDate (time) {
            return time.getTime() > Date.now() + 8.64e7 * otherDay;
        },
        shortcuts: [
            {
                text: "最近一周",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 6);
                    return [start, end];
                },
            },
            {
                text: "最近一个月",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                    return [start, end];
                },
            },
            {
                text: "最近六个月",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 30 * 6);
                    return [start, end];
                },
            },
        ],
    };
}

//时间选择不能大于otherDay默认当天，最多365天
export function pickerMax365 (otherDay = 0, maxSelect = 365) {
    let startDate = null, endDate = null;
    return {
        visibleChange () {
            if (endDate) {
                endDate = '';
                startDate = '';
            }
        },
        calendarChange (dates) {
            // 记录选择的开始日期，方便后面根据开始日期限定结束日期
            let hasSelectDate = dates !== null && dates.length > 0;
            startDate = hasSelectDate ? dates[0] : null;
            endDate = hasSelectDate ? dates[1] : null;
        },
        disabledDate (time) {
            if (startDate !== null) {
                return (
                    time.getTime() >= Date.now() + otherDay * 8.64e7 ||
                    time.getTime() > startDate.getTime() + maxSelect * 8.64e7 ||
                    time.getTime() < startDate.getTime() - maxSelect * 8.64e7
                );
            }
            // 默认只能选择今天以及今天之前的日期
            return time.getTime() >= Date.now() + otherDay * 8.64e7;
        },
        shortcuts: [
            {
                text: "最近一周",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 8.64e7 * 6);
                    return [start, end];
                },
            },
            {
                text: "最近一个月",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 8.64e7 * 29);
                    return [start, end];
                },
            },
            {
                text: "最近六个月",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 8.64e7 * 30 * 6);
                    return [start, end];
                },
            },
            {
                text: "最近一年",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 8.64e7 * 365);
                    return [start, end];
                },
            },
        ],
    };
}

//默认一个月  年月日 时分秒
// export function pickerDateOneMonthDay (time = 30) {
//     let monthsTime = time * 24 * 3600 * 1000; //一个月的毫秒
//     let endTime = new Date(+new Date() + 8 * 3600 * 1000).toJSON().substr(0, 19).replace("T", " ");
//     let startTime = new Date(+new Date() + 8 * 3600 * 1000 - monthsTime).toJSON().substr(0, 19).replace("T", " ");
//     return [startTime, endTime];
// }
export function pickerDateOneMonthDay(time = 30) {
    // 获取当前时间（UTC+8）
    const now = new Date();
    const offset = 8 * 60 * 60 * 1000; // UTC+8 偏移量
    const utcNow = new Date(now.getTime() + offset);

    // 计算一个月前的日期（不考虑具体时间）
    const monthAgo = new Date(utcNow);
    monthAgo.setDate(utcNow.getDate() - time); // 减去 time 天

    // 设置为当天的 00:00:00
    monthAgo.setHours(0, 0, 0, 0);

    // 当前时间保持原样（带时间）
    const endTime = new Date(utcNow).toJSON().substr(0, 19).replace("T", " ");

    // 格式化 start date 为 "YYYY-MM-DD HH:mm:ss"
    const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day} 00:00:00`;
    };

    return [formatDate(monthAgo), endTime];
}

//默认一个月  年月日
export function pickerDateOneMonthDayNOMin (time = 30) {
    const end = new Date();
    const start = new Date();
    end.setTime(Date.now());
    start.setTime(start.getTime() - 3600 * 1000 * 24 * time);
    return [moment(start).format('YYYY-MM-DD'), moment(end).format('YYYY-MM-DD')];
}

// 时间不能大于当前 otherDay: 天
export function pickerOptionsGthanAcTime (otherDay = 0) {
    let startDate = '', endDate = '';
    return {
        pickerDateChange (value) {
            if ([undefined, null].includes(value)) {
                endDate = '';
                startDate = '';
            }
        },
        calendarChange (dates) {
            // console.log(dates);
            // isDisabledDate = true;
            // 记录选择的开始日期，方便后面根据开始日期限定结束日期
            let hasSelectDate = dates && dates.length;
            startDate = hasSelectDate ? dates[0] : null;
            endDate = hasSelectDate ? dates[1] : null;
        },
        // 年月日限制
        disabledDate (time, date) {
            let newDate = date ? new Date(date) : new Date();
            return time.getTime() > newDate.getTime() - 8.64e7 * otherDay;
        },
        disabledDateToday (time) {
            const today = new Date();
            // 禁用未来日期（包括今天之后）
            return time.getTime() > today.getTime();
        },
        // 小时限制
        disabledHours (type) {
            let timer = startDate;
            if (type === "end") timer = endDate;
            if (isToday(timer)) {
                // console.log("为今天");
                let hour = new Date().getHours();
                return makeRange(hour + 1);
            }
        },
        // 分钟限制
        disabledMinutes (selectedHour, type) {
            // console.log(type)
            // console.log(selectedHour)
            let timer = startDate;
            if (type === "end") timer = endDate;
            if (isToday(timer)) {
                let date = new Date();
                let hour = date.getHours();
                if (hour === selectedHour) {
                    let minutes = date.getMinutes();
                    return makeRange(minutes + 1, 60);
                }
            }
        },
        // 秒限制
        disabledSeconds (selectedHour, selectedMinute, type) {
            let timer = startDate ? startDate : startTime;
            if (type === "end") timer = endDate ? endDate : endTime;
            if (isToday(timer)) {
                let date = new Date();
                let hour = date.getHours();
                let minutes = date.getMinutes();
                if (hour === selectedHour && minutes === selectedMinute) {
                    let seconds = date.getSeconds();
                    return makeRange(seconds + 1, 60);
                }
            }
        },
        shortcuts: [
            {
                text: "最近一周",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 6);
                    return [start, end];
                }
            },
            {
                text: "最近一个月",
                value: () => {
                    const end = new Date();
                    const start = new Date();
                    end.setTime(Date.now() + 8.64e7 * otherDay);
                    start.setTime(start.getTime() - 3600 * 1000 * 24 * 29);
                    return [start, end];
                }
            },
        ]
    };
}

// 选择时间不能大于当前时间 (最大为 24小时)
export function pickerOptionsMinutesSecondsTimer (startTime, endTime) {
    let startDate = '', endDate = '';
    // console.log(startTime);
    return {
        pickerDateChange (value) {
            if ([undefined, null].includes(value)) {
                endDate = '';
                startDate = '';
            }
        },
        calendarChange (dates) {
            // console.log(dates);
            // isDisabledDate = true;
            // 记录选择的开始日期，方便后面根据开始日期限定结束日期
            let hasSelectDate = dates && dates.length;
            startDate = hasSelectDate ? dates[0] : null;
            endDate = hasSelectDate ? dates[1] : null;
        },
        // 年月日限制
        disabledDate (time) {
            // if (startDate) {
            //     return (
            //         time.getTime() < startDate.getTime() - 8.64e7 ||
            //         time.getTime() > startDate.getTime() + 8.64e7 ||
            //         time.getTime() > new Date().getTime()
            //     )
            // } else {
            //     return time.getTime() > new Date().getTime();
            // }
            return time.getTime() > new Date().getTime();
        },
        // 小时限制
        disabledHours (type) {
            let timer = startDate ? startDate : startTime;
            if (type === "end") timer = endDate ? endDate : endTime;
            if (isToday(timer)) {
                // console.log("为今天");
                let hour = new Date().getHours();
                return makeRange(hour + 1);
            }
        },
        // 分钟限制
        disabledMinutes (selectedHour, type) {
            // console.log(type)
            // console.log(selectedHour)
            let timer = startDate ? startDate : startTime;
            if (type === "end") timer = endDate ? endDate : endTime;
            if (isToday(timer)) {
                let date = new Date();
                let hour = date.getHours();
                if (hour === selectedHour) {
                    let minutes = date.getMinutes();
                    return makeRange(minutes + 1, 60);
                }
            }
        },
        // 秒限制
        disabledSeconds (selectedHour, selectedMinute, type) {
            let timer = startDate ? startDate : startTime;
            if (type === "end") timer = endDate ? endDate : endTime;
            if (isToday(timer)) {
                let date = new Date();
                let hour = date.getHours();
                let minutes = date.getMinutes();
                if (hour === selectedHour && minutes === selectedMinute) {
                    let seconds = date.getSeconds();
                    return makeRange(seconds + 1, 60);
                }
            }
        },
        shortcuts: [{
            text: "最近5分钟",
            value: () => {
                const end = getNowDateMin(0);
                const start = getNowDateMin(-5);
                return [start, end];
            }
        },
        {
            text: "最近10分钟",
            value: () => {
                const end = getNowDateMin(0);
                const start = getNowDateMin(-10);
                return [start, end];
            }
        },
        {
            text: "最近15分钟",
            value: () => {
                const end = getNowDateMin(0);
                const start = getNowDateMin(-15);
                return [start, end];
            }
        },
        {
            text: "最近30分钟",
            value: () => {
                const end = getNowDateMin(0);
                const start = getNowDateMin(-30);
                return [start, end];
            }
        },
        {
            text: "最近1小时",
            value: () => {
                const end = getNowDateMin(0);
                const start = getNowDateMin(-60);
                return [start, end];
            }
        },
        {
            text: "最近12小时",
            value: () => {
                const end = getNowDateMin(0);
                const start = getNowDateMin(-720);
                return [start, end];
            }
        },
        {
            text: "最近24小时",
            value: () => {
                const end = getNowDateMin(0);
                const start = getNowDateMin(-1440);
                return [start, end];
            }
        }
        ]
    };
}

// 仅限制时分秒的选择  几分钟之后
export function pickerOptionsHoursAndMinutesAndSeconds (minutesNum = 0) {
    return {
        disabledHours (timeType = 1) {
            if (timeType === 1) {
                // console.log("为今天");
                let hour = new Date(getNowDateMin(minutesNum)).getHours();
                return makeRange(0, hour - 1);
            }
        },
        disabledMinutes (selectedHour, timeType = 1) {
            if (timeType === 1) {
                let date = new Date(getNowDateMin(minutesNum));
                let hour = date.getHours();
                if (hour === selectedHour) {
                    let minutes = date.getMinutes();
                    // console.log(makeRange(minutes + 1, 60))
                    return makeRange(0, minutes - 1);
                }
            }
        },
        disabledSeconds (selectedHour, selectedMinute, timeType = 1) {
            if (timeType === 1) {
                let date = new Date(getNowDateMin(minutesNum));
                let hour = date.getHours();
                let minutes = date.getMinutes();
                if (hour === selectedHour && minutes === selectedMinute) {
                    let seconds = date.getSeconds();
                    return makeRange(0, seconds - 1);
                }
            }
        }
    };
}

// 计算两个时间相差多少天
export function getDaysBetweenDates (date1, date2) {
    // 将日期字符串转换为Date对象
    let d1 = new Date(date1);
    let d2 = new Date(date2);
    // 计算两个日期的时间差（毫秒）
    let timeDiff = Math.abs(d2.getTime() - d1.getTime());
    // 计算天数
    return Math.ceil(timeDiff / (1000 * 3600 * 24));
}

export function formatDateTime (date, format) {
    const map = {
        'YYYY': date.getFullYear(),


        'MM': (date.getMonth() + 1).toString().padStart(2, '0'),


        'DD': date.getDate().toString().padStart(2, '0'),


        'HH': date.getHours().toString().padStart(2, '0'),


        'mm': date.getMinutes().toString().padStart(2, '0'),


        'ss': date.getSeconds().toString().padStart(2, '0'),


    };


    return format.replace(/YYYY|MM|DD|HH|mm|ss/gi, matched => map[matched]);
}
