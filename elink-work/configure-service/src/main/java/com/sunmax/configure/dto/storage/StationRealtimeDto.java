package com.sunmax.configure.dto.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "推送储能平台实时数据实体类")
public class StationRealtimeDto {

    /**
     * 项目编号
     */
    @Schema(description = "项目编号")
    private String stationNo;

    /**
     * 数据时间 时间戳（timestamp），毫秒级
     */
    @Schema(description = "数据时间")
    private Long dataTime;

    /**
     * 实时状态 000-待机 101-充电 110-放电 001-故障 011-停机
     */
    @Schema(description = "实时状态 000-待机 101-充电 110-放电 001-故障 011-停机")
    private String stationStatus;

    /**
     * 实时功率 单位：kW，精度：4位小数【正充负放】
     */
    @Schema(description = "实时功率")
    private Float activePower;

    /**
     * SOC值 单位：%，精度：4位小数
     */
    @Schema(description = "SOC值")
    private Float valueSOC;

    /**
     * 数据来源 00-实时数据 01-补传数据 10-工单重传数据
     */
    @Schema(description = "数据来源 00-实时数据 01-补传数据 10-工单重传数据")
    private String dataType;

    /**
     * 工单编号(dataType=10时为必填项)
     */
    @Schema(description = "工单编号(dataType=10时为必填项)")
    private String orderNo;

}
