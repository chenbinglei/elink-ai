package com.sunmax.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 区域地址返回实体类
 */
@Data
@Schema(description = "areaAddressDto")
public class AreaAddressDto {

    /**
     * 省名称
     */
    @Schema(description = "省名称")
    private String provinceName;

    /**
     * 市名称
     */
    @Schema(description = "市名称")
    private String cityName;

    /**
     * 区域名称
     */
    @Schema(description = "区域名称")
    private String areaName;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;
}
