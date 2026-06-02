package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模型扩展属性编辑参数类
 */
@Data
@ApiModel(value = "ConfigChangeVo", description = "参数配置编辑参数类")
public class ConfigChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id", required = true)
    private String typeId;

    /**
     * 字段标识
     */
    @ApiModelProperty(value = "字段标识", required = true)
    private String fieldLogo;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称", required = true)
    private String fieldName;

    /**
     * 字段长度
     */
    @ApiModelProperty(value = "字段长度", required = true)
    private Integer fieldLength;

    /**
     * 参数类型 1-系统参数 2-TCU参数 3-PCU参数 4-桩参数 5-枪参数
     */
    @ApiModelProperty(value = "参数类型 1-系统参数 2-TCU参数 3-PCU参数 4-桩参数 5-枪参数", required = true)
    private Integer paramType;

    /**
     * 字段类型 1-数值 2-文字 3-选项
     */
    @ApiModelProperty(value = "字段类型 1-数值 2-文字 3-选项", required = true)
    private Integer fieldType;

    /**
     * 读写类型 1-只读 2-读写
     */
    @ApiModelProperty(value = "读写类型 1-只读 2-读写", required = true)
    private Integer rwType;

    /**
     * 额外值
     */
    @ApiModelProperty(value = "额外值")
    private String extraValue;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

}
