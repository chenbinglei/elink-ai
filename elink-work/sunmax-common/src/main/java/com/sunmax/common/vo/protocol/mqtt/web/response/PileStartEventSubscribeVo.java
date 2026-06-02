package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 业务命令响应-启动事件 订阅实体类
 */
@Data
public class PileStartEventSubscribeVo {

    /**
     * 桩编码
     */
    private String pilesCode;

    /**
     *枪标识
     */
    private Integer gunCode;

    /**
     * 事件结果 0 成功；255 其他错误。
     */
    private Integer eventResult;

    /**
     * 失败详细原因
     */
    private Integer faileReason;

    /**
     * 失败详细原因
     */
    private String stopDetail;

    /**
     *开始充/放电时间
     */
    private int startTime;

    /**
     * 交易流水号
     */
    private String recordId;

    /**
     *BMS 信息
     */
    private PileBmsInfoSubscribeVo.BmsInfoVo bmsInfo;
}
