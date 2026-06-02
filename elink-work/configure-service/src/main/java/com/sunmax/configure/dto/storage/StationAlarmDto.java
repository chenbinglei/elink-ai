package com.sunmax.configure.dto.storage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StationAlarmDto", description = "推送储能平台故障数据实体类")
public class StationAlarmDto {

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号", required = true)
    private String stationNo;

    /**
     * 数据时间 时间戳（timestamp），毫秒级
     */
    @ApiModelProperty(value = "数据时间", required = true)
    private Long dataTime;

    /**
     * 故障编号
     */
    @ApiModelProperty(value = "故障编号", required = true)
    private String alarmNo;

    /**
     * 故障状态 01-发生故障 00-故障恢复
     */
    @ApiModelProperty(value = "故障状态 01-发生故障 00-故障恢复", required = true)
    private String alarmStatus;

    /**
     * 故障等级 010-三级 001-二级 000-一级
     */
    @ApiModelProperty(value = "故障等级 010-三级 001-二级 000-一级", required = true)
    private String alarmLevel;

    /**
     * 故障类型 000-能量管理系统故障 001-电池管理系统故障 010-变流器故障 011-电池故障 100-消防单元故障 999-其他故障
     */
    @ApiModelProperty(value = "故障类型 000-能量管理系统故障 001-电池管理系统故障 010-变流器故障 011-电池故障 100-消防单元故障 999-其他故障")
    private String alarmType;

    /**
     * 故障名称
     */
    @ApiModelProperty(value = "故障名称")
    private String alarmTitle;

    /**
     * 故障发生时间 时间戳（timestamp），毫秒级
     */
    @ApiModelProperty(value = "故障发生时间", required = true)
    private Long beginTime;

    /**
     * 故障恢复时间 时间戳（timestamp），毫秒级（当alarmStatus=00时必填）
     */
    @ApiModelProperty(value = "故障恢复时间")
    private Long finishTime;

}
