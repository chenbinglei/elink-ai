package com.sunmax.common.dto.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "协议日志列表返回实体类")
public class ProtocolListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private String gunCode;

//    /**
//     * 报文类型 1-发送 2-接收
//     */
//    @Schema(description = "报文类型 1-发送 2-接收")
//    private Integer type;
//
//    /**
//     * 消息类型 1-外网 2-内网
//     */
//    @Schema(description = "消息类型 1-外网 2-内网")
//    private Integer messageType;

    /**
     * 发送方
     */
    @Schema(description = "发送方")
    private String sender;

    /**
     * 接收方
     */
    @Schema(description = "接收方")
    private String receiver;

    /**
     * 协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应
     */
    @Schema(description = "协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应")
    private Integer protocolType;

    /**
     * 报文命令
     */
    @Schema(description = "报文命令")
    private String cmd;

    /**
     * 报文内容
     */
    @Schema(description = "报文内容")
    private String content;

    /**
     * 时间
     */
    @Schema(description = "时间")
    private String dateTime;

}
