package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "需求响应命令参数实体类")
public class ControlResponseVo {

    /**
     * 场站id
     */
    @Schema(description = "场站id")
    private Long stationId;

    /**
     * 需求响应命令参数
     */
    @Schema(description = "需求响应命令参数")
    private ControlInfo controlInfo;

    /**
     * 控制参数
     */
    @Data
    @Schema(description = "需求响应命令参数")
    public static class ControlInfo {

        /**
         * 需求响应事件编号
         */
        @Schema(description = "需求响应事件编号")
        private String sjbh;

        /**
         * 需求响应事件名称
         */
        @Schema(description = "需求响应事件名称")
        private String sjmc;

        /**
         * 需求响应类型 1-削峰 2-填谷
         */
        @Schema(description = "需求响应类型 1-削峰 2-填谷")
        private String xylx;

        /**
         * 响应容量kW
         */
        @Schema(description = "响应容量kW")
        private Float xyrl;

        /**
         * 事件类型 1-日前 2-分钟
         */
        @Schema(description = "事件类型 1-日前 2-分钟")
        private String sjlx;

        /**
         * 开始时间 格式yyyy-MM-dd HH:mm:ss
         */
        @Schema(description = "开始时间")
        private String kssj;

        /**
         * 结束时间 格式yyyy-MM-dd HH:mm:ss
         */
        @Schema(description = "结束时间")
        private String jssj;

        /**
         * 邀约响应截止时间 格式yyyy-MM-dd HH:mm:ss
         */
        @Schema(description = "邀约响应截止时间")
        private String yyjzsj;

    }

}
