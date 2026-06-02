package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PlatformSetDto", description = "平台设置参数")
public class PlatformSetDto {

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 下发状态 -1-执行超时 0-执行成功 1-执行失败 255-其他原因
     */
    @ApiModelProperty("下发状态 -1-执行超时 0-执行成功 1-执行失败 255-其他原因")
    private Integer issuedStatus;

}
