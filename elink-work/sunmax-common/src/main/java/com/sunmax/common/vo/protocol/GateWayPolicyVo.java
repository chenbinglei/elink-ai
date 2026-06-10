package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "GateWayPolicy")
public class GateWayPolicyVo {

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceCode;

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
    private PolicyPeriod policyPeriod;

    /**
     * 命令类型 “get”查询参数，“set”设置参数
     */
    @Schema(description = "命令类型 “get”查询参数,“set”设置参数")
    private String cmdType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "PolicyPeriod")
    public static class PolicyPeriod {

        /**
         * 控制开关 true-启动 false-关闭
         */
        @Schema(description = "控制开关 true-启动 false-关闭")
        private Boolean controlSwitch;

        /**
         * 执行类型 1-全时段 2-工作日 3-周末 4-自定义时段
         */
        @Schema(description = "执行类型 1-全时段 2-工作日 3-周末 4-自定义时段")
        private Integer executeType;

        /**
         * 执行时段 自定义时段{1:["00:00:00","01:00:00"],2:["00:00:00"]}
         */
        @Schema(description = "执行时段 自定义时段{1:['00:00:00','01:00:00'],2:['00:00:00']}")
        private String executeTime;

        /**
         * 策略计划类型 1-全部启动 2-全部停止
         */
        @Schema(description = "策略计划类型 1-全部启动 2-全部停止")
        private Integer planType;

        /**
         * 过滤日期
         */
        @Schema(description = "过滤日期")
        private String filterDates;

    }

}
