package com.sunmax.device.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "DeviceEventListDto", description = "设备事件列表返回实体类")
public class DeviceEventListDto {

    /**
     * 设备事件id
     */
    @ApiModelProperty(value = "设备事件id")
    private String id;

    /**
     * 事件名称
     */
    @ApiModelProperty(value = "事件名称")
    private String eventName;

    /**
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @ApiModelProperty(value = "事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警")
    private Integer eventLevel;

    /**
     * 多个功能点名称
     */
    @ApiModelProperty(value = "多个功能点名称")
    private String functionNames;

    /**
     * 事件描述
     */
    @ApiModelProperty(value = "事件描述")
    private String eventDesc;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @ApiModelProperty(value = "事件状态 0-未恢复 1-已修复")
    private Integer eventStatus;

    /**
     * 是否允许解除 1-允许 2-不允许
     */
    @ApiModelProperty(value = "是否允许解除 1-允许 2-不允许")
    private Integer isAllow;

    /**
     * 事件类型 1-模型事件 2-故障定义
     */
    @ApiModelProperty(value = "事件类型 1-模型事件 2-故障定义")
    private Integer type;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 编辑时间
     */
    @ApiModelProperty(value = "编辑时间")
    private String updateTime;

}
