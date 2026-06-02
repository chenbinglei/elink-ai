package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DataForwardChangeVo", description = "数据转发编辑参数实体类")
public class DataForwardChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 通道名称
     */
    @ApiModelProperty(value = "通道名称", required = true)
    private String channelName;

    /**
     * 接入协议类型 1-mqtt 2-http
     */
    @ApiModelProperty(value = "接入协议类型 1-mqtt 2-http", required = true)
    private Integer protocolType;

    /**
     * 接入协议标识
     */
    @ApiModelProperty(value = "接入协议标识", required = true)
    private String protocolCode;

    /**
     * 动态字段
     */
    @ApiModelProperty(value = "动态字段", required = true)
    private String dynamicFields;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址", required = true)
    private String address;

    /**
     * 状态 1-启用 2-断开
     */
    @ApiModelProperty(value = "状态 1-启用 2-断开", required = true)
    private Integer status;

}
