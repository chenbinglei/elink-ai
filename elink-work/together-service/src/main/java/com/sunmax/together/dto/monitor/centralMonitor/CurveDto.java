package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "CurveDto", description = "曲线返回实体类")
public class CurveDto {

    /**
     * 曲线名称
     */
    @ApiModelProperty(value = "曲线名称")
    private String name;

    /**
     * 功率曲线
     */
    @ApiModelProperty(value = "功率曲线")
    private List<HistoryDto> data;
}
