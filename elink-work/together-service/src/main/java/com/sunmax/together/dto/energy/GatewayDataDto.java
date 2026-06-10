package com.sunmax.together.dto.energy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "网关列表返回实体类")
public class GatewayDataDto {

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
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 类型 1-边缘网关 2-云网关 3-云平台
     */
    @Schema(description = "类型 1-边缘网关 2-云网关 3-云平台")
    private Integer strategyType;

}
