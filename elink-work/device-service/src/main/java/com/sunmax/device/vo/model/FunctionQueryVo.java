package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FunctionQueryVo", description = "模型标准功能查询参数")
public class FunctionQueryVo {

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id", required = true)
    private String typeId;

    /**
     * 关键字(功能名称+标识符)
     */
    @ApiModelProperty(value = "关键字(功能名称+标识符)")
    private String keyword;

    /**
     * 功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
     */
    @ApiModelProperty(value = "功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调")
    private Integer functionType;

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
