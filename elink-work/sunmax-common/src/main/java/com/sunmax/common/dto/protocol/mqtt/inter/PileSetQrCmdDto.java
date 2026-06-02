package com.sunmax.common.dto.protocol.mqtt.inter;

/**
 * 4.45 CMD_PILE_SETQR,//设置二维码前缀 58
 * 发送方向：平台服务--->前置服务
 */

import lombok.Data;

@Data
public class PileSetQrCmdDto {

    /**
     * 电桩编号
     */
    private String pilesCode;

    /**
     * 前缀格式 0-前缀+桩编号 1-前缀+桩编号+枪编号
     */
    private Integer qrFormat;

    /**
     * 二维码前缀长度
     * 长度最大不超过200字节
     */
    private Integer qrLen;

    /**
     * 二维码内容前缀
     */
    private String qrStr;

}
