package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CustomChangeVo", description = "系统自定义编辑参数实体类")
public class CustomChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 平台名称
     */
    @ApiModelProperty(value = "平台名称", required = true)
    private String platformName;

    /**
     * 大屏标题
     */
    @ApiModelProperty(value = "大屏标题", required = true)
    private String largeTitle;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

}
