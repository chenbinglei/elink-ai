package com.sunmax.device.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceChangeVo", description = "设备编辑参数实体类")
public class DeviceChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 资产分类id
     */
    @ApiModelProperty(value = "资产分类id", required = true)
    private String typeId;

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id", required = true)
    private String modelId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    private String parentId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称", required = true)
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @ApiModelProperty(value = "接入类型 1-直连设备 2-网关设备 3-网关子设备", required = true)
    private Integer accessType;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @ApiModelProperty(value = "运营状态 0-未知 1-投运 2-检修 3-退役", required = true)
    private Integer operateStatus;

    /**
     * 设备描述
     */
    @ApiModelProperty(value = "设备描述")
    private String deviceDesc;

    /**
     * 读写数据对象
     */
    @ApiModelProperty(value = "读写数据对象")
    private String readwriteObject;

    /**
     * 删除图片路径
     */
    @ApiModelProperty(value = "删除图片路径")
    private String deleteImagePath;

}
