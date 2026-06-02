package com.sunmax.common.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "InterflowDeviceVo", description = "互联互通设备编辑参数")
public class InterflowDeviceVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 资产分类id
     */
    @ApiModelProperty(value = "资产分类id")
    private String typeId;

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    private String modelId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

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
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @ApiModelProperty(value = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @ApiModelProperty(value = "运营状态 0-未知 1-投运 2-检修 3-退役")
    private Integer operateStatus;

    /**
     * 读写数据对象
     */
    @ApiModelProperty(value = "读写数据对象")
    private String readwriteObject;

    /**
     * 伪删除状态 1-正常 2-已删除
     */
    @ApiModelProperty(value = "伪删除状态 1-正常 2-已删除")
    private Integer isDelete;

    /**
     * 设备接口信息
     */
    @ApiModelProperty(value = "设备接口信息")
    private List<InterflowGunVo> interflowGunVoList;
}
