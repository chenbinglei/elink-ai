package com.sunmax.common.util;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class JsonUtil extends JSONObject {

    public static <T> T objectToEntity(Object text, Class<T> clazz) {
        return parseObject(toJSONString(text), clazz);
    }

    public static <T> List<T> objectToList(Object text, Class<T> clazz) {
        return parseArray(toJSONString(text), clazz);
    }

}
