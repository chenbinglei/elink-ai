package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "场站地图数据返回实体类")
public class SiteMapDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 电桩容量
     */
    @Schema(description = "电桩容量")
    private Double pileCap = 0.0;

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
     * 光伏容量
     */
    @Schema(description = "光伏容量")
    private Double pvCap = 0.0;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

}
