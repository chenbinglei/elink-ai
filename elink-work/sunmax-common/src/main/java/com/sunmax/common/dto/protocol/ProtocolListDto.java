package com.sunmax.common.dto.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ProtocolListDto", description = "协议日志列表返回实体类")
public class ProtocolListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "枪编号")
    private String gunCode;

//    /**
//     * 报文类型 1-发送 2-接收
//     */
//    @ApiModelProperty(value = "报文类型 1-发送 2-接收")
//    private Integer type;
//
//    /**
//     * 消息类型 1-外网 2-内网
//     */
//    @ApiModelProperty(value = "消息类型 1-外网 2-内网")
//    private Integer messageType;

    /**
     * 发送方
     */
    @ApiModelProperty(value = "发送方")
    private String sender;

    /**
     * 接收方
     */
    @ApiModelProperty(value = "接收方")
    private String receiver;

    /**
     * 协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应
     */
    @ApiModelProperty(value = "协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应")
    private Integer protocolType;

    /**
     * 报文命令
     */
    @ApiModelProperty(value = "报文命令")
    private String cmd;

    /**
     * 报文内容
     */
    @ApiModelProperty(value = "报文内容")
    private String content;

    /**
     * 时间
     */
    @ApiModelProperty(value = "时间")
    private String dateTime;

}
