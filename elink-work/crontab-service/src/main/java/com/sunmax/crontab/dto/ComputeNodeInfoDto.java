package com.sunmax.crontab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 计算节点返回信息实体类
 */
@Data
@Schema(description = "计算节点信息返回实体类")
public class ComputeNodeInfoDto {

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
     * 单位
     */
    @Schema(description = "单位")
    private String unit;

    /**
     * 策略类型 1-每次存储 2-变化存储 3-不存储
     */
    @Schema(description = "策略类型 1-每次存储 2-变化存储 3-不存储")
    private Integer strategyType;

    /**
     * 计算字段
     */
    @Schema(description = "计算字段")
    private String computeField;

    /**
     * 计算公式-前端用
     */
    @Schema(description = "计算公式-前端用")
    private String formulaFront;

    /**
     * 计算周期 年-y 月-n 日-d 时-h 分-m
     */
    @Schema(description = "计算周期 年-y 月-n 日-d 时-h 分-m")
    private String computePeriod;

    /**
     * 统计周期 自然年-y 自然月-n 日-d 时-h 分-m
     */
    @Schema(description = "统计周期 自然年-y 自然月-n 日-d 时-h 分-m")
    private String countPeriod;

    /**
     * 计算周期(cron表达式)
     */
    @Schema(description = "计算周期(cron表达式)")
    private String cronExpression;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 节点参数列表
     */
    @Schema(description = "节点参数列表")
    private List<NodeParamInfo> nodeParamInfoList;

    /**
     * 节点参数信息
     */
    @Data
    public static class NodeParamInfo {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 参数类型 1-功能点 2-节点
         */
        @Schema(description = "参数类型 1-功能点 2-节点")
        private Integer paramType;

        /**
         * 设备/站点/节点id
         */
        @Schema(description = "设备/站点id")
        private String deviceId;

        /**
         * 参数名称
         */
        @Schema(description = "参数名称")
        private String paramName;

        /**
         * 来源标识(量测类型-量测标识，节点类型-节点存储id)
         */
        @Schema(description = "来源标识(量测类型-量测标识，节点类型-节点存储id)")
        private String sourceCode;

        /**
         * 来源id
         */
        @Schema(description = "来源id")
        private String sourceId;

        /**
         * 索引号
         */
        @Schema(description = "索引号")
        private Integer indexNum;

        /**
         * 最大值
         */
        @Schema(description = "最大值")
        private Long maxValue;

        /**
         * 最小值
         */
        @Schema(description = "最小值")
        private Long minValue;

        /**
         * 缺省值
         */
        @Schema(description = "缺省值")
        private Double defaultValue;

        /**
         * 索引是否可修改 0-是 1-否
         */
        @Schema(description = "索引是否可修改 0-是 1-否")
        private Integer isIndexDisabled;
    }
}
