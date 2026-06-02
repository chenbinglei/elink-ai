package com.sunmax.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2022/6/811:17
 * @version: 1.0
 * @注释: 市级返回实体类
 */
@Data
@ApiModel("CityDto")
public class CityDto {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private Long id;


    /**
     * 全国城市id
     */
    @ApiModelProperty("全国城市id")
    private String cityId;

    /**
     * 全国城市名称
     */
    @ApiModelProperty("全国城市名称")
    private String cityName;

    /**
     * 全国省id
     */
    @ApiModelProperty("全国省id")
    private String provinceId;

    /**
     * 经度
     */
    @ApiModelProperty("经度")
    private String latitude;

    /**
     * 纬度
     */
    @ApiModelProperty("纬度")
    private String longitude;
}
