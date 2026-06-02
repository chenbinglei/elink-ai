package com.sunmax.crontab.util;

import com.sunmax.common.util.StringUtil;

public class CrontabCommonUtil {

    public static String dataTypeConvertStr(Integer dataType) {
        if (StringUtil.isEmpty(dataType)) {
            return null;
        }
        //数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
        switch (dataType) {
            case 1:
            case 2:
                return "Integer";
            case 4:
                return "Double";
            case 6:
                return "Boolean";
            case 3:
            case 5:
            case 7:
                return "String";
            case 8:
                return "ArrayString";
        }
        return null;
    }
}
