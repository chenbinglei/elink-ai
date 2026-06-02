package com.sunmax.device.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceQueryVo", description = "设备查询条件参数")
public class DeviceQueryVo {

    /**
     * 关键词(设备ID+设备名称)
     */
    @ApiModelProperty(value = "关键词(设备ID+设备名称)")
    private String keyword;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    public String deviceNumber;

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    public String modelId;

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id", required = true)
    private String siteIds;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @ApiModelProperty(value = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    public Integer accessType;

    /**
     * 通信状态 0-未注册 1-在线 2-故障 88-离线
     */
    @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-故障 88-离线")
    public Integer txStatus;

    /**
     * 告警状态 1-无告警 2-有告警
     */
    @ApiModelProperty(value = "告警状态 1-无告警 2-有告警")
    private Integer alarmStatus;

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id")
    private Integer typeId;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

}
