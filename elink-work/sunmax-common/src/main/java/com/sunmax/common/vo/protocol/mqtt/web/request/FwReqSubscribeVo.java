package com.sunmax.common.vo.protocol.mqtt.web.request;

import lombok.Data;

@Data
public class FwReqSubscribeVo {
    /**
     *固件类型
     */
    private Integer deviceType;

    /**
     *固件主版本号
     */
    private Integer majorNo;

    /**
     *固件副版本号
     */
    private Integer childNo;

    /**
     *固件内测版本号
     */
    private Integer betaNo;

    /**
     *数据块标号
     * 0~65535
     */
    private Integer dataFlag;

    /**
     *数据块大小
     * 0~1024
     */
    private Integer dataLen;

}
