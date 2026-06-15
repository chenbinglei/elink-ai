package com.sunmax.together.vo.energy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 策略模板编辑实体类
 */
@Data
@Schema(description = "TemplateChangeVo")
public class TemplateChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称")
    private String templateName;

    /**
     * 策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略
     */
    @Schema(description = "策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略")
    private Integer strategyType;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
