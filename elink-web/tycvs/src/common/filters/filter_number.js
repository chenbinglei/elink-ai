import {calcNumberFun} from "@/utils";

export default {  // 有关数字 过滤处理文件
  /*
  * 金额处理 数值处理
  * num  当前数值
  * toNA： 空值转换为
  * isRetainNum：  保留的位数
  * isZeroFill:  小数点位数不够是否补0
  * */
  moneyTwoNum(num, isRetainNum = 4, toNA = "--", isZeroFill = true) {
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
  /**
   * 文件大小单位转换
   * a 参数：表示要被转化的容量大小，以字节为单
   * b 参数：表示如果转换时出小数，四舍五入保留多少位 默认为2位小数
   */
  formatBytes(a, b) {
    if (0 === a) return "0 B";
    let c = 1024, d = b || 2, e = ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
        f = Math.floor(Math.log(a) / Math.log(c));
    return parseFloat((a / Math.pow(c, f)).toFixed(d)) + " " + e[f];
  },
}
