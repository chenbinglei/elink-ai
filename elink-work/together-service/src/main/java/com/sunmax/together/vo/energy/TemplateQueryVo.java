package com.sunmax.together.vo.energy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模板查询列表实体类
 */
@Data
@Schema(description = "TemplateQueryVo")
public class TemplateQueryVo {

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
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;


}
