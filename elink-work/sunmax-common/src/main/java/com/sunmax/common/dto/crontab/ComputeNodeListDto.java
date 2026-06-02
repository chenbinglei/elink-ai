package com.sunmax.common.dto.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 计算节点列表返回实体类
 */
@Data
@ApiModel(value = "ComputeNodeListDto", description = "计算节点列表返回实体类")
public class ComputeNodeListDto {

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
     * 站点/设备id
     */
    @ApiModelProperty("站点/设备id")
    private String deviceId;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String unit;

    /**
     * 策略类型 1-每次存储 2-变化存储 3-不存储
     */
    @ApiModelProperty("策略类型 1-每次存储 2-变化存储 3-不存储")
    private Integer strategyType;

    /**
     * 计算周期 年-y 月-n 日-d 时-h 分-m
     */
    @ApiModelProperty(value = "计算周期 年-y 月-n 日-d 时-h 分-m")
    private String computePeriod;

    /**
     * 统计周期 自然年-y 自然月-n 日-d 时-h 分-m
     */
    @ApiModelProperty(value = "统计周期 自然年-y 自然月-n 日-d 时-h 分-m")
    private String countPeriod;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
}

