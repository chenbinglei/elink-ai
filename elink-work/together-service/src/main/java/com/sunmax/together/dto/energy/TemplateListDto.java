package com.sunmax.together.dto.energy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模板列表返回实体类
 */
@Data
@Schema(description = "TemplateListDto")
public class TemplateListDto {

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
     * 策略说明名称
     */
    @Schema(description = "策略说明名称")
    private String explainName;

    /**
     * 配置文件名称
     */
    @Schema(description = "配置文件名称")
    private String configName;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称")
    private String createName;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称")
    private String updateName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
