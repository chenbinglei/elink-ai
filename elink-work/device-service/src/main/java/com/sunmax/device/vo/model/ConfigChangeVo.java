package com.sunmax.device.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模型扩展属性编辑参数类
 */
@Data
@Schema(description = "参数配置编辑参数类")
public class ConfigChangeVo {

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
     * 字段标识
     */
    @Schema(description = "字段标识")
    private String fieldLogo;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称")
    private String fieldName;

    /**
     * 字段长度
     */
    @Schema(description = "字段长度")
    private Integer fieldLength;

    /**
     * 参数类型 1-系统参数 2-TCU参数 3-PCU参数 4-桩参数 5-枪参数
     */
    @Schema(description = "参数类型 1-系统参数 2-TCU参数 3-PCU参数 4-桩参数 5-枪参数")
    private Integer paramType;

    /**
     * 字段类型 1-数值 2-文字 3-选项
     */
    @Schema(description = "字段类型 1-数值 2-文字 3-选项")
    private Integer fieldType;

    /**
     * 读写类型 1-只读 2-读写
     */
    @Schema(description = "读写类型 1-只读 2-读写")
    private Integer rwType;

    /**
     * 额外值
     */
    @Schema(description = "额外值")
    private String extraValue;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

}
