package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点储能概览静态数据返回实体类")
public class SiteSeStaticDataDto {

    /**
     * 储能PCS额定功率
     */
    @Schema(description = "储能PCS额定功率")
    private Double pcsPower = 0.0;

    /**
     * 储能电池簇额定容量
     */
    @Schema(description = "储能电池簇额定容量")
    private Double batteryCap = 0.0;

    /**
     * 储能SOC
     */
    @Schema(description = "储能SOC")
    private Double soc = 0.0;

    /**
     * 实时功率
     */
    @Schema(description = "实时功率")
    private Double power;

}
