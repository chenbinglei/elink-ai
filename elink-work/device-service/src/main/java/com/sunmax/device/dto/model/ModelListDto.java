package com.sunmax.device.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型列表返回实体类")
public class ModelListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @Schema(description = "设备类型名称")
    private String typeName;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

    /**
     * 设备数量
     */
    @Schema(description = "设备数量")
    private Long deviceNum;

    /**
     * 模型状态 0-开发中 1-已发布
     */
    @Schema(description = "模型状态 0-开发中 1-已发布")
    private Integer modelStatus;

    /**
     * logo路径
     */
    @Schema(description = "logo路径")
    private String logoPath;

}
