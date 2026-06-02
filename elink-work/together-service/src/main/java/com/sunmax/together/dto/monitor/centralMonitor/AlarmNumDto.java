package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "AlarmNumDto", description = "告警数量返回实体类")
public class AlarmNumDto {

    /**
     * 数据id
     */
    @ApiModelProperty(value = "数据id")
    private String dataId;

    /**
     * 数据名称
     */
    @ApiModelProperty(value = "数据名称")
    private String dataName;

    /**
     * 告警数量
     */
    @ApiModelProperty(value = "告警数量")
    @Builder.Default
    private Long alarmNum = 0L;

}
