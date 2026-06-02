package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ControlResponseVo", description = "需求响应命令参数实体类")
public class ControlResponseVo {

    /**
     * 场站id
     */
    @ApiModelProperty(value = "场站id", required = true)
    private Long stationId;

    /**
     * 需求响应命令参数
     */
    @ApiModelProperty(value = "需求响应命令参数", required = true)
    private ControlInfo controlInfo;

    /**
     * 控制参数
     */
    @Data
    @ApiModel(value = "ControlInfo", description = "需求响应命令参数")
    public static class ControlInfo {

        /**
         * 需求响应事件编号
         */
        @ApiModelProperty(value = "需求响应事件编号", required = true)
        private String sjbh;

        /**
         * 需求响应事件名称
         */
        @ApiModelProperty(value = "需求响应事件名称")
        private String sjmc;

        /**
         * 需求响应类型 1-削峰 2-填谷
         */
        @ApiModelProperty(value = "需求响应类型 1-削峰 2-填谷", required = true)
        private String xylx;

        /**
         * 响应容量kW
         */
        @ApiModelProperty(value = "响应容量kW", required = true)
        private Float xyrl;

        /**
         * 事件类型 1-日前 2-分钟
         */
        @ApiModelProperty(value = "事件类型 1-日前 2-分钟", required = true)
        private String sjlx;

        /**
         * 开始时间 格式yyyy-MM-dd HH:mm:ss
         */
        @ApiModelProperty(value = "开始时间", required = true)
        private String kssj;

        /**
         * 结束时间 格式yyyy-MM-dd HH:mm:ss
         */
        @ApiModelProperty(value = "结束时间", required = true)
        private String jssj;

        /**
         * 邀约响应截止时间 格式yyyy-MM-dd HH:mm:ss
         */
        @ApiModelProperty(value = "邀约响应截止时间", required = true)
        private String yyjzsj;

    }

}
