package com.sunmax.device.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModelListDto", description = "模型列表返回实体类")
public class ModelListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @ApiModelProperty(value = "设备类型名称")
    private String typeName;

    /**
     * 模型名称
     */
    @ApiModelProperty(value = "模型名称")
    private String modelName;

    /**
     * 设备数量
     */
    @ApiModelProperty(value = "设备数量")
    private Long deviceNum;

    /**
     * 模型状态 0-开发中 1-已发布
     */
    @ApiModelProperty(value = "模型状态 0-开发中 1-已发布")
    private Integer modelStatus;

    /**
     * logo路径
     */
    @ApiModelProperty(value = "logo路径")
    private String logoPath;

}
