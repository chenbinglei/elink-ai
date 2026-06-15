package com.sunmax.together.vo.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "故障告警查询条件参数实体类")
public class FaultAlarmQueryVo {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 设备类型id
     */
    @Schema(description = "设备类型id")
    private String typeId;

    /**
     * 事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警
     */
    @Schema(description = "事件级别 0-提示告警 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 开始时间(yyyy-MM-dd)
     */
    @Schema(description = "开始时间(yyyy-MM-dd)")
    private String startDate;

    /**
     * 结束时间(yyyy-MM-dd)
     */
    @Schema(description = "结束时间(yyyy-MM-dd)")
    private String endDate;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @Schema(description = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
