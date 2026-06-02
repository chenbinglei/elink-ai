package com.sunmax.device.dto.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备升级编辑列表返回实体类
 */
@Data
@ApiModel(value = "DeviceUpdateDto", description = "设备升级编辑列表返回实体类")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceUpdateDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 当前版本
     */
    @ApiModelProperty(value = "当前版本")
    private String currentVersion;

    /**
     * 状态 1-可用 2-不可用
     */
    @ApiModelProperty(value = "状态 1-可用 2-不可用")
    private Integer status;

}
