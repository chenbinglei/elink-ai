package com.sunmax.crontab.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点补录添加入参
 */
@Data
@ApiModel(value = "NodeAddRecordVo", description = "节点补录添加入参")
public class NodeAddRecordChangeVo {

    /**
     * 当前用户id
     */
    @ApiModelProperty(value = "当前用户id", required = true)
    private String userId;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true)
    private String deviceId;

    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id", required = true)
    private String nodeId;

    /**
     * 节点存储id
     */
    @ApiModelProperty(value = "节点存储id", required = true)
    private String storageId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private String endTime;

    /**
     * 补录状态 1-执行中 2-已完成
     */
    @ApiModelProperty(value = "补录状态 1-执行中 2-已完成", required = true)
    private Integer addRecordState;
}
