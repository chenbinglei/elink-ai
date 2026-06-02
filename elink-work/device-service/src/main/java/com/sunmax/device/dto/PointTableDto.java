package com.sunmax.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 点表信息返回实体类
 */
@Data
@ApiModel(value = "PointTableDto", description = "点表信息返回实体类")
public class PointTableDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 通道id
     */
    @ApiModelProperty(value = "通道id")
    private String channelId;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 功能点id
     */
    @ApiModelProperty(value = "功能点id")
    private String functionId;

    /**
     * 功能点下标
     */
    @ApiModelProperty(value = "功能点下标")
    private Integer functionIndex;

    /**
     * 功能点名称
     */
    @ApiModelProperty(value = "功能点名称")
    private String functionName;

    /**
     * 功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
     */
    @ApiModelProperty(value = "功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调")
    private Integer functionType;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @ApiModelProperty(value = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

    /**
     * 数据点号
     */
    @ApiModelProperty(value = "数据点号")
    private Long dataId;

    /**
     * 系数
     */
    @ApiModelProperty(value = "系数")
    private Float coefficient;

    /**
     * 偏移量
     */
    @ApiModelProperty(value = "偏移量")
    private Integer offset;

}
