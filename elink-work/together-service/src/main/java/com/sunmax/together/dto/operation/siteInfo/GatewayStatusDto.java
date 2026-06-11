package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 网关实时状态返回实体类
 */
@Data
@Schema(description = "网关实时状态返回实体类")
public class GatewayStatusDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String name;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    private String code;

    /**
     * 设备工作状态 1-在线 2-未注册 88-离线
     */
    @Schema(description = "设备工作状态 1-在线 2-故障 3-未注册 88-离线")
    private Integer workState;

    /**
     * 运行时长
     */
    @Schema(description = "运行时长")
    private String runTime;

    /**
     * cpu占用率
     */
    @Schema(description = "cpu占用率")
    private Integer cpu;

    /**
     * 硬盘占用率
     */
    @Schema(description = "硬盘占用率")
    private Integer diskPercent;

    /**
     * 硬盘总容量
     */
    @Schema(description = "硬盘总容量")
    private Integer diskTotal;

    /**
     * 硬盘已用量
     */
    @Schema(description = "硬盘已用量")
    private Integer diskUsed;

    /**
     * 内存占用率
     */
    @Schema(description = "内存占用率")
    private Integer memPercent;

    /**
     * 内存总容量
     */
    @Schema(description = "内存总容量")
    private Integer memTotal;

    /**
     * 内存已用量
     */
    @Schema(description = "内存已用量")
    private Integer memUsed;
}
