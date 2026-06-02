package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteGwStaticDataDto", description = "站点关口总览静态数据返回实体类")
public class SiteGwStaticDataDto {

    /**
     * 总有功功率
     */
    @ApiModelProperty(value = "总有功功率")
    private Double power;

    /**
     * 功率因数
     */
    @ApiModelProperty(value = "功率因数")
    private Double powerFactor;

}
