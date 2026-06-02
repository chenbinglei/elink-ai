package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网关策略响应实体类
 */
@Data
@ApiModel("GateWayPolicyDto")
public class GateWayPolicyDto {

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String deviceCode;

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

    /**
     * 下发状态 -1-执行超时 0-执行成功 1-执行失败 9-无效策略 255-其他原因
     */
    @ApiModelProperty("下发状态 -1-执行超时 0-执行成功 1-执行失败 9-无效策略 255-其他原因")
    private Integer issuedStatus;

}
