package com.sunmax.together.dto.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "逆变器报表数据返回实体类")
public class InverterReportInfoDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 组串容量
     */
    @Schema(description = "组串容量")
    private Double seriesCapacity = 0.0;

    /**
     * 发电量
     */
    @Schema(description = "发电量")
    private Double generateQt = 0.0;

    /**
     * 累计发电量
     */
    @Schema(description = "累计发电量")
    private Double sumGenerateQt = 0.0;

    /**
     * 等价发电小时
     */
    @Schema(description = "等价发电小时")
    private Double equivGeneHour;

    /**
     * 峰值交流功率
     */
    @Schema(description = "峰值交流功率")
    private Double peakAcPower;

    /**
     * 并网时长
     */
    @Schema(description = "并网时长")
    private Double gridHour;

    /**
     * 限电损失电量
     */
    @Schema(description = "限电损失电量")
    private Double rationLossQt;

    /**
     * 离散率
     */
    @Schema(description = "离散率")
    private Double discRate;
}
