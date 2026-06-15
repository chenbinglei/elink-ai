package com.sunmax.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2022/6/811:17
 * @version: 1.0
 * @注释: 市级返回实体类
 */
@Data
@Schema(description = "CityDto")
public class CityDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private Long id;


    /**
     * 全国城市id
     */
    @Schema(description = "全国城市id")
    private String cityId;

    /**
     * 全国城市名称
     */
    @Schema(description = "全国城市名称")
    private String cityName;

    /**
     * 全国省id
     */
    @Schema(description = "全国省id")
    private String provinceId;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private String latitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private String longitude;
}
