package com.sunmax.together.dto.monitor.centralMonitor;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "AlarmSiteCountDto", description = "告警站点统计返回实体类")
public class AlarmSiteCountDto {

    /**
     * 告警列表数据
     */
    @ApiModelProperty(value = "告警列表数据")
    private List<AlarmNumDto> alarmNumList = Lists.newArrayList();

    /**
     * 告警趋势数据
     */
    @ApiModelProperty(value = "告警趋势数据(日期->告警数量)")
    private Map<String, Long> alarmTrendMap = Maps.newTreeMap();

    /**
     * 告警等级统计
     */
    @ApiModelProperty(value = "告警等级统计(类型id(0-其他告警 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警)->数量)")
    private Map<Integer, Long> alarmLevelMap = Maps.newTreeMap();

    /**
     * 持续时间分布统计
     */
    @ApiModelProperty(value = "持续时间分布统计(类型id(1-(<1h) 2-(1-3h) 3-(3-12h) 4-(12-24h) 5-(24-72h) 6-(>72h))->数量)")
    private Map<Integer, Long> durationMap = Maps.newTreeMap();

}
