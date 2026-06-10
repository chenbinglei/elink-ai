package com.sunmax.common.vo.protocol.mqtt.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "GwGeneralParamResponseSubscribeVo")
public class GwGeneralParamResponseSubscribeVo {

    /**
     * 策略id
     */
    @Schema(description = "策略id")
    private Integer policyId;

    /**
     * 策略名称
     */
    @Schema(description = "策略名称")
    private String name;

    /**
     * 策略配置参数 JSONObject类型
     */
    @Schema(description = "策略配置参数 JSONObject类型")
    private Object policyCfg;

    /**
     * 策略执行周期
     */
    @Schema(description = "策略执行周期")
    private Object policyPeriod;

    /**
     * 命令类型 response-正常 response_invaild_policy-策略参数无效
     */
    @Schema(description = "命令类型 response-正常 response_invaild_policy-策略参数无效")
    private String cmdType;

}
