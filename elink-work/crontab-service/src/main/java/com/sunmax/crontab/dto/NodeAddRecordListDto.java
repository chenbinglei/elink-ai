package com.sunmax.crontab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点数据补录列表返回实体类
 */
@Data
@Schema(description = "节点数据补录列表返回实体类")
public class NodeAddRecordListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 节点存储id
     */
    @Schema(description = "节点存储id")
    private Long storageId;

    /**
     * 节点编码
     */
    @Schema(description = "节点编码")
    private String nodeCode;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String nodeName;

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 补录状态 1-执行中 2-已完成
     */
    @Schema(description = "补录状态 1-执行中 2-已完成")
    private Integer addRecordState;
}
