package com.sunmax.device.vo.firmware;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 固件包编辑参数
 */
@Data
@ApiModel("FirmwareChangeVo")
public class FirmwareChangeVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 类型id 29-直流充电桩 30-V2G充电桩 31-边缘网关
     */
    @ApiModelProperty(value = "类型id 29-直流充电桩 30-V2G充电桩 31-边缘网关", required = true)
    private String typeId;

    /**
     * 设备型号(多选,例如'型号1','型号2')
     */
    @ApiModelProperty(value = "设备型号(多选,例如'型号1','型号2')", required = true)
    private String equipmentModels;

    /**
     * 固件包名称
     */
    @ApiModelProperty(value = "固件包名称", required = true)
    private String firmwareName;

    /**
     * 固件类型
     * 1-V2G_1.0 TCP控制板
     * 2-V2G_2.0 TCP控制板
     * 3-V2G_3.0 TCP控制板
     * 4-V2G_4.0 TPU控制板
     * 5-V2G_4.0 CCU控制板
     * 6-V2G_6.0 TCP控制板
     * 7-V2G_7.0 TPU控制板
     * 8-V2G_7.0 CCU控制板
     * 9-V2G_9.0 TCP_BOOT控制板
     * 10-V2G_10.0 TPU_BOOT控制板
     * 11-V2G_11.0 CCU_BOOT控制板
     */
    @ApiModelProperty(value = "固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_7.0 CCU控制板 9-V2G_9.0 TCP_BOOT控制板 10-V2G_10.0 TPU_BOOT控制板 11-V2G_11.0 CCU_BOOT控制板", required = true)
    private Integer firmwareType;

    /**
     * 固件版本号
     */
    @ApiModelProperty(value = "固件版本号", required = true)
    private String firmwareVersion;

    /**
     * 固件包描述
     */
    @ApiModelProperty(value = "固件包描述")
    private String firmwareDesc;

}
