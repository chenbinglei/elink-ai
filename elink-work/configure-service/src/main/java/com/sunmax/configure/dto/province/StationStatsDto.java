package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:34
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "充电站统计实体类")
public class StationStatsDto {

    /**
     * 充电站编码
     */
    @Schema(description = "充电站编码")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 开始时间(默认当天零时)
     */
    @Schema(description = "开始时间(默认当天零时)")
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 结束时间(默认当天23:59:59)
     */
    @Schema(description = "结束时间(默认当天23:59:59)")
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 累计电量
     */
    @Schema(description = "累计电量")
    @JSONField(name = "StationElectricity")
    private Double stationElectricity;

    /**
     * 充电设备统计信息列表
     */
    @Schema(description = "充电设备统计信息列表")
    @JSONField(name = "EquipmentStatsInfos")
    private List<EquipmentStatsDto> equipmentStatsInfos;
}
