package com.sunmax.common.vo.protocol.mqtt.web.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 费率下发 发布实体类
 */
@Data
@ApiModel(value = "EventRateReqPublicVo", description = "费率下发 发布实体类")
public class EventRateReqPublicVo {

    /**
     * 充电费率模板ID
     */
    @ApiModelProperty(value = "充电费率模板ID")
    private String cRateId;

    /**
     * 充电有效时段数
     */
    @ApiModelProperty(value = "充电有效时段数")
    private Integer cTimeFrameNum;

    /**
     * 充电时段费率
     */
    @ApiModelProperty(value = "充电时段费率")
    private List<TimeFrameRate> cTimeFrameRate = new ArrayList<>();

    /**
     * 放电费率模板ID
     */
    @ApiModelProperty(value = "放电费率模板ID")
    private String dRateId;

    /**
     * 放电有效时段数
     */
    @ApiModelProperty(value = "放电有效时段数")
    private Integer dTimeFrameNum;

    /**
     * 放电时段费率
     */
    @ApiModelProperty(value = "放电时段费率")
    private List<TimeFrameRate> dTimeFrameRate = new ArrayList<>();

    @Data
    @ApiModel(value = "计费时段费率")
    public static class TimeFrameRate {

        /**
         * 时段开始时间
         */
        @ApiModelProperty(value = "时段开始时间")
        private Long startTime;

        /**
         * 时段结束时间
         */
        @ApiModelProperty(value = "时段结束时间")
        private Long endTime;

        /**
         * 时段电价
         */
        @ApiModelProperty(value = "时段电价")
        private Long price;

        /**
         * 服务费
         */
        @ApiModelProperty(value = "时段服务费")
        private Long serviceCharger;

        /**
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时
         */
        @ApiModelProperty(value = "时段类型 1-尖时 2-峰时 3-平时 4-谷时")
        private Integer type;
    }

}
