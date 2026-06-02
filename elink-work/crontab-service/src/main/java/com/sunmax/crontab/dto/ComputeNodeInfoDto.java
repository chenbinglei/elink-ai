package com.sunmax.crontab.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 计算节点返回信息实体类
 */
@Data
@ApiModel(value = "ComputeNodeInfoDto", description = "计算节点信息返回实体类")
public class ComputeNodeInfoDto {

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
     * 计算字段
     */
    @ApiModelProperty("计算字段")
    private String computeField;

    /**
     * 计算公式-前端用
     */
    @ApiModelProperty("计算公式-前端用")
    private String formulaFront;

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
     * 计算周期(cron表达式)
     */
    @ApiModelProperty("计算周期(cron表达式)")
    private String cronExpression;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startTime;

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

    /**
     * 节点参数列表
     */
    @ApiModelProperty("节点参数列表")
    private List<NodeParamInfo> nodeParamInfoList;

    /**
     * 节点参数信息
     */
    @Data
    public static class NodeParamInfo {

        /**
         * 主键id
         */
        @ApiModelProperty("主键id")
        private String id;

        /**
         * 参数类型 1-功能点 2-节点
         */
        @ApiModelProperty("参数类型 1-功能点 2-节点")
        private Integer paramType;

        /**
         * 设备/站点/节点id
         */
        @ApiModelProperty("设备/站点id")
        private String deviceId;

        /**
         * 参数名称
         */
        @ApiModelProperty("参数名称")
        private String paramName;

        /**
         * 来源标识(量测类型-量测标识，节点类型-节点存储id)
         */
        @ApiModelProperty("来源标识(量测类型-量测标识，节点类型-节点存储id)")
        private String sourceCode;

        /**
         * 来源id
         */
        @ApiModelProperty("来源id")
        private String sourceId;

        /**
         * 索引号
         */
        @ApiModelProperty("索引号")
        private Integer indexNum;

        /**
         * 最大值
         */
        @ApiModelProperty("最大值")
        private Long maxValue;

        /**
         * 最小值
         */
        @ApiModelProperty("最小值")
        private Long minValue;

        /**
         * 缺省值
         */
        @ApiModelProperty("缺省值")
        private Double defaultValue;

        /**
         * 索引是否可修改 0-是 1-否
         */
        @ApiModelProperty("索引是否可修改 0-是 1-否")
        private Integer isIndexDisabled;
    }
}
