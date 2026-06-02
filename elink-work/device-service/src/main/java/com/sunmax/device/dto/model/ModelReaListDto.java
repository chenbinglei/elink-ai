package com.sunmax.device.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModelReaListDto", description = "模型关联扩展属性返回实体类")
public class ModelReaListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 模型扩展属性id
     */
    @ApiModelProperty(value = "模型扩展属性id")
    private String modelReaId;

    /**
     * 扩展属性名称
     */
    @ApiModelProperty(value = "扩展属性名称")
    private String reaName;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    /**
     * 扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间
     */
    @ApiModelProperty(value = "扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间")
    private Integer reaType;

//    /**
//     * 读写类型 1-只读 2-读写
//     */
//    @ApiModelProperty(value = "读写类型 1-只读 2-读写")
//    private Integer readWriteType;

    /**
     * 是否必填 true-是 false-否
     */
    @ApiModelProperty(value = "是否必填 true-是 false-否")
    private Boolean required;

    /**
     * 默认值
     */
    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 额外值
     */
    @ApiModelProperty(value = "额外值")
    private String extraValue;

}
