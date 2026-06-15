package com.sunmax.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据转发详情返回实体类")
public class DataForwardDetailDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 通道名称
     */
    @Schema(description = "通道名称")
    private String channelName;

    /**
     * 接入协议标识
     */
    @Schema(description = "接入协议标识")
    private String protocolCode;

    /**
     * 接入协议类型 1-mqtt 2-http
     */
    @Schema(description = "接入协议类型 1-mqtt 2-http")
    private Integer protocolType;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String address;

    /**
     * 动态字段
     */
    @Schema(description = "动态字段")
    private String dynamicFields;

    /**
     * 状态 1-启用 2-断开
     */
    @Schema(description = "状态 1-启用 2-断开")
    private Integer status;

}
