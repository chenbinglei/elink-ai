package com.sunmax.common.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CalculateUtil {

    private static final ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
     * java判断计算 (> < >= <= = != && ||)
     * @param formula 公式
     * @return 值
     */
    public static Object eval(String formula) {
        try {
            return jse.eval(formula);
        } catch (ScriptException e) {
            return null;
        }
    }

}
