package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(value = "AlarmRecordQueryVo", description = "告警事件查询实体类")
public class AlarmRecordQueryVo {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "多个设备编号", required = true)
    private Set<String> deviceCodes;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @ApiModelProperty(value = "告警状态 0-未修复 1-已修复")
    private Integer alarmStatus;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startDate;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endDate;

    /**
     * 枪编码
     */
    @ApiModelProperty(value = "枪编码")
    private String gunCode;

    /**
     * 故障码
     */
    @ApiModelProperty(value = "故障码")
    private String faultCode;

    /**
     * 恢复开始时间
     */
    @ApiModelProperty(value = "恢复开始时间")
    private String recoverStartDate;

    /**
     * 恢复结束时间
     */
    @ApiModelProperty(value = "恢复结束时间")
    private String recoverEndDate;

    /**
     * 忽略状态 0-未忽略 1-已忽略
     */
    @ApiModelProperty(value = "忽略状态 0-未忽略 1-已忽略")
    private Integer ignoreStatus;

}
