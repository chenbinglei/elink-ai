package com.sunmax.device.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型名称返回实体类")
public class ModelNameDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

}
