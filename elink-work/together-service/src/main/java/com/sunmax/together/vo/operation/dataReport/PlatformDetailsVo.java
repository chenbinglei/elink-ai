package com.sunmax.together.vo.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileListQueryVo", description = "电桩列表查询参数")
public class PlatformDetailsVo {

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
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 时间类型 1-逐日 2-逐月
     */
    @ApiModelProperty(value = "时间类型 1-逐日 2-逐月")
    private Integer timeType;
}
