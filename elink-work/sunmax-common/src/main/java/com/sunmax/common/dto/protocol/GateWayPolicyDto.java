package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 网关策略响应实体类
 */
@Data
@Schema(description = "GateWayPolicyDto")
public class GateWayPolicyDto {

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceCode;

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

    /**
     * 下发状态 -1-执行超时 0-执行成功 1-执行失败 9-无效策略 255-其他原因
     */
    @Schema(description = "下发状态 -1-执行超时 0-执行成功 1-执行失败 9-无效策略 255-其他原因")
    private Integer issuedStatus;

}
