package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "HistoryDto", description = "曲线返回实体类")
public class HistoryDto {

    /**
     * 时间
     */
    @ApiModelProperty(value = "时间")
    private String time;

    /**
     * 值
     */
    @ApiModelProperty(value = "值")
    private Object value;

}
