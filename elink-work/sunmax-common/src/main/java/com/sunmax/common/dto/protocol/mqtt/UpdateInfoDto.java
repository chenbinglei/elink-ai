package com.sunmax.common.dto.protocol.mqtt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 升级信息
 */
@Data
public class UpdateInfoDto {

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("固件包路径")
    private String firmwarePath;

    @ApiModelProperty("硬件主版本号")
    private Integer hardwareMajorVersion;

    @ApiModelProperty("硬件次版本号")
    private Integer hardwareMinorVersion;

    @ApiModelProperty("固件主版本号")
    private Integer firmwareMajorVersion;

    @ApiModelProperty("固件次版本号")
    private Integer firmwareMinorVersion;

    @ApiModelProperty("数据块标号")
    private Integer dataBlockLabel;

    @ApiModelProperty("数据块总个数")
    private Integer dataBlockSum = 0;

    @ApiModelProperty("状态 -1-正在进行中 0-成功 1-数据校验失败 2-电桩应答超时 3-flash擦除失败 4-flash写入失败 5-同版本不升级 6-平台应答超时 255-其他原因")
    private Integer status;

    @ApiModelProperty("信息")
    private String message;
}
