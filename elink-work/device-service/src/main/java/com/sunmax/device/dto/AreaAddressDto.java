package com.sunmax.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 区域地址返回实体类
 */
@Data
@ApiModel("areaAddressDto")
public class AreaAddressDto {

    /**
     * 省名称
     */
    @ApiModelProperty("省名称")
    private String provinceName;

    /**
     * 市名称
     */
    @ApiModelProperty("市名称")
    private String cityName;

    /**
     * 区域名称
     */
    @ApiModelProperty("区域名称")
    private String areaName;

    /**
     * 详细地址
     */
    @ApiModelProperty("详细地址")
    private String address;
}
