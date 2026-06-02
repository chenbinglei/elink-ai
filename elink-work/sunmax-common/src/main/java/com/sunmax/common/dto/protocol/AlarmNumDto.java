package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AlarmNumDto", description = "告警条数返回实体类")
public class AlarmNumDto {

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 告警条数
     */
    @ApiModelProperty(value = "告警条数")
    private Integer alarmNum;
}
