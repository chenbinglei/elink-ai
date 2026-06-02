package com.sunmax.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CustomChangeDto", description = "系统自定义返回实体类")
public class CustomChangeDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 平台名称
     */
    @ApiModelProperty(value = "平台名称")
    private String platformName;

    /**
     * 平台LOGO
     */
    @ApiModelProperty(value = "平台LOGO")
    private String platformLogo;

    /**
     * 大屏标题
     */
    @ApiModelProperty(value = "大屏标题")
    private String largeTitle;

}
