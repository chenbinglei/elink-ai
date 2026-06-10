package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "网关设备列表返回实体类")
public class SiteGwDeviceDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

}
