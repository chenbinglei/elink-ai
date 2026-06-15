package com.sunmax.together.dto.monitor.centralMonitor;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "告警设备统计返回实体类")
public class AlarmDeviceCountDto {

    /**
     * 告警趋势数据
     */
    @Schema(description = "告警趋势数据(日期->告警数量)")
    private Map<String, Long> alarmTrendMap = Maps.newTreeMap();

    /**
     * 散点分析数据
     */
    @Schema(description = "散点分析数据(告警频次 -> 告警数据)")
    private Map<Integer, List<ScatterDto>> scatterMap = Maps.newTreeMap();

    /**
     * 告警级别数量列表
     */
    @Schema(description = "告警级别数量列表")
    private List<AlarmLevelDto> alarmLevelList = Lists.newArrayList();

    /**
     * 高频告警列表
     */
    @Schema(description = "高频告警列表")
    private List<HighFrequencyDto> highFrequencyAlarmList = Lists.newArrayList();

    /**
     * 累计时长较高的告警列表
     */
    @Schema(description = "累计时长较高的告警列表")
    private List<ScatterDto> accDurationAlarmList = Lists.newArrayList();


    @Data
    @Schema(description = "散点分析数据返回实体类")
    public static class ScatterDto {

        /**
         * 事件名称
         */
        @Schema(description = "事件名称")
        private String eventName;

        /**
         * 告警频次
         */
        @Schema(description = "告警频次")
        private Integer alarmFrequency;

        /**
         * 累计时长
         */
        @Schema(description = "累计时长")
        private Double totalDuration;

    }

    @Data
    @Schema(description = "告警数量返回实体类")
    public static class AlarmLevelDto {

        /**
         * 告警级别
         */
        @Schema(description = "告警级别")
        private Integer alarmLevel;

        /**
         * 告警数量
         */
        @Schema(description = "告警数量")
        private Long alarmCount;

    }

    @Data
    @Schema(description = "高频告警列表")
    public static class HighFrequencyDto {

        /**
         * 事件名称
         */
        @Schema(description = "事件名称")
        private String eventName;

        /**
         * 告警数量
         */
        @Schema(description = "告警数量")
        private Long alarmCount;

        /**
         * 占比
         */
        @Schema(description = "占比")
        private Integer proportion;

    }

}
