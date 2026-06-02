package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

import java.util.List;

/**
 * 电桩离线订单
 */
@Data
public class PileOffLineRecordSubscribeVo {
    /**
     * 离网时间戳
     */
    private Long offLineTime;
    /**

    /**
     * 离线充电记录数组(4011)
     */
    private List<PileRecordSubscribeVo> offLineChargeRecord;

    /**
     * 电桩编号
     */
    private List<OrderRecord> orderRecord;


    @Data
    public static class OrderRecord {

        /**
         * 订单编号
         */
        private String orderId;
        /**
         * 订单类型
         */
        private Integer orderType;

        /**
         * 电桩编码
         */
        private String pilesCode;
        /**
         * 电桩编码
         */
        private Integer gunCode;
        /**
         * 创建时间
         */
        private Long createTime;
        /**
         * 订单状态
         */
        private Integer orderStatus;
        /**
         * 用户类型
         */
        private Integer accountType;
        /**
         * 用户数据
         */
        private String accountData;
    }

}
