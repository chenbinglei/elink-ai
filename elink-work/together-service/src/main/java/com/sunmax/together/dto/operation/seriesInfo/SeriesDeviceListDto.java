package com.sunmax.together.dto.operation.seriesInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "组串设备列表返回实体类")
public class SeriesDeviceListDto {

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
     * 资产分类id 20-逆变器
     */
    @Schema(description = "资产分类id 20-逆变器")
    private String typeId;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称")
    private String typeName;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String equipmentModel;

    /**
     * 配置状态 1-未配置 2-已配置
     */
    @Schema(description = "配置状态 1-未配置 2-已配置")
    private Integer configStatus = 1;

    /**
     * MPPT数量
     */
    @Schema(description = "MPPT数量")
    private Integer mppt = 0;
}
