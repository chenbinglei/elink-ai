package com.sunmax.together.vo.operation.operationAnalysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PvOperationAnalysisVo", description = "光伏运行分析查询条件")
public class PvOperationAnalysisVo {

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id", required = true)
    private String siteIds;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private String startDate;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private String endDate;

    /**
     * 时间维度 1-月 2-年 3-生命周期
     */
    @ApiModelProperty(value = "时间维度 1-月 2-年 3-生命周期 ", required = true)
    private Integer dateType;

}
