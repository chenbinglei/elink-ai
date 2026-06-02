package com.sunmax.crontab.util;

import com.google.common.collect.Sets;
import com.sunmax.crontab.vo.mqtt.NodeVarParamVo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class ConstantUtil {

    public static Set<String> getConstantValues(Class<?> clazz) {
        Set<String> resultList = Sets.newHashSet();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 检查是否为public static final
            if (java.lang.reflect.Modifier.isPublic(field.getModifiers())
                    && java.lang.reflect.Modifier.isStatic(field.getModifiers())
                    && java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                try {
                    // 获取字段的值
                    Object value = field.get(null); // 静态字段使用null作为对象参数
                    if (value != null) {
                        resultList.add((String) value);
                    }
                } catch (IllegalAccessException e) {
                    log.error("获取常量出错", e);
                }
            }
        }
        return resultList;
    }

    public static void main(String[] args) {
        System.out.println(getConstantValues(NodeVarParamVo.class));
    }

}
