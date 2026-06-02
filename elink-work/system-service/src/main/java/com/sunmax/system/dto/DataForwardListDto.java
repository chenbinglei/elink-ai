package com.sunmax.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DataForwardListDto", description = "数据转发列表")
public class DataForwardListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 通道名称
     */
    @ApiModelProperty(value = "通道名称")
    private String channelName;

    /**
     * 接入协议标识
     */
    @ApiModelProperty(value = "接入协议标识")
    private String protocolCode;

    /**
     * 接入协议类型 1-mqtt 2-http
     */
    @ApiModelProperty(value = "接入协议类型 1-mqtt 2-http")
    private Integer protocolType;

    /**
     * 接入协议名称
     */
    @ApiModelProperty(value = "接入协议名称")
    private String protocolName;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 状态 1-启用 2-断开
     */
    @ApiModelProperty(value = "状态 1-启用 2-断开")
    private Integer status;

}
