package com.sunmax.together.dto.monitor.centralMonitor;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "AlarmDeviceCountDto", description = "告警设备统计返回实体类")
public class AlarmDeviceCountDto {

    /**
     * 告警趋势数据
     */
    @ApiModelProperty(value = "告警趋势数据(日期->告警数量)")
    private Map<String, Long> alarmTrendMap = Maps.newTreeMap();

    /**
     * 散点分析数据
     */
    @ApiModelProperty(value = "散点分析数据(告警频次 -> 告警数据)")
    private Map<Integer, List<ScatterDto>> scatterMap = Maps.newTreeMap();

    /**
     * 告警级别数量列表
     */
    @ApiModelProperty(value = "告警级别数量列表")
    private List<AlarmLevelDto> alarmLevelList = Lists.newArrayList();

    /**
     * 高频告警列表
     */
    @ApiModelProperty(value = "高频告警列表")
    private List<HighFrequencyDto> highFrequencyAlarmList = Lists.newArrayList();

    /**
     * 累计时长较高的告警列表
     */
    @ApiModelProperty(value = "累计时长较高的告警列表")
    private List<ScatterDto> accDurationAlarmList = Lists.newArrayList();


    @Data
    @ApiModel(value = "ScatterDto", description = "散点分析数据返回实体类")
    public static class ScatterDto {

        /**
         * 事件名称
         */
        @ApiModelProperty(value = "事件名称")
        private String eventName;

        /**
         * 告警频次
         */
        @ApiModelProperty(value = "告警频次")
        private Integer alarmFrequency;

        /**
         * 累计时长
         */
        @ApiModelProperty(value = "累计时长")
        private Double totalDuration;

    }

    @Data
    @ApiModel(value = "AlarmLevelDto", description = "告警数量返回实体类")
    public static class AlarmLevelDto {

        /**
         * 告警级别
         */
        @ApiModelProperty(value = "告警级别")
        private Integer alarmLevel;

        /**
         * 告警数量
         */
        @ApiModelProperty(value = "告警数量")
        private Long alarmCount;

    }

    @Data
    @ApiModel(value = "HighFrequencyDto", description = "高频告警列表")
    public static class HighFrequencyDto {

        /**
         * 事件名称
         */
        @ApiModelProperty(value = "事件名称")
        private String eventName;

        /**
         * 告警数量
         */
        @ApiModelProperty(value = "告警数量")
        private Long alarmCount;

        /**
         * 占比
         */
        @ApiModelProperty(value = "占比")
        private Integer proportion;

    }

}
