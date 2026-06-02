package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网关实时状态返回实体类
 */
@Data
@ApiModel(value = "GatewayStatusDto", description = "网关实时状态返回实体类")
public class GatewayStatusDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String name;

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String code;

    /**
     * 设备工作状态 1-在线 2-未注册 88-离线
     */
    @ApiModelProperty(value = "设备工作状态 1-在线 2-故障 3-未注册 88-离线")
    private Integer workState;

    /**
     * 运行时长
     */
    @ApiModelProperty("运行时长")
    private String runTime;

    /**
     * cpu占用率
     */
    @ApiModelProperty("cpu占用率")
    private Integer cpu;

    /**
     * 硬盘占用率
     */
    @ApiModelProperty("硬盘占用率")
    private Integer diskPercent;

    /**
     * 硬盘总容量
     */
    @ApiModelProperty("硬盘总容量")
    private Integer diskTotal;

    /**
     * 硬盘已用量
     */
    @ApiModelProperty("硬盘已用量")
    private Integer diskUsed;

    /**
     * 内存占用率
     */
    @ApiModelProperty("内存占用率")
    private Integer memPercent;

    /**
     * 内存总容量
     */
    @ApiModelProperty("内存总容量")
    private Integer memTotal;

    /**
     * 内存已用量
     */
    @ApiModelProperty("内存已用量")
    private Integer memUsed;
}
