package com.sunmax.crontab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点补录添加入参
 */
@Data
@Schema(description = "节点补录添加入参")
public class NodeAddRecordChangeVo {

    /**
     * 当前用户id
     */
    @Schema(description = "当前用户id")
    private String userId;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 节点id
     */
    @Schema(description = "节点id")
    private String nodeId;

    /**
     * 节点存储id
     */
    @Schema(description = "节点存储id")
    private String storageId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 补录状态 1-执行中 2-已完成
     */
    @Schema(description = "补录状态 1-执行中 2-已完成")
    private Integer addRecordState;
}
