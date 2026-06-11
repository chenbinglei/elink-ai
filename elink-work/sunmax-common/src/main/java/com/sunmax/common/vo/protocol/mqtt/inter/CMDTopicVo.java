package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "token")
    private String token;

    /**
     * 时间 “yyyy-mm-dd hh:mm:ss”
     */
    @Schema(description = "时间")
    private String timestamp;

    /**
     * 数据体
     */
    @Schema(description = "数据体")
    private Body body;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Body {

        /**
         * 设备编码
         */
        @Schema(description = "设备编码")
        private String devId;

        /**
         * 点号
         */
        @Schema(description = "点号")
        private String point;

        /**
         * 命令码
         */
        @Schema(description = "命令码")
        private String cmd;

        /**
         * 平台标识
         */
        @Schema(description = "平台标识")
        private String platformId;

        /**
         * 命令类型 TCP_CMD 1
         */
        @Schema(description = "命令类型")
        private String type;

        /**
         * 协议编号
         */
        @Schema(description = "协议编号")
        private String protCode;

        /**
         * 数据
         */
        @Schema(description = "数据")
        private Object body;
    }

}
