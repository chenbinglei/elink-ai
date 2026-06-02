package com.sunmax.common.dto.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemVarDataDto", description = "系统变量数据返回实体类")
public class SystemVarDataDto {

    /**
     * 变量编码
     */
    @ApiModelProperty(value = "变量编码")
    private String varCode;

    /**
     * 变量名称
     */
    @ApiModelProperty(value = "变量名称")
    private String varName;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 变量数据
     */
    @ApiModelProperty(value = "变量数据")
    private Object value;
}
