package com.sunmax.log.util;

public class StringUtils {

    public static boolean isEmpty(Object str) {
        return str == null || "null".equals(str) || "".equals(str) || "[]".equals(str) || "{}".equals(str) || "undefined".equals(str);
    }

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

}
