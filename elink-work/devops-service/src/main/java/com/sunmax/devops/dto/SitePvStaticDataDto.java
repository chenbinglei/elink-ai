package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SitePvStaticDataDto", description = "站点光伏总览静态数据返回实体类")
public class SitePvStaticDataDto {

    /**
     * 光伏容量
     */
    @ApiModelProperty(value = "光伏容量")
    private Double pvCap = 0.0;

    /**
     * 实时功率
     */
    @ApiModelProperty(value = "实时功率")
    private Double power;

}
