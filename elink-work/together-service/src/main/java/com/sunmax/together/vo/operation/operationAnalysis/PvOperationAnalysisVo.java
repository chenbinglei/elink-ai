package com.sunmax.together.vo.operation.operationAnalysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光伏运行分析查询条件")
public class PvOperationAnalysisVo {

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startDate;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endDate;

    /**
     * 时间维度 1-月 2-年 3-生命周期
     */
    @Schema(description = "时间维度 1-月 2-年 3-生命周期 ")
    private Integer dateType;

}
