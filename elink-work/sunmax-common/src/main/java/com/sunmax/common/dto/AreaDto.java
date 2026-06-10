package com.sunmax.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2022/6/811:19
 * @version: 1.0
 * @注释: 区级数据返回实体类
 */
@Data
@Schema(description = "CityDto")
public class AreaDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private Long id;

    /**
     * 全国区县id
     */
    @Schema(description = "全国区县id")
    private String areaId;

    /**
     * 全国区县名称
     */
    @Schema(description = "全国区县名称")
    private String areaName;

    /**
     * 全国城市id
     */
    @Schema(description = "全国城市id")
    private String cityId;
}
