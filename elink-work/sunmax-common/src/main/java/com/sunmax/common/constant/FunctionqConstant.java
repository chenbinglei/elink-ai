package com.sunmax.common.constant;

/**
 * 计算节点函数常量
 */
public class FunctionqConstant {

    /**
     * 求和
     */
    public final static String SUM = "SUM";

    /**
     * 最大值
     */
    public final static String MAX = "MAX";

    /**
     * 最小值
     */
    public final static String MIN = "MIN";

    /**
     * 平均值
     */
    public final static String AVG = "AVG";

    /**
     * 求X的最新值与周期内第⼀个值的差值
     */
    public final static String DIF = "DIF";

    /**
     * 求X的最新值与周期内第⼀个值的差值
     * 注：若第⼀个值为空，在周期内向后寻找临近点位的数据代替
     */
    public final static String DIFF = "DIFF";

    /**
     * 求X的平⽅根
     * 注：X的取值为正数，X为负数时返回空值
     */
    public final static String SQRT = "SQRT";

    /**
     * 求X的平⽅
     */
    public final static String SQUARE = "SQUARE";

    /**
     * 取相反值，返回－X
     */
    public final static String REVERSE = "REVERSE";

    /**
     * 求X的Y次幂
     * 注：
     * 1、当X为负数时，Y必须为整数，因为底数为负时，不能进⾏开⽅运算，返回值为空值。
     * 2、X,Y可以为数值，也可以为变量。
     */
    public final static String POW = "POW";

    /**
     * 取的X的绝对值
     * 注：
     * 1、正数的绝对值是它本身；
     * 2、负数的绝对值是它的相反数；
     * 3、0的绝对值还是0；
     */
    public final static String ABS = "ABS";

    /**
     * 取X的整数部分
     */
    public final static String INTPART = "INTPART";
}
