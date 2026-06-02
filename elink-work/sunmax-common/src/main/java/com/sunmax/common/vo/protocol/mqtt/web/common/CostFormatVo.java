package com.sunmax.common.vo.protocol.mqtt.web.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 时段费率格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostFormatVo implements Serializable {

    /**
     * 时段开始时间 Unix时间戳(秒)+地区时区时间(秒)
     */
    private int startTime;

    /**
     * 时段结束时间 Unix时间戳(秒)+地区时区时间(秒)
     */
    private int endTime;

    /**
     * 时段电价 精度 0.001元/kW·h
     */
    private double price;

    /**
     * 服务费 精度0.001元/kW·h
     */
    private double serviceCharger;

    /**
     * 时段类型 1-尖 2-峰 3-平 4-谷
     */
    private Integer type;
}
