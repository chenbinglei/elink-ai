package com.sunmax.crontab.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 计算节点列表查询实体类
 */
@Data
@ApiModel(value = "ComputeNodeListVo", description = "计算节点列表查询信息参数")
public class ComputeNodeListVo {

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

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true)
    private String deviceId;

    /**
     * 关键字
     */
    @ApiModelProperty("关键字")
    private String keyword;

    /**
     * 关键字类型 1-节点名称 2-节点标识
     */
    @ApiModelProperty(value = "关键字类型 1-节点名称 2-节点标识")
    private Integer keywordType;

    /**
     * 策略类型 1-每次存储 2-变化存储 3-不存储
     */
    @ApiModelProperty("策略类型 1-每次存储 2-变化存储 3-不存储")
    private Integer strategyType;
}
