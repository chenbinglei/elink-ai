import { calcNumberFun, isNumber } from "@/utils";

export default {  // 有关数字 过滤处理文件
    // formatNumber (num) {
    //     if (num === null || num === undefined) return '';
    //     const str = num.toFixed(2); // 先保留两位小数
    //     const parts = str.split('.');
    //     if (parts[1] === '00') {
    //         return parts[0]; // 没有小数
    //     } else if (parts[1].length === 1) {
    //         return `${parts[0]}.${parts[1]}`; // 一位小数
    //     } else {
    //         return str; // 两位小数
    //     }
    // },
    formatNumber (num) {
        if (num === null || num === undefined) return '';
        const str = num.toString();
        const toStr= num.toFixed(2);
        const parts = str.split('.');
        if (parts.length === 1) {
            return parts[0]; // 没有小数
        } else {
            const decimalPart = parts[1];
            if (decimalPart === '0') {
                return parts[0]; // 一位小数且为 0
            } else if (decimalPart.length === 1) {
                return `${parts[0]}.${decimalPart}`; // 一位小数
            } else {
                return toStr; // 多位小数（如 3108.35）
            }
        }
    },
    /*
    * 金额处理 数值处理
    * num  当前数值
    * toNA： 空值转换为
    * isRetainNum：  保留的位数
    * isZeroFill:  小数点位数不够是否补0
    * */

    moneyTwoNum (num, isRetainNum = 4, config = { toNA: '--', isZeroFill: true }) {
        if (num === null || num === "" || num === undefined) return config.toNA ?? '--';

        let money = num ? num : 0;
        let result = parseFloat(money);
        if (isNaN(result)) return config.toNA ?? '--';

        let retainNum = "1";
        while (retainNum.length <= isRetainNum) retainNum += "0";
        result = Math.floor(calcNumberFun(money, retainNum, '*')) / retainNum;
        let s_x = result.toString();

        // 位数不够进行补零
        if (config.isZeroFill) {
            let pos_decimal = s_x.indexOf(".");
            if (pos_decimal < 0) {
                pos_decimal = s_x.length;
                s_x += ".";
            }
            while (s_x.length <= pos_decimal + isRetainNum) s_x += "0";
        }

        return s_x;
    },
    // 最多两位小数
    reserveTwoNum (num, isRetainNum = 3, toNA = '--') {
        return this.moneyTwoNum(num, isRetainNum, { toNA: toNA, isZeroFill: false });
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
    // 数字取整 舍弃小数部分
    roundedNumber (num) {
        if (num === null || num === "" || num === undefined) return '--';
        return Math.floor(num);
    },
    //手机好中间四位变****
    phoneFourRep (phone) {
        if (phone) {
            let mobile = String(phone);
            return mobile.substr(0, 3) + "****" + mobile.substr(7);
        } else {
            return '--';
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
    numberUnit (value, unit = '') {
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
    // 千克转换 吨数值
    kgNumConvert (value) {
        let new_value = value * 1;
        if (isNumber(new_value)) {
            const unitValue = Number(value);
            if (unitValue >= 1000) {
                return this.moneyTwoNum(calcNumberFun(unitValue, 1000, '/'), 2, { toNA: '--', isZeroFill: false });
            } else {
                return this.moneyTwoNum(unitValue, 2, { toNA: '--', isZeroFill: false });
            }
        } else {
            return '--';
        }
    },
    // 千克 转换 吨单位
    kgUnitConvert (value) {
        let new_value = value * 1;
        if (isNumber(new_value)) {
            const unitValue = Number(value);
            if (unitValue >= 1000) {
                return "吨";
            } else {
                return "千克";
            }
        } else {
            return "千克";
        }
    },
    /**
     * 文件大小单位转换
     * a 参数：表示要被转化的容量大小，以字节为单
     * b 参数：表示如果转换时出小数，四舍五入保留多少位 默认为2位小数
     */
    formatBytes (a, b) {
        if (0 === a) return "0 B";
        let c = 1024, d = b || 2, e = ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
            f = Math.floor(Math.log(a) / Math.log(c));
        return parseFloat((a / Math.pow(c, f)).toFixed(d)) + " " + e[f];
    },
};
