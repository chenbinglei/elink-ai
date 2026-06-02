package com.sunmax.crontab.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DataQueryVo", description = "数据查询信息参数")
public class DataQueryVo {

    /**
     * 多个功能点标识
     */
    @ApiModelProperty(value = "多个功能点标识")
    private String functionLogos;

    /**
     * 多个数组类型功能点标识
     */
    @ApiModelProperty(value = "多个数组类型功能点标识")
    private String arrayFunctionLogos;

    /**
     * 多个计算节点唯一id
     */
    @ApiModelProperty(value = "多个计算节点唯一id")
    private String nodeIds;

    /**
     * 所选设备id
     */
    @ApiModelProperty(value = "所选设备id", required = true)
    private String deviceId;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String endTime;

    /**
     * 索引
     */
    @ApiModelProperty(value = "索引")
    private Integer index;
}
