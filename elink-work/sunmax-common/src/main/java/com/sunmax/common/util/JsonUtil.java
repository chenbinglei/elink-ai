package com.sunmax.common.util;

import com.alibaba.fastjson2.JSON;

import java.util.List;

public class JsonUtil {

    public static <T> T objectToEntity(Object text, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(text), clazz);
    }

    public static <T> List<T> objectToList(Object text, Class<T> clazz) {
        return JSON.parseArray(JSON.toJSONString(text), clazz);
    }

}
