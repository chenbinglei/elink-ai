package com.sunmax.device.dto.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备升级编辑列表返回实体类
 */
@Data
@Schema(description = "设备升级编辑列表返回实体类")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceUpdateDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 当前版本
     */
    @Schema(description = "当前版本")
    private String currentVersion;

    /**
     * 状态 1-可用 2-不可用
     */
    @Schema(description = "状态 1-可用 2-不可用")
    private Integer status;

}
