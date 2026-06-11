package com.sunmax.device.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模型扩展属性编辑参数类
 */
@Data
@Schema(description = "模型扩展属性编辑参数类")
public class ReaChangeVo {

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
     * 扩展属性名称
     */
    @Schema(description = "扩展属性名称")
    private String reaName;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称")
    private String fieldName;

    /**
     * 扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
     */
    @Schema(description = "扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本")
    private Integer reaType;

//    /**
//     * 读写类型 1-只读 2-读写
//     */
//    @Schema(description = "读写类型 1-只读 2-读写")
//    private Integer readWriteType;

    /**
     * 是否必填 true-是 false-否
     */
    @Schema(description = "是否必填 true-是 false-否")
    private Boolean required;

//    /**
//     * 默认值
//     */
//    @Schema(description = "默认值")
//    private String defaultValue;

    /**
     * 单位
     */
    @Schema(description = "单位")
    private String unit;

    /**
     * 额外值
     */
    @Schema(description = "额外值")
    private String extraValue;

}
