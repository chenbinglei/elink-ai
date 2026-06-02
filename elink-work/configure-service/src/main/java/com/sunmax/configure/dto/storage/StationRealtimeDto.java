package com.sunmax.configure.dto.storage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StationRealtimeDataDto", description = "推送储能平台实时数据实体类")
public class StationRealtimeDto {

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号", required = true)
    private String stationNo;

    /**
     * 数据时间 时间戳（timestamp），毫秒级
     */
    @ApiModelProperty(value = "数据时间", required = true)
    private Long dataTime;

    /**
     * 实时状态 000-待机 101-充电 110-放电 001-故障 011-停机
     */
    @ApiModelProperty(value = "实时状态 000-待机 101-充电 110-放电 001-故障 011-停机", required = true)
    private String stationStatus;

    /**
     * 实时功率 单位：kW，精度：4位小数【正充负放】
     */
    @ApiModelProperty(value = "实时功率", required = true)
    private Float activePower;

    /**
     * SOC值 单位：%，精度：4位小数
     */
    @ApiModelProperty(value = "SOC值", required = true)
    private Float valueSOC;

    /**
     * 数据来源 00-实时数据 01-补传数据 10-工单重传数据
     */
    @ApiModelProperty(value = "数据来源 00-实时数据 01-补传数据 10-工单重传数据", required = true)
    private String dataType;

    /**
     * 工单编号(dataType=10时为必填项)
     */
    @ApiModelProperty(value = "工单编号(dataType=10时为必填项)")
    private String orderNo;

}
