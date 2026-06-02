package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关策略参数下发及响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("GateWayPolicy")
public class GateWayPolicyVo {

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String deviceCode;

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
    private PolicyPeriod policyPeriod;

    /**
     * 命令类型 “get”查询参数，“set”设置参数
     */
    @ApiModelProperty("命令类型 “get”查询参数,“set”设置参数")
    private String cmdType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel("PolicyPeriod")
    public static class PolicyPeriod {

        /**
         * 控制开关 true-启动 false-关闭
         */
        @ApiModelProperty("控制开关 true-启动 false-关闭")
        private Boolean controlSwitch;

        /**
         * 执行类型 1-全时段 2-工作日 3-周末 4-自定义时段
         */
        @ApiModelProperty("执行类型 1-全时段 2-工作日 3-周末 4-自定义时段")
        private Integer executeType;

        /**
         * 执行时段 自定义时段{1:["00:00:00","01:00:00"],2:["00:00:00"]}
         */
        @ApiModelProperty("执行时段 自定义时段{1:['00:00:00','01:00:00'],2:['00:00:00']}")
        private String executeTime;

        /**
         * 策略计划类型 1-全部启动 2-全部停止
         */
        @ApiModelProperty("策略计划类型 1-全部启动 2-全部停止")
        private Integer planType;

        /**
         * 过滤日期
         */
        @ApiModelProperty("过滤日期")
        private String filterDates;

    }

}
