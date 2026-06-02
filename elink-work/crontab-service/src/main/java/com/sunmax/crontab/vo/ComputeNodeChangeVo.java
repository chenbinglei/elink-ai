package com.sunmax.crontab.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ComputeNodeChangeVo", description = "计算节点编辑信息参数")
public class ComputeNodeChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private String id;

    /**
     * 站点/设备id
     */
    @ApiModelProperty(value = "站点/设备id", required = true)
    private String deviceId;

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id", required = true)
    private String siteId;

    /**
     * 当前用户id
     */
    @ApiModelProperty(value = "当前用户id", required = true)
    private String userId;

    /**
     * 节点编码
     */
    @ApiModelProperty(value = "节点编码", required = true)
    private String nodeCode;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称", required = true)
    private String nodeName;

    /**
     * 实例类型 1-设备类型 2-站点类型
     */
    @ApiModelProperty(value = "实例类型 1-设备类型 2-站点类型", required = true)
    private Integer exampleType;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String unit;

    /**
     * 策略类型 1-每次存储 2-变化存储 3-不存储
     */
    @ApiModelProperty(value = "策略类型 1-每次存储 2-变化存储 3-不存储", required = true)
    private Integer strategyType;

    /**
     * 计算公式-前端用
     */
    @ApiModelProperty(value = "计算公式-前端用", required = true)
    private String formulaFront;

    /**
     * 计算公式-后端用
     */
    @ApiModelProperty(value = "计算公式-后端用", required = true)
    private String formulaAfter;

    /**
     * 计算周期 年-y 月-n 日-d 时-h 分-m
     */
    @ApiModelProperty(value = "计算周期 年-y 月-n 日-d 时-h 分-m", required = true)
    private String computePeriod;

    /**
     * 统计周期 自然年-y 自然月-n 日-d 时-h 分-m
     */
    @ApiModelProperty(value = "统计周期 自然年-y 自然月-n 日-d 时-h 分-m", required = true)
    private String countPeriod;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private String startTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 节点参数列表
     */
    @ApiModelProperty("节点参数列表")
    private String nodeParamInfos;

    /**
     * 节点参数信息
     */
    @Data
    public static class NodeParamInfo {

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
