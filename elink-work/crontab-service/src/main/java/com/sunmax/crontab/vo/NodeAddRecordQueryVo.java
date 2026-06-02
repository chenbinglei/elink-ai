package com.sunmax.crontab.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点补录列表查询入参
 */
@Data
@ApiModel(value = "NodeAddRecordQueryVo", description = "节点补录列表查询入参")
public class NodeAddRecordQueryVo {

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
     * 关键字类型 1-名称 2-编码
     */
    @ApiModelProperty(value = "关键字类型 1-名称 2-编码")
    private Integer keyType;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyValue;
}
