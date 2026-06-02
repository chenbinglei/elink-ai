package com.sunmax.common.vo.protocol.mqtt.web.command;

import lombok.Data;

import java.util.List;

@Data
public class PileUpdatePublishVo {

    /**
     * 充电桩编号
     */
    private List<String> pilesCode;

    /**
     * 固件获取发起者
     */
    private Integer sponsor;

    /**
     *强制升级:0 不强制； 1 强制
     */
    private Integer forced_update;

    /**
     *要求硬件主版本号
     */
    private Integer request_majorNo;

    /**
     *要求硬件子版本号
     */
    private Integer request_childNo;

    /**
     *新固件类型
     */
    private Integer deviceType;

    /**
     *新固件主版本号
     */
    private Integer majorNo;

    /**
     *新固件副版本号
     */
    private Integer childNo;

    /**
     *新固件内测版本号
     */
    private Integer betaNo;

    /**
     *新固件编译时间
     */
    private int compiletime;

    /**
     *新固件数据大小
     */
    private int dataLen;

    /**
     *新固件数据校验码
     */
    private Long deviceCRC;

}
