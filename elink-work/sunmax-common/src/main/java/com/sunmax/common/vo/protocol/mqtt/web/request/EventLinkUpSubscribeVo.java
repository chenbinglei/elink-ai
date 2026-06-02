package com.sunmax.common.vo.protocol.mqtt.web.request;

import lombok.Data;

/**
 * 设备上线请求  终端版本订阅信息
 */
@Data
public class EventLinkUpSubscribeVo {

    /**
     * 设备名称
      */
    private String devName;

    /**
     * 设备编号
     */
    private String devSN;

    /**
     * 设备类型
     */
    private String devType;

    /**
     * 描述
     */
    private String distro;

    /**
     * 硬件版本号
     */
    private Integer harderVer;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 内核
     */
    private String kernel;

    /**
     * 信息
     */
    private String mfgInfo;

    /**
     * 软件版本号
     */
    private Integer softVer;

    /**
     * 设备版本号
     */
    private String version;

}
