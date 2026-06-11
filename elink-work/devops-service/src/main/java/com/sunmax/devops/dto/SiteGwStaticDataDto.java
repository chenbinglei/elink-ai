package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点关口总览静态数据返回实体类")
public class SiteGwStaticDataDto {

    /**
     * 总有功功率
     */
    @Schema(description = "总有功功率")
    private Double power;

    /**
     * 功率因数
     */
    @Schema(description = "功率因数")
    private Double powerFactor;

}
