package com.sunmax.device.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备编辑参数实体类")
public class DeviceChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 资产分类id
     */
    @Schema(description = "资产分类id")
    private String typeId;

    /**
     * 模型id
     */
    @Schema(description = "模型id")
    private String modelId;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;

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
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @Schema(description = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @Schema(description = "运营状态 0-未知 1-投运 2-检修 3-退役")
    private Integer operateStatus;

    /**
     * 设备描述
     */
    @Schema(description = "设备描述")
    private String deviceDesc;

    /**
     * 读写数据对象
     */
    @Schema(description = "读写数据对象")
    private String readwriteObject;

    /**
     * 删除图片路径
     */
    @Schema(description = "删除图片路径")
    private String deleteImagePath;

}
