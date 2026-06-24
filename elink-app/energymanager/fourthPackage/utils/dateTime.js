export function getCurrentMonthFirstDay (data = "") {
    let date = new Date();
    if (data) date = new Date(data);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 月份从0开始计算，需要加1
    const firstDay = "01";
    return getNowDate(year + '-' + month + '-' + firstDay);
}
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
// 获取某月 1 