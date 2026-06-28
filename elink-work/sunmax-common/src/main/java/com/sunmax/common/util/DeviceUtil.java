package com.sunmax.common.util;

public class DeviceUtil {


    /**
     * 数据类型转字段类型
     * @param dataType 数据类型
     * @return 字段类型
     */
    public static String dataTypeConvertFieldType(Integer dataType) {
        String result = null;
        //数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
        switch (dataType) {
            case 1:
            case 2:
            case 5:
                result = "INT";
                break;
            case 3:
                result = "FLOAT";
                break;
            case 4:
                result = "DOUBLE";
                break;
            case 6:
                result = "BOOL";
                break;
            case 7:
            case 8:
                result = "VARCHAR(2048)";
                break;
            case 9:
                result = "TIMESTAMP";
                break;
        }
        return result;
    }

    @SuppressWarnings("removal")
    public static Double dataTypeConvert(Integer dataType, Object data) {
        if (StringUtil.isEmpty(dataType) || StringUtil.isEmpty(data)) {
            return null;
        }
        String dataValue = String.valueOf(data);
        //数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
        switch (dataType) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                return new Double(dataValue);
            case 6:
                return (double) (Boolean.parseBoolean(dataValue) ? 0 : 1);
        }
        return null;
    }

}
