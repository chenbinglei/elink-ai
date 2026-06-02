package com.sunmax.devops.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OverviewQueryVo", description = "站点概览查询参数实体类")
public class OverviewQueryVo {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 设备id(主要用于关口总览传参)
     */
    @ApiModelProperty(value = "设备id(主要用于关口总览传参)")
    private String deviceId;

    /**
     * 日期类型 1-日 2-月 3-年 4-总(开始时间传最早的站点创建时间,结束时间传当前时间)
     */
    @ApiModelProperty(value = "日期类型 1-日 2-月 3-年 4-总(开始时间传最早的站点创建时间,结束时间传当前时间)", required = true)
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
