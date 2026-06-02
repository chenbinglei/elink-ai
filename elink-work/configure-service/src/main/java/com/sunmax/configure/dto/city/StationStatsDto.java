package com.sunmax.configure.dto.city;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:34
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "StationStatsDto", description = "充电站统计实体类")
public class StationStatsDto {

    /**
     * 充电站编码
     */
    @ApiModelProperty(value = "充电站编码")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 开始时间(默认当天零时)
     */
    @ApiModelProperty(value = "开始时间(默认当天零时)")
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 结束时间(默认当天23:59:59)
     */
    @ApiModelProperty(value = "结束时间(默认当天23:59:59)")
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 累计电量
     */
    @ApiModelProperty(value = "累计电量")
    @JSONField(name = "StationElectricity")
    private Double stationElectricity;

    /**
     * 充电设备统计信息列表
     */
    @ApiModelProperty(value = "充电设备统计信息列表")
    @JSONField(name = "EquipmentStatsInfos")
    private List<EquipmentStatsDto> equipmentStatsInfos;
}
