package com.sunmax.device.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModelEventDetailDto", description = "模型事件详情返回实体类")
public class ModelEventDetailDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联模型表id
     */
    @ApiModelProperty(value = "关联模型表id")
    private String modelId;

    /**
     * 事件名称
     */
    @ApiModelProperty(value = "事件名称")
    private String eventName;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 1-普通告警 2-重要告警 3-紧急告警")
    private Integer eventLevel;

    /**
     * 计算类型 1-值运算 2-位运算
     */
    @ApiModelProperty(value = "计算类型 1-值运算 2-位运算")
    private Integer calculateType;

    /**
     * 多个功能点id
     */
    @ApiModelProperty(value = "多个功能点id 例如['111','222']")
    private String functionIds;

    /**
     * 存储数据
     */
    @ApiModelProperty(value = "存储数据")
    private String storeData;

    /**
     * 展示数据
     */
    @ApiModelProperty(value = "展示数据")
    private String showData;

    /**
     * 是否允许解除 1-允许 2-不允许
     */
    @ApiModelProperty(value = "是否允许解除 1-允许 2-不允许")
    private Integer isAllow;

    /**
     * 事件通知
     */
    @ApiModelProperty(value = "事件通知")
    private String eventInform;

    /**
     * 事件描述
     */
    @ApiModelProperty(value = "事件描述")
    private String eventDesc;

}
