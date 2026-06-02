package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteSeStaticDataDto", description = "站点储能概览静态数据返回实体类")
public class SiteSeStaticDataDto {

    /**
     * 储能PCS额定功率
     */
    @ApiModelProperty(value = "储能PCS额定功率")
    private Double pcsPower = 0.0;

    /**
     * 储能电池簇额定容量
     */
    @ApiModelProperty(value = "储能电池簇额定容量")
    private Double batteryCap = 0.0;

    /**
     * 储能SOC
     */
    @ApiModelProperty(value = "储能SOC")
    private Double soc = 0.0;

    /**
     * 实时功率
     */
    @ApiModelProperty(value = "实时功率")
    private Double power;

}
