package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "告警事件查询实体类")
public class AlarmRecordQueryVo {

    /**
     * 设备id
     */
    @Schema(description = "多个设备编号")
    private Set<String> deviceCodes;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @Schema(description = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @Schema(description = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startDate;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endDate;

    /**
     * 枪编码
     */
    @Schema(description = "枪编码")
    private String gunCode;

    /**
     * 故障码
     */
    @Schema(description = "故障码")
    private String faultCode;

    /**
     * 恢复开始时间
     */
    @Schema(description = "恢复开始时间")
    private String recoverStartDate;

    /**
     * 恢复结束时间
     */
    @Schema(description = "恢复结束时间")
    private String recoverEndDate;

    /**
     * 忽略状态 0-未忽略 1-已忽略
     */
    @Schema(description = "忽略状态 0-未忽略 1-已忽略")
    private Integer ignoreStatus;

}
