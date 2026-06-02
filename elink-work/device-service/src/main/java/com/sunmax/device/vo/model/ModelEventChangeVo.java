package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModelEventChangeVo", description = "模型事件编辑参数实体类")
public class ModelEventChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联模型表id
     */
    @ApiModelProperty(value = "关联模型表id", required = true)
    private String modelId;

    /**
     * 事件名称
     */
    @ApiModelProperty(value = "事件名称", required = true)
    private String eventName;

    /**
     * 事件级别 1-普通告警 2-重要告警 3-紧急告警
     */
    @ApiModelProperty(value = "事件级别 1-普通告警 2-重要告警 3-紧急告警", required = true)
    private Integer eventLevel;

    /**
     * 计算类型 1-值运算 2-位运算
     */
    @ApiModelProperty(value = "计算类型 1-值运算 2-位运算", required = true)
    private Integer calculateType;

    /**
     * 多个功能点标识
     */
    @ApiModelProperty(value = "多个功能点标识 例如['111','222']", required = true)
    private String functionLogos;

    /**
     * 存储数据
     */
    @ApiModelProperty(value = "存储数据", required = true)
    private String storeData;

    /**
     * 展示数据
     */
    @ApiModelProperty(value = "展示数据")
    private String showData;

    /**
     * 是否允许解除 1-允许 2-不允许
     */
    @ApiModelProperty(value = "是否允许解除 1-允许 2-不允许", required = true)
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
