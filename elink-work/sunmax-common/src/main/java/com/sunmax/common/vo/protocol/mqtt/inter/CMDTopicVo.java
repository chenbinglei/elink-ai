package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前置消息格式实体类
 */
@Data
public class CMDTopicVo {

    /**
     * token
     */
    @ApiModelProperty(value = "token", required = true)
    private String token;

    /**
     * 时间 “yyyy-mm-dd hh:mm:ss”
     */
    @ApiModelProperty(value = "时间", required = true)
    private String timestamp;

    /**
     * 数据体
     */
    @ApiModelProperty(value = "数据体", required = true)
    private Body body;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Body {

        /**
         * 设备编码
         */
        @ApiModelProperty(value = "设备编码", required = true)
        private String devId;

        /**
         * 点号
         */
        @ApiModelProperty(value = "点号")
        private String point;

        /**
         * 命令码
         */
        @ApiModelProperty(value = "命令码", required = true)
        private String cmd;

        /**
         * 平台标识
         */
        @ApiModelProperty(value = "平台标识", required = true)
        private String platformId;

        /**
         * 命令类型 TCP_CMD 1
         */
        @ApiModelProperty(value = "命令类型", required = true)
        private String type;

        /**
         * 协议编号
         */
        @ApiModelProperty(value = "协议编号")
        private String protCode;

        /**
         * 数据
         */
        @ApiModelProperty(value = "数据", required = true)
        private Object body;
    }

}
