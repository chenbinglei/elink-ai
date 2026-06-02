package com.sunmax.together.vo.energy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 策略新增参数实体类
 */
@Data
@ApiModel(value = "StrategySaveVo", description = "策略新增参数实体类")
public class StrategySaveVo {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 策略模板id
     */
    @ApiModelProperty(value = "策略模板id", required = true)
    private String templateId;

    /**
     * 策略名称
     */
    @ApiModelProperty(value = "策略名称", required = true)
    private String strategyName;

    /**
     * 类型 1-边缘网关 2-云网关 3-云平台
     */
    @ApiModelProperty(value = "类型 1-边缘网关 2-云网关 3-云平台", required = true)
    private Integer strategyType;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

}
