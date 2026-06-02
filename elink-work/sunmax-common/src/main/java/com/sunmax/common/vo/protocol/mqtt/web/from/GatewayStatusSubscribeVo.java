package com.sunmax.common.vo.protocol.mqtt.web.from;

import lombok.Data;

import java.util.List;

/**
 * 网关状态订阅实体类
 */
@Data
public class GatewayStatusSubscribeVo {

    /**
     * 网关编号
     */
    private String devId;

    /**
     * 网关运行时间-分钟
     */
    private String runTime;

    /**
     * cpu占用率
     */
    private Integer cpu;

    /**
     * 硬盘占用率
     */
    private Integer diskPercent;

    /**
     * 硬盘总容量
     */
    private Integer diskTotal;

    /**
     * 硬盘已用量
     */
    private Integer diskUsed;

    /**
     * 内存占用率
     */
    private Integer memPercent;

    /**
     * 内存总容量
     */
    private Integer memTotal;

    /**
     * 内存已用量
     */
    private Integer memUsed;

    /**
     * 网关网络配置
     */
    private List<Network> network;


    @Data
    public static class Network {

        /**
         * 网口
         */
        private String eth;

        /**
         * IP地址
         */
        private String addr;

        /**
         * 广播地址
         */
        private String bcast;

        /**
         * 物理地址(MAC地址)
         */
        private String hwaddr;

        /**
         * 子网掩码
         */
        private String mask;

        /**
         * 接收
         */
        private Integer rx;

        /**
         * 发送
         */
        private Integer tx;
    }

}
