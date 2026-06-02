package com.sunmax.common.vo.protocol.mqtt.inter;

import lombok.Data;

/**
 * 4.46 CMD_PILE_SETQR_RES,//设置二维码前缀响应 59
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileSetQrResVo {

    /**
     * 桩编号
     */
    private String pilesCode;

    /**
     * 结果 0-成功 1-失败
     */
    private Integer result;

}
