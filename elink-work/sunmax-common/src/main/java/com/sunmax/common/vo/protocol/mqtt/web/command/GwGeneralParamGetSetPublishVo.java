package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 网关通用参数下发
 */
@Data
@Schema(description = "GwGeneralParamGetSetPublishVo")
public class GwGeneralParamGetSetPublishVo {

    /**
     * 策略id
     */
    @Schema(description = "策略id")
    private Long policyId;

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
     * 命令类型 “get”查询参数，“set”设置参数
     */
    @Schema(description = "命令类型 “get”查询参数,“set”设置参数")
    private String cmdType;

}
