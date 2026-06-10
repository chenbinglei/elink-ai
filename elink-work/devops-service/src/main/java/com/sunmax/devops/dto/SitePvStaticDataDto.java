package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点光伏总览静态数据返回实体类")
public class SitePvStaticDataDto {

    /**
     * 光伏容量
     */
    @Schema(description = "光伏容量")
    private Double pvCap = 0.0;

    /**
     * 实时功率
     */
    @Schema(description = "实时功率")
    private Double power;

}
