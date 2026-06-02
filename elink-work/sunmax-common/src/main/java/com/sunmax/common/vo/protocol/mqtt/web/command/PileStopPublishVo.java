package com.sunmax.common.vo.protocol.mqtt.web.command;

import lombok.Data;

@Data
public class PileStopPublishVo {

    /**
     * 充电桩编号
     */
    private String pilesCode;

    /**
     * 充电枪编号
     */
    private Integer gunCode;

    /**
     * 结束原因
     */
    private Integer stopReason;

    /**
     * 交易记录号
     */
    private String recordId;

}
