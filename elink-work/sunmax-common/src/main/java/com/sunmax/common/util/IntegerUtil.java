package com.sunmax.common.util;

public class IntegerUtil {

    public static boolean isEmpty( Integer value) {
        return value == null || 0 == value;
    }

    public static boolean isNotEmpty(Integer value) {
        return !isEmpty(value);
    }

    /**
     * 是否为偶数
     * @param value 值
     * @return true为偶数 false为奇数
     */
    public static boolean isEven(Integer value) {
        return (value & 1) == 0;
    }

    /**
     * 获取bit位值
     */
    public static int getBit(int num, int bitIndex) {
        return (num >> bitIndex) & 1;
    }

    public static void main(String[] args) {
        System.out.println(getBit(15,2));
    }

}
