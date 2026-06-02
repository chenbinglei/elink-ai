package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 业务命令响应-启动响应 订阅实体类
 */
@Data
public class PileStartResponseSubscribeVo {

    /**
     * 桩编码
     */
    private String pilesCode;

    /**
     *枪标识
     */
    private Integer gunCode;

    /**
     * 响应结果
     */
    private int responseResult;

    /**
     * 失败原因
     */
    private int faileReason;

    /**
     * 失败详细原因
     */
    private String stopDetail;

    /**
     * 交易流水号
     */
    private String recordId;
}
