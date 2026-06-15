package com.sunmax.devops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "能源储能查询参数实体类")
public class EnergyQueryVo {

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 日期类型 1-日 2-月 3-年 4-总(开始时间传最早的站点创建时间,结束时间传当前时间)
     */
    @Schema(description = "日期类型 1-日 2-月 3-年 4-总(开始时间传最早的站点创建时间,结束时间传当前时间)")
    private Integer dateType;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

}
