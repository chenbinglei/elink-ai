package com.sunmax.together.dto.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InverterReportInfoDto", description = "逆变器报表数据返回实体类")
public class InverterReportInfoDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 组串容量
     */
    @ApiModelProperty(value = "组串容量")
    private Double seriesCapacity = 0.0;

    /**
     * 发电量
     */
    @ApiModelProperty(value = "发电量")
    private Double generateQt = 0.0;

    /**
     * 累计发电量
     */
    @ApiModelProperty(value = "累计发电量")
    private Double sumGenerateQt = 0.0;

    /**
     * 等价发电小时
     */
    @ApiModelProperty(value = "等价发电小时")
    private Double equivGeneHour;

    /**
     * 峰值交流功率
     */
    @ApiModelProperty(value = "峰值交流功率")
    private Double peakAcPower;

    /**
     * 并网时长
     */
    @ApiModelProperty(value = "并网时长")
    private Double gridHour;

    /**
     * 限电损失电量
     */
    @ApiModelProperty(value = "限电损失电量")
    private Double rationLossQt;

    /**
     * 离散率
     */
    @ApiModelProperty(value = "离散率")
    private Double discRate;
}
