package com.sunmax.together.vo.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AlarmQueryVo", description = "告警查询实体类")
public class AlarmQueryVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 设备类型id
     */
    @ApiModelProperty(value = "设备类型id")
    private String typeId;

    /**
     * 开始日期
     */
    @ApiModelProperty(value = "开始日期")
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private String endDate;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @ApiModelProperty(value = "事件状态 0-未恢复 1-已修复")
    private Integer eventStatus;

    /**
     * 忽略状态 0-未忽略 1-已忽略
     */
    @ApiModelProperty(value = "忽略状态 0-未忽略 1-已忽略")
    private Integer ignoreStatus;

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
