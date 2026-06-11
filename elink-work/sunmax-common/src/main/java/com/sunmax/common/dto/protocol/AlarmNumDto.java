package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "告警条数返回实体类")
public class AlarmNumDto {

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceCode;

    /**
     * 告警条数
     */
    @Schema(description = "告警条数")
    private Integer alarmNum;
}
