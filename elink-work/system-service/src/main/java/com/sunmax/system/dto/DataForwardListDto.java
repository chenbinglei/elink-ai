package com.sunmax.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据转发列表")
public class DataForwardListDto {

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
     * 接入协议名称
     */
    @Schema(description = "接入协议名称")
    private String protocolName;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String address;

    /**
     * 状态 1-启用 2-断开
     */
    @Schema(description = "状态 1-启用 2-断开")
    private Integer status;

}
