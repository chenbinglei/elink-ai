package com.sunmax.common.vo.protocol.mqtt.web.platform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 同步时钟发布参数实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncClockPublishVo {

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 类型 1-查询时钟  2-设置时钟
     */
    private Integer type;

}
