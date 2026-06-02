package com.sunmax.crontab.vo.mqtt;

import lombok.Data;

/**
 * 即时指令下发
 * Topic：/[vendor]/[GW_SN]/service/set
 * 该topic用于给指定网关下发指令
 */
@Data
public class HDSetVo {

    /**
     * 云边对接情况下，代表下挂设备的唯一标识，一般为序列号。
     * 例如负荷控制终端下挂断路器那么填写断路器的序列号，如果终端直接下挂智能设备（例如可接收modbus指令的空调），那么填写智能设备的唯一编码。
     * 云云对接情况下，与vpp平台中资源对应的虚拟设备保持一致。
     */
    private String sn;

    /**
     * 消息id，用来唯一确认一条消息,字符串类型
     */
    private String mi;

    /**
     * 控制指令发出时间的13位时间戳
     */
    private Long time;

    /**
     * 服务标识，目前支持两种控制方式，开关控制switchCtl和功率控制powerCtl。
     * switchStateGet-开关状态
     * switchCtl(target:open)-即时分闸 switchCtl(target:close)-即时合闸
     * powerCtl-功率控制
     */
    private String identifier;

    /**
     * 控制参数，不同的控制方式的参数名称和类型有可能不同。开关控制的参数名为target，类型为字符串；功率控制的参数名为target，类型为带符号的浮点数（%.2f），单位为瓦/W
     */
    private Param params;

    @Data
    public static class Param {

        /**
         * 目标值
         */
        private Object target;

    }

}
