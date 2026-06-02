package com.sunmax.together.vo.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PvSiteReportQueryVo", description = "光伏站点报表查询参数")
public class PvSiteReportQueryVo {


    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数(传0不分页，返回所有列表)", required = true)
    private Integer size;

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id", required = true)
    private String siteIds;

    /**
     * 时间维度 1-日 2-月 3-年
     */
    @ApiModelProperty(value = "时间维度 1-日 2-月 3-年", required = true)
    private Integer dateType;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private String endTime;
}
