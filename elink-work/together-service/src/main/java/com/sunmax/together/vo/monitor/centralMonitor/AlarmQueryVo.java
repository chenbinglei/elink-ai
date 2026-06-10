package com.sunmax.together.vo.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "告警查询实体类")
public class AlarmQueryVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

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
     * 开始日期
     */
    @Schema(description = "开始日期")
    private String startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private String endDate;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @Schema(description = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @Schema(description = "事件状态 0-未恢复 1-已修复")
    private Integer eventStatus;

    /**
     * 忽略状态 0-未忽略 1-已忽略
     */
    @Schema(description = "忽略状态 0-未忽略 1-已忽略")
    private Integer ignoreStatus;

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
