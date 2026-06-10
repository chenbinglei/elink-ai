package com.sunmax.together.vo.operation.deviceManage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "告警列表查询参数")
public class AlarmListQueryVo {

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

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 电桩编码
     */
    @Schema(description = "电桩编码")
    private String pileCode;

    /**
     * 故障码
     */
    @Schema(description = "故障码")
    private String faultCode;

    /**
     * 告警开始时间
     */
    @Schema(description = "告警开始时间")
    private String alarmStartDate;

    /**
     * 告警结束时间
     */
    @Schema(description = "告警结束时间")
    private String alarmEndDate;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @Schema(description = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @Schema(description = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;
}
