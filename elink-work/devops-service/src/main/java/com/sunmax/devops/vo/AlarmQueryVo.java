package com.sunmax.devops.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AlarmQueryVo", description = "告警查询实体类")
public class AlarmQueryVo {

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id", required = true)
    private String siteIds;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceNumber;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 多个设备类型id
     */
    @ApiModelProperty(value = "多个设备类型id")
    private String typeIds;

    /**
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @ApiModelProperty(value = "事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警")
    private Integer eventLevel;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @ApiModelProperty(value = "事件状态 0-未恢复 1-已修复")
    private Integer eventStatus;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期 默认近一周", example = "yyyy-MM-dd", required = true)
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期 默认近一周", example = "yyyy-MM-dd", required = true)
    private String endDate;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

}
