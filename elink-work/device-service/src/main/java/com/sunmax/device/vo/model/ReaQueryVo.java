package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模型扩展属性查询类
 */
@Data
@ApiModel(value = "ReaQueryVo", description = "模型扩展属性查询类")
public class ReaQueryVo {

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id", required = true)
    private String typeId;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;

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
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

}
