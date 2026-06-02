package com.sunmax.common.dto.protocol.mqtt.web.falut;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 电力模块故障
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElecModuleFault implements Serializable {

    /**
     *电力模块地址 从1开始
     */
    private Integer moduleAddr;

    /**
     *PCU 上报故障信息
     *      Bit 0： 交流输入缺相
     *      Bit 1： 交流输入过压
     *      Bit 2： 交流输入欠压
     *      Bit 3： 交流输入不平衡
     *      Bit 4： 电力模块上报 ID 重复
     *      Bit 5： 电力模块上报短路
     *      Bit 6： 电力模块上报输出过压
     *      Bit 7： 电力模块上报输出过流
     *      Bit 8： 电力模块上报输出欠压
     *      Bit 9： 电力模块上报风扇故障
     *      Bit 10：电力模块上报过温
     *      Bit 11：枪不可用（所有电力模块故障）
     *
     *      Bit 15：电力模块通讯超时
     */
    private Integer pcuReportFault;

    /**
     * PCU超时类故障信息
     *  Bit 0：电力模块通讯超时
     *  Bit 1：
     */
    private Integer pcuTimeout;
}
