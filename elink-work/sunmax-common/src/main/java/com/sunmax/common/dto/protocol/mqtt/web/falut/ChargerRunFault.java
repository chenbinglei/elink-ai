package com.sunmax.common.dto.protocol.mqtt.web.falut;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电桩运行故障
 */
@Data
@NoArgsConstructor
public class ChargerRunFault {

    /**
     * 电桩紧急停止故障
     *      Bit 0： 停电
     *      Bit 1： 急停按钮按下
     *      Bit 2： 电桩箱门打开
     */
    private Integer chargerStopFault;

    /**
     * 电桩通用故障
     *      Bit 0： 电桩箱门故障
     *      Bit 1： 防雷器故障
     *      Bit 2： 交流接触器故障
     *      Bit 3： 电桩风扇故障
     *      Bit 4： 地锁故障
     *      Bit 5： 桩体过温故障
     *      Bit 6： PE 掉线
     */
    private Integer chargerComFault;

    /**
 *          TCU 上报故障
     *      Bit 0： 主控板元器件故障
     *      Bit 1： 桩与平台通讯断开故障
     *      Bit 2： RTC 时钟错误
     */
    private Integer tcuReportFault;

}
