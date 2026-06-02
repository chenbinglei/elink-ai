package com.sunmax.together.dto.energy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "GatewayDataDto", description = "网关列表返回实体类")
public class GatewayDataDto {

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
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 类型 1-边缘网关 2-云网关 3-云平台
     */
    @ApiModelProperty(value = "类型 1-边缘网关 2-云网关 3-云平台")
    private Integer strategyType;

}
