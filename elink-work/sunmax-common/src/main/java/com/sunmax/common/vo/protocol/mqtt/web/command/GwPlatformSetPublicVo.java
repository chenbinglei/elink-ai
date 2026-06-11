package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "平台设置公共参数")
public class GwPlatformSetPublicVo {

    /**
     * 策略id
     */
    private Integer policyId;

    /**
     * 命令类型 “get”查询参数，“set”设置参数
     */
    private String cmdType;

    /**
     * 服务代码 0默认发给控制服务 1-tcp 2-uart 3-控制 4-协议分发
     */
    private Integer srvCode;

    /**
     * 策略名称 网关内要求策略名称唯一
     */
    private String name;

    /**
     * 策略参数 不定类型，不同参数类型不同
     */
    private PolicyCfg policyCfg;

    /**
     * 策略周期
     */
    private PolicyPeriod policyPeriod;

    @Data
    @Schema(description = "策略参数值")
    public static class PolicyCfg {

        /**
         * 接口代码，不同服务提供不同接口
         */
        private Integer apiCode;

        /**
         * 根据不同接口参数不同
         */
        private Object apiParams;

    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "设置平台驱动参数")
    public static class PlatformDriver {

        /**
         * 驱动名称
         */
        private String comDriver;

        /**
         * 驱动路径
         */
        private String comIP;

        /**
         * 通信端口
         */
        private String comPort;

        /**
         * 通信类型 1-tcp server 2-tcp client 3-uart
         */
        private Integer comType;

        /**
         * 自定义协议识别码，一般为起始域
         */
        private String domainCode;

        /**
         * 驱动路径
         */
        private String driverPath;

        /**
         * 串口配置
         */
        private List<UartCfg> uartCfg;

        /**
         * 服务器端口配置
         */
        private List<ServerCfg> serverCfg;
    }

    @Data
    public static class UartCfg {

        /**
         * 串口名称
         */
        private String portName;

        /**
         * 波特率
         */
        private Integer baudRate;

        /**
         * 数据位
         */
        private Integer dataBits;

        /**
         * 停止位
         */
        private Integer stopBits;

        /**
         * 校验位
         */
        private String parity;

        /**
         * 采集参数路径
         */
        private String paramPath;

        /**
         * 通道名称
         */
        private String channelName;

        /**
         * 主机Ip地址
         */
        private String hostAddr;

        /**
         * 主机端口
         */
        private Integer hostPort;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerCfg {

        /**
         * 名称
         */
        private String serverName;

        /**
         * 主键Ip地址
         */
        private String hostAddr;

        /**
         * 主机端口
         */
        private Integer hostPort;

        /**
         * 加密key
         */
        private String encrpt_key;

        /**
         * 加密向量
         */
        private String encrpt_key_iv;

        /**
         * 平台id
         */
        private String company_id;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PolicyPeriod {

        /**
         * 控制开关 true-启动 false-关闭
         */
        private Boolean controlSwitch;

        /**
         * 执行类型 1-全时段 2-工作日 3-周末 4-自定义时段
         */
        private Integer executeType;

        /**
         * 执行时段 自定义时段{1:["00:00:00","01:00:00"],2:["00:00:00"]}
         */
        private String executeTime;

        /**
         * 策略计划类型 1-全部启动 2-全部停止
         */
        private Integer planType;

        /**
         * 过滤日期
         */
//        private String filterDates;

    }

}
