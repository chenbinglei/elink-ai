package com.sunmax.common.dto.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: yqz
 * @Date: 2023/6/1916:29
 * @version: 1.0
 * @注释: 实例节点返回实体类
 */
@Data
@ApiModel(value = "NodeHistoryDataDto", description = "计算节点历史数据返回实体类")
public class NodeHistoryDataDto {

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private String ts;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private String updateTime;

    /**
     * 数据值
     */
    @ApiModelProperty("数据值")
    private Double resultValue;

    /**
     * first时间(计算节点内部使用字段)
     */
    @ApiModelProperty("first时间(计算节点内部使用字段)")
    private String firstTs;

    /**
     * first数据值(计算节点内部使用字段)
     */
    @ApiModelProperty("first数据值(计算节点内部使用字段)")
    private Double firstResultValue;
}
