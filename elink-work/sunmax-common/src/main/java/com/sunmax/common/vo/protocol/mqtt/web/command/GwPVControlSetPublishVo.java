package com.sunmax.common.vo.protocol.mqtt.web.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 调控需求下发
 */
@Data
@Schema(description = "GwPVControlSetPublishVo")
public class GwPVControlSetPublishVo {

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
    private Integer xylx;

    /**
     * 响应容量kW
     */
    @Schema(description = "响应容量kW")
    private Float xyrl;

    /**
     * 事件类型 1-日前 2-分钟
     */
    @Schema(description = "事件类型 1-日前 2-分钟")
    private Integer sjlx;

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
