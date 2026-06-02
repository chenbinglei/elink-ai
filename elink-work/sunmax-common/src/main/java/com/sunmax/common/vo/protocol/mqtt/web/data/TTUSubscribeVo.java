package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

import java.io.Serializable;

/**
 * 配变终端数据实体类
 */
@Data
public class TTUSubscribeVo implements Serializable {

    /**
     * 遥测
     */
    public TTUYcVo ttuYc;

    /**
     * 遥信
     */
    public TTUYxVo ttuYx;

    @Data
    public static class TTUYcVo implements Serializable {

        /**
         * A相电压
         */
        private Double PhV_phsA;

        /**
         * B相电压
         */
        private Double PhV_phsB;

        /**
         * C相电压
         */
        private Double PhV_phsC;

        /**
         * 总有功功率
         */
        private Double PhW_P;

        /**
         * A相有功功率
         */
        private Double PhW_phsA;

        /**
         * B相有功功率
         */
        private Double PhW_phsB;

        /**
         * C相有功功率
         */
        private Double PhW_phsC;

        /**
         * A相电流
         */
        private Double A_phsA;

        /**
         * B相电流
         */
        private Double A_phsB;

        /**
         * C相电流
         */
        private Double A_phsC;

        /**
         * 频率
         */
        private Double Hz;

        /**
         * 无功功率
         */
        private Double PhVAr_Q;
        private Double PhVAr_phsA;//A相无功功率 单位kVar
        private Double PhVAr_phsB;//B相无功功率 单位kVar
        private Double PhVAr_phsC;//C相无功功率 单位kVar
        private Double PhVA_S;//相视在功功率 单位kVA
        private Double PhVA_phsA;//A相视在功功率 单位kVA
        private Double PhVA_phsB;//B相视在功功率 单位kVA
        private Double PhVA_phsC;//C相视在功功率 单位kVA
        private Double Cos;//功率因数
        private Double SupWh;//正向有功电能
        private Double SupWh1;//当前正向有功电能尖示值
        private Double SupWh2;//当前正向有功电能峰示值
        private Double SupWh3;//当前正向有功电能平示值
        private Double SupWh4;//当前正向有功电能谷示值
        private Double RevWh;//当前反向有功电能示值

    }

    @Data
    public static class TTUYxVo implements Serializable {

        /**
         * 开入量1
         */
        private Integer Ind1;

        /**
         * 开入量2
         */
        private Integer Ind2;

        /**
         * 开入量3
         */
        private Integer Ind3;

        /**
         * 开入量4
         */
        private Integer Ind4;

    }

}
