package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModelQueryVo", description = "模型查询参数")
public class ModelQueryVo {

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id")
    private String typeId;

    /**
     * 关键字(模型ID+模型名称)
     */
    @ApiModelProperty(value = "关键字(模型ID+模型名称)")
    private String keyword;

    /**
     * 模型状态 0-开发中 1-已发布
     */
    @ApiModelProperty(value = "模型状态 0-开发中 1-已发布")
    private Integer modelStatus;

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
