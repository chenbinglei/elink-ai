package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "DeviceStatisticsDto", description = "设备统计返回实体类")
public class SystemStatisticsDto {

    /**
     * 功能标志
     */
    @ApiModelProperty(value = "功能标志")
    private String functionLogos;

    /**
     * 故障数量
     */
    @ApiModelProperty(value = "故障数量")
    private Integer error;

    /**
     * 未注册数量
     */
    @ApiModelProperty(value = "未注册数量")
    private Integer unregistered;

    /**
     * 离线数量
     */
    @ApiModelProperty(value = "离线数量")
    private Integer offline;

    /**
     * 正常设备数量
     */
    @ApiModelProperty(value = "正常设备数量")
    private Integer normal;

    /**
     * 功率曲线列表
     */
    @ApiModelProperty(value = "功率曲线")
    private List<CurveDto> historys;

}
