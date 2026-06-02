package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 业务命令响应-费率下发响应 订阅实体类
 */
@Data
public class PileRateSetResponseSubscribeVo {

    /**
     * 桩编码
     */
    private String pilesCode;

    /**
     *费率类型
     */
    private Integer type;

    /**
     * 费率id。
     */
    private String rateId;

}
