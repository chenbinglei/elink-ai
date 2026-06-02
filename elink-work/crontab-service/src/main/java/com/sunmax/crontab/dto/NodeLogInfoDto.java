package com.sunmax.crontab.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 节点日志信息参数
 */
@Data
@ApiModel(value = "NodeLogInfoDto", description = "节点日志信息返回实体类")
public class NodeLogInfoDto {


    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;


    /**
     * 节点
     */
    @ApiModelProperty(value = "节点")
    private String nodeId;

    /**
     * 存储id
     */
    @ApiModelProperty(value = "存储id")
    private Long storageId;

    /**
     * 日志类型 1-定时任务 2-数据补录
     */
    @ApiModelProperty(value = "日志类型 1-定时任务 2-数据补录")
    private Integer logType;

    /**
     * 数据点时间
     */
    @ApiModelProperty(value = "数据点时间")
    private String tsTime;

    /**
     * 日志时间
     */
    @ApiModelProperty(value = "日志时间")
    public LocalDateTime logTime;

    /**
     * 日志级别 1-error 2-warning
     */
    @ApiModelProperty(value = "日志级别 1-error 2-warning")
    private Integer logLevel;

    /**
     * 日志内容
     */
    @ApiModelProperty(value = "日志内容")
    private String logInfo;

    /**
     * 返回值是否是缺省值 1-是
     */
    @ApiModelProperty(value = "返回值是否是缺省值 1-是")
    private Integer isDefaultValue;
}
