package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 点表编辑参数
 */
@Data
@ApiModel("pointTableChangeVo")
public class PointTableChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 通道id
     */
    @ApiModelProperty(value = "通道id", required = true)
    private String channelId;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true)
    private String deviceId;

    /**
     * 功能点id
     */
    @ApiModelProperty(value = "功能点id", required = true)
    private String functionId;

    /**
     * 功能点下标
     */
    @ApiModelProperty(value = "功能点下标")
    private Integer functionIndex;

    /**
     * 数据点号
     */
    @ApiModelProperty(value = "数据点号", required = true)
    private Long dataId;

    /**
     * 系数
     */
    @ApiModelProperty(value = "系数", required = true)
    private Float coefficient;

    /**
     * 偏移量
     */
    @ApiModelProperty(value = "偏移量", required = true)
    private Integer offset;

    /**
     * 编辑类型 1-新增 2-编辑 3-删除
     */
    @ApiModelProperty(value = "编辑类型 1-新增 2-编辑 3-删除", required = true)
    private Integer updateType;

}
