package com.sunmax.crontab.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点数据补录列表返回实体类
 */
@Data
@ApiModel(value = "NodeAddRecordListDto", description = "节点数据补录列表返回实体类")
public class NodeAddRecordListDto {

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private String id;

    /**
     * 节点存储id
     */
    @ApiModelProperty("节点存储id")
    private Long storageId;

    /**
     * 节点编码
     */
    @ApiModelProperty("节点编码")
    private String nodeCode;

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 补录状态 1-执行中 2-已完成
     */
    @ApiModelProperty(value = "补录状态 1-执行中 2-已完成")
    private Integer addRecordState;
}
