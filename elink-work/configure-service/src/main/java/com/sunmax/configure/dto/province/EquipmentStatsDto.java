package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:33
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "充电设备统计实体类")
public class EquipmentStatsDto {

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    @JSONField(name = "EquipmentID")
    private String equipmentId;

    /**
     * 设备累计电量
     */
    @Schema(description = "设备累计电量")
    @JSONField(name = "EquipmentElectricity")
    private Double equipmentElectricity;

    /**
     * 接口状态列表
     */
    @Schema(description = "接口状态列表")
    @JSONField(name = "ConnectorStatsInfos")
    private List<ConnectorStatsDto> connectorStatsDtos;
}
