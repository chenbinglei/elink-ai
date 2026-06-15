package com.sunmax.together.dto.energy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 策略列表返回实体类
 */
@Data
@Schema(description = "策略列表返回实体类")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class StrategyListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 策略名称
     */
    @Schema(description = "策略名称")
    private String strategyName;

    /**
     * 策略模板id
     */
    @Schema(description = "策略模板id")
    private String templateId;

    /**
     * 策略模板名称
     */
    @Schema(description = "策略模板名称")
    private String templateName;

    /**
     * 策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略
     */
    @Schema(description = "策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略")
    private Integer strategyType;

    /**
     * 执行状态 0-未下发 1-已下发
     */
    @Schema(description = "执行状态 0-未下发 1-已下发")
    private Integer executeStatus;

}
