package com.sunmax.common.vo.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OperationOverviewVo", description = "运营总览查询条件实体类")
public class OperationOverviewVo {

    /**
     * 多个站点id 例如['siteId1','siteId2']
     */
    @ApiModelProperty(value = "多个站点id 例如['siteId1','siteId2']", required = true)
    private String siteIds;

    /**
     * 多个系统id 例如['systemId1','systemId2']
     */
    @ApiModelProperty(value = "多个系统id 例如['systemId1','systemId2']", required = true)
    private String systemIds;

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
     * 上一个开始时间
     */
    @ApiModelProperty(value = "上一个开始时间")
    private String beforeStartDate;

    /**
     * 上一个结束时间
     */
    @ApiModelProperty(value = "上一个结束时间")
    private String beforeEndDate;

}
