package com.sunmax.common.vo.protocol.mqtt.web.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("GwGeneralParamResponseSubscribeVo")
public class GwGeneralParamResponseSubscribeVo {

    /**
     * 策略id
     */
    @ApiModelProperty("策略id")
    private Integer policyId;

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
     * 命令类型 response-正常 response_invaild_policy-策略参数无效
     */
    @ApiModelProperty("命令类型 response-正常 response_invaild_policy-策略参数无效")
    private String cmdType;

}
