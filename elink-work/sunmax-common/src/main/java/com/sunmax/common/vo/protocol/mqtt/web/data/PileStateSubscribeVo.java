package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 电桩状态实体类
 */
@Data
public class PileStateSubscribeVo implements Serializable {


    /**
     * 枪状态
     */
    private List<GunStateVo> gun_state = new ArrayList<>();

    /**
     * 复位次数
     */
    private int resetTimes;

    /**
     * 充电桩工作状态
     */
    private Integer workState;

    @Data
    public static class GunStateVo implements Serializable {
        /**
         * 枪编号
         */
        private Integer gunCode;

        /**
         * 车辆连接状态
         */
        private Integer vehicleConnState;

        /**
         * 充电枪工作状态
         */
        private Integer workState;
    }


}
