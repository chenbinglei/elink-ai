package com.sunmax.together.vo.operation.operationAnalysis;

import lombok.Data;

@Data
public class OperationTypeVo {

    /**
     * 订单总金额
     */
    public static final String SUM_COST = "sumCost";

    /**
     * 充电电费
     */
    public static final String CHARGE_FEE = "chargeFee";

    /**
     * 充电服务费
     */
    public static final String CHARGE_SERVICE_FEE = "chargeServiceFee";

    /**
     * 实付总金额
     */
    public static final String ACTUAL_TOTAL_COST = "actualTotalCost";

    /**
     * 实付电费
     */
    public static final String ACTUAL_TOTAL_ELECT = "actualTotalElect";

    /**
     * 实付服务费
     */
    public static final String ACTUAL_TOTAL_FEE = "actualTotalFee";

    /**
     * 充电电量
     */
    public static final String CHARGE_QT = "chargeQt";

    /**
     * 直流充电量
     */
    public static final String DC_CHARGE_QT = "dcChargeQt";

    /**
     * 交流充电量
     */
    public static final String AC_CHARGE_QT = "acChargeQt";

    /**
     * 尖时充电量
     */
    public static final String SHARP_QT = "sharpQt";

    /**
     * 峰时充电量
     */
    public static final String PEAK_QT = "peakQt";

    /**
     * 平时充电量
     */
    public static final String FLAT_QT = "flatQt";

    /**
     * 谷时充电量
     */
    public static final String VALLEY_QT = "valleyQt";

    /**
     * 深谷时充电量
     */
    public static final String DEEP_VALLEY_QT = "deepValleyQt";

    /**
     * 订单数量
     */
    public static final String ORDER_NUM = "orderNum";

    /**
     * 异常订单数量
     */
    public static final String AB_ORDER_NUM = "abOrderNum";

    /**
     * V2G放电订单金额
     */
    public static final String DISCHARGE_SUM_COST = "dischargeSumCost";

    /**
     * V2G放电电量
     */
    public static final String DISCHARGE_QT = "dischargeQt";

    /**
     * 枪均充电量
     */
    public static final String GUN_AV_CHARGE_QT = "gunAvChargeQt";

    /**
     * 直流枪均充电量
     */
    public static final String DC_GUN_AV_CHARGE_QT = "dcGunAvChargeQt";

    /**
     * 交流枪均充电量
     */
    public static final String AC_GUN_AV_CHARGE_QT = "acGunAvChargeQt";

    /**
     * 时间利用率
     */
    public static final String TIME_RATIO = "timeRatio";

    /**
     * 充电时长
     */
    public static final String CHARGE_DURATION = "chargeDuration";

    /**
     * 直流充电时长
     */
    public static final String DC_CHARGE_DURATION = "dcChargeDuration";

    /**
     * 交流充电时长
     */
    public static final String AC_CHARGE_DURATION = "acChargeDuration";

    /**
     * 度均服务费
     */
    public static final String AVG_CHARGE_FEE = "avgChargeFee";

    /**
     * 功率利用率
     */
    public static final String POWER_RATIO = "powerRatio";

    /**
     * 一次充电成功率
     */
    public static final String CHARGE_SUCCESS_RATIO = "chargeSuccessRatio";

}
