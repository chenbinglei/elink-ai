// 有关数字 过滤处理文件
export default {
  // 金额处理，isRetainNum: 默认保留两位小数,toNA 转变空值为NA
  moenyTwoNum(num,isRetainNum = 2,toNA) {
    if(num === null || num === "" || num === undefined)return toNA ? "NA" : "--"
    let money = num ? num : 0;
    var result = parseFloat(money);
    if (isNaN(result)) {
      return "";
    }
    result = Math.round(money * 100) / 100;
    var s_x = result.toString();
    var pos_decimal = s_x.indexOf(".");
    if (pos_decimal < 0) {
      pos_decimal = s_x.length;
      s_x += ".";
    }

    while (s_x.length <= pos_decimal + isRetainNum) {
      s_x += "0";
    }

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
}
