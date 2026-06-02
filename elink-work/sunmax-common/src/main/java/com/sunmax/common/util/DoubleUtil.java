package com.sunmax.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * double类
 */
public class DoubleUtil {

    public static boolean isEmpty(Double value) {
        return value == null || 0.0 == value;
    }

    public static boolean isNotEmpty(Double value) {
        return !isEmpty(value);
    }

    /**
     * 两个值相减
     */
    public static Double getObjSub(Object value1, Object value2) {
        if (StringUtil.isEmpty(value1) || StringUtil.isEmpty(value2)) {
            return 0.0;
        }
        double result = DoubleUtil.objToDouble(value2) - DoubleUtil.objToDouble(value1);
        return result >= 0 ? result : 0.0;
    }


    public static Double objToDouble(Object value) {
        return StringUtil.isNotEmpty(value) ? Double.parseDouble(String.valueOf(value)) : 0.0;
    }

    public static Double objToDouble(Object value, Double defaultValue) {
        return StringUtil.isNotEmpty(value) ? Double.parseDouble(String.valueOf(value)) : defaultValue;
    }

    public static Double objToDouble(Object value, int length) {
        if (StringUtil.isEmpty(value)) {
            return 0.0;
        }
        Double doubleValue = objToDouble(value, null);
        return doubleValue != null ? Double.parseDouble(String.format("%.0" + length + "f", doubleValue)) : 0.0;
    }

    public static Double objToDouble(Object value, int length, Double defaultValue) {
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }
        Double doubleValue = objToDouble(value, defaultValue);
        return doubleValue != null ? Double.parseDouble(String.format("%.0" + length + "f", doubleValue)) : defaultValue;
    }

    /**
     * 保留两位小时
     *
     * @param value 值
     * @return
     */
    public static Double getToDouble(Double value) {
        return StringUtil.isNotEmpty(value) ? Double.parseDouble(String.format("%.2f", value)) : 0.0;
    }

    public static Double getToDouble(Double value, int length) {
        return StringUtil.isNotEmpty(value) ? Double.parseDouble(String.format("%.0" + length + "f", value)) : 0.0;
    }

    public static Double getToDoubleIsNull(Double value) {
        return StringUtil.isNotEmpty(value) ? Double.parseDouble(String.format("%.2f", value)) : null;
    }

    public static Double getFillEmpty0(Double value) {
        return StringUtil.isNotEmpty(value) ? value : 0.0;
    }

    /**
     * 获取当前参数的绝对值
     *
     * @param value
     * @return
     */
    public static Double getAbsDouble(Double value) {
        return Math.abs(getToDouble(value));
    }

    public static Double getAbsDouble(Double value, int length) {
        return Math.abs(getToDouble(value, length));
    }

    public static Double getToFloat(Float value) {
        return Double.parseDouble(String.format("%.2f", value));
    }

    public static Double getToFloat(Float value, int length) {
        return Double.parseDouble(String.format("%." + length + "f", value));
    }

    public static Double getAbsFloat(Float value) {
        return Math.abs(getToFloat(value));
    }

    public static String getToValue(long value) {
        return value > 0 ? "0" + String.format("%04d", value) : "1" + String.format("%04d", -value);
    }

    public static BigDecimal getToBigDecimal(BigDecimal value) {
        return StringUtil.isNotEmpty(value) ? new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP) : new BigDecimal("0.0");
    }

    public static BigDecimal getToBigDecimal(BigDecimal value, int scale) {
        return StringUtil.isNotEmpty(value) ? new BigDecimal(value.toString()).setScale(scale, RoundingMode.HALF_UP) : new BigDecimal("0.0");
    }

    public static BigDecimal getAbsBigDecimal(BigDecimal value) {
        return BigDecimal.valueOf(Math.abs(getToDouble(StringUtil.isNotEmpty(value) ? value.doubleValue() : 0.0)));
    }

    public static BigDecimal getAbsBigDecimal(BigDecimal value, int scale) {
        return BigDecimal.valueOf(Math.abs(getToDouble(StringUtil.isNotEmpty(value) ? value.doubleValue() : 0.0, scale)));
    }

    public static BigDecimal getBigDecimal(BigDecimal value) {
        return BigDecimal.valueOf(getToDouble(StringUtil.isNotEmpty(value) ? value.doubleValue() : 0.0));
    }
}
