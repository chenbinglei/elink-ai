package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 业务命令响应-停止响应 订阅实体类
 */
@Data
public class PileStopResponseSubscribeVo {

    /**
     * 桩编码
     */
    private String pilesCode;

    /**
     * 枪口标识
     */
    private Integer gunCode;

    /**
     * 响应结果 (失败原因 0 成功；255 其他错误)
     */
    private int responseResult;

    /**
     * 失败详细原因
     */
    private int faileReason;

    /**
     * 交易流水号
     */
    private String recordId;

}
