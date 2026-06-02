package com.sunmax.together.dto.operation.seriesInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SeriesDeviceListDto", description = "组串设备列表返回实体类")
public class SeriesDeviceListDto {

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
     * 资产分类id 20-逆变器
     */
    @ApiModelProperty(value = "资产分类id 20-逆变器")
    private String typeId;

    /**
     * 类型名称
     */
    @ApiModelProperty(value = "类型名称")
    private String typeName;

    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号")
    private String equipmentModel;

    /**
     * 配置状态 1-未配置 2-已配置
     */
    @ApiModelProperty(value = "配置状态 1-未配置 2-已配置")
    private Integer configStatus = 1;

    /**
     * MPPT数量
     */
    @ApiModelProperty(value = "MPPT数量")
    private Integer mppt = 0;
}
