package com.sunmax.protocol.dto.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 电桩费率返回实体类
 */
@Data
@Schema(description = "电桩费率返回实体类")
public class PlatformPileRateDto {

    /**
     * 充电费率模板ID
     */
    @Schema(description = "充电费率模板ID")
    private String cRateId;

    /**
     * 充电有效时段数
     */
    @Schema(description = "充电有效时段数")
    private Integer cTimeFrameNum;

    /**
     * 充电时段费率
     */
    @Schema(description = "充电时段费率")
    private List<TimeFrameRate> cTimeFrameRate = new ArrayList<>();

    /**
     * 放电费率模板ID
     */
    @Schema(description = "放电费率模板ID")
    private String dRateId;

    /**
     * 放电有效时段数
     */
    @Schema(description = "放电有效时段数")
    private Integer dTimeFrameNum;

    /**
     * 放电时段费率
     */
    @Schema(description = "放电时段费率")
    private List<TimeFrameRate> dTimeFrameRate = new ArrayList<>();

    @Data
    @Schema(description = "计费时段费率")
    public static class TimeFrameRate {

        /**
         * 时段开始时间
         */
        @Schema(description = "时段开始时间")
        private String startTime;

        /**
         * 时段结束时间
         */
        @Schema(description = "时段结束时间")
        private String endTime;

        /**
         * 时段电价
         */
        @Schema(description = "时段电价")
        private Double price;

        /**
         * 服务费
         */
        @Schema(description = "时段服务费")
        private Double serviceCharger;

        /**
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时
         */
        @Schema(description = "时段类型 1-尖时 2-峰时 3-平时 4-谷时")
        private Integer type;
    }

}
