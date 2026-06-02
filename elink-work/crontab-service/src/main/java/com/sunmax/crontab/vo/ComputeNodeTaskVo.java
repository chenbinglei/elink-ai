package com.sunmax.crontab.vo;

import com.sunmax.crontab.dto.NodeParamInfoDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "ComputeNodeTaskVo", description = "计算节点定时任务信息参数")
public class ComputeNodeTaskVo {

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private String id;

    /**
     * 存储id
     */
    @ApiModelProperty("存储id")
    private Long storageId;

    /**
     * 节点编码
     */
    @ApiModelProperty("节点编码")
    private String nodeCode;

    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private String deviceId;

    /**
     * 所属站点id
     */
    @ApiModelProperty("所属站点id")
    private String siteId;

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;

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
     * 计算公式-后端用
     */
    @ApiModelProperty("计算公式-后端用")
    private String formulaAfter;

    /**
     * 计算周期 年-y 月-n 日-d 时-h 分-m
     */
    @ApiModelProperty(value = "计算周期 年-y 月-n 日-d 时-h 分-m")
    private String computePeriod;

    /**
     * 统计周期 自然年-y 自然月-n 日-d 时-h 分-m
     */
    @ApiModelProperty("统计周期 自然年-y 自然月-n 日-d 时-h 分-m")
    private String countPeriod;

    /**
     * 统计周期(cron表达式)
     */
    @ApiModelProperty("计算周期(cron表达式)")
    private String cronExpression;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 任务来源 1-新建节点 2-服务启动/编辑
     */
    @ApiModelProperty("任务来源 1-新建节点 2-服务启动/编辑")
    private Integer taskSource;

    /**
     * 节点参数列表
     */
    @ApiModelProperty("节点参数列表")
    private List<NodeParamInfoDto> nodeParamInfoList;
}
