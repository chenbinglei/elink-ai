package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

import java.util.List;

//网关接收固件完成响应
@Data
public class FwGwOverSubscribeVo {
    /**
     * 充电桩编号
     */
    private List<String> pilesCode;
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

    private Integer failReason;

}
