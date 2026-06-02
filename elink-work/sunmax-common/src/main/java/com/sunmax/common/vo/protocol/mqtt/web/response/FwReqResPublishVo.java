package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

//固件数据块请求信息响应
@Data
public class FwReqResPublishVo {
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
    /**
     *数据块大小
     * 0~1024
     */
    private String dataBlock;

}
