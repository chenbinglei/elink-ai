package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网关通用参数下发
 */
@Data
@ApiModel("GwGeneralParamGetSetPublishVo")
public class GwGeneralParamGetSetPublishVo {

    /**
     * 策略id
     */
    @ApiModelProperty("策略id")
    private Long policyId;

    /**
     * 策略名称
     */
    @ApiModelProperty("策略名称")
    private String name;

    /**
     * 策略配置参数 JSONObject类型
     */
    @ApiModelProperty("策略配置参数 JSONObject类型")
    private Object policyCfg;

    /**
     * 策略执行周期
     */
    @ApiModelProperty("策略执行周期")
    private Object policyPeriod;

    /**
     * 命令类型 “get”查询参数，“set”设置参数
     */
    @ApiModelProperty("命令类型 “get”查询参数,“set”设置参数")
    private String cmdType;

}
