package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "平台设置参数")
public class PlatformSetDto {

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceCode;

    /**
     * 下发状态 -1-执行超时 0-执行成功 1-执行失败 255-其他原因
     */
    @Schema(description = "下发状态 -1-执行超时 0-执行成功 1-执行失败 255-其他原因")
    private Integer issuedStatus;

}
