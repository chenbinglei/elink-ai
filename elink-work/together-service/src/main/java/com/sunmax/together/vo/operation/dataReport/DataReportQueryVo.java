package com.sunmax.together.vo.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DataReportQueryVo", description = "数据报表查询参数")
public class DataReportQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数(传0不分页，返回所有列表)", required = true)
    private Integer size;

    /**
     *
     * 报表模式 0-充电 1-放电
     */
    @ApiModelProperty(value = "报表模式 0-充电 1-放电")
    private Integer runMode;

    /**
     * 区域类型 1-省级 2-市级
     */
    @ApiModelProperty(value = "区域类型 1-省级 2-市级")
    private Integer areaType;

    /**
     * 区域
     */
    @ApiModelProperty(value = "区域")
    private String area;

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id", required = true)
    private String siteIds;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private String accountId;

    /**
     * 渠道来源标识
     */
    @ApiModelProperty(value = "渠道来源标识")
    private String platformLogo;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 电桩编码
     */
    @ApiModelProperty(value = "电桩编码")
    private String pileCode;

    /**
     * 电桩类型 28-交流 29-直流 30-V2G
     */
    @ApiModelProperty("电桩类型 28-交流 29-直流 30-V2G")
    private String pileType;

    /**
     * 时间利用率排序 0-升序 1-降序
     */
    @ApiModelProperty("时间利用率排序 0-升序 1-降序")
    private Integer timeUtilizeSort;

    /**
     * 功率利用率排序 0-升序 1-降序
     */
    @ApiModelProperty("功率利用率排序 0-升序 1-降序")
    private Integer powerUtilizeSort;

    /**
     * 枪均电量排序 0-升序 1-降序
     */
    @ApiModelProperty("枪均电量排序 0-升序 1-降序")
    private Integer gunAvgQtSort;

    /**
     * 充电时长排序 0-升序 1-降序
     */
    @ApiModelProperty("充电时长排序 0-升序 1-降序")
    private Integer chargeDurationSort;

    /**
     * 放电时长排序 0-升序 1-降序
     */
    @ApiModelProperty("放电时长排序 0-升序 1-降序")
    private Integer dischargeDurationSort;

    /**
     * 日均充电量排序 0-升序 1-降序
     */
    @ApiModelProperty("日均充电量排序 0-升序 1-降序")
    private Integer dayAvgQtSort;
}
