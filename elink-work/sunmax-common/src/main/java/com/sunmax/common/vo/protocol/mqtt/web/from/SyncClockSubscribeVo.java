package com.sunmax.common.vo.protocol.mqtt.web.from;

import lombok.Data;

/**
 * 同步时钟订阅实体类
 */
@Data
public class SyncClockSubscribeVo {

    /**
     * 时间戳-网关当前时间
     */
    private Long timestamp;

    /**
     * 类型 1-查询时钟  2-设置时钟
     */
    private Integer type;

    /**
     * 结果 0-失败 1-成功
     */
    private Integer result;

}
