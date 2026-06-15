package com.sunmax.together.vo.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光伏站点报表查询参数")
public class PvSiteReportQueryVo {


    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数(传0不分页，返回所有列表)")
    private Integer size;

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 时间维度 1-日 2-月 3-年
     */
    @Schema(description = "时间维度 1-日 2-月 3-年")
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
