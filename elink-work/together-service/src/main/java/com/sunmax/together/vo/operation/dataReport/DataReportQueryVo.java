package com.sunmax.together.vo.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据报表查询参数")
public class DataReportQueryVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数(传0不分页，返回所有列表)")
    private Integer size;

    /**
     *
     * 报表模式 0-充电 1-放电
     */
    @Schema(description = "报表模式 0-充电 1-放电")
    private Integer runMode;

    /**
     * 区域类型 1-省级 2-市级
     */
    @Schema(description = "区域类型 1-省级 2-市级")
    private Integer areaType;

    /**
     * 区域
     */
    @Schema(description = "区域")
    private String area;

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 商户id
     */
    @Schema(description = "商户id")
    private String accountId;

    /**
     * 渠道来源标识
     */
    @Schema(description = "渠道来源标识")
    private String platformLogo;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 电桩编码
     */
    @Schema(description = "电桩编码")
    private String pileCode;

    /**
     * 电桩类型 28-交流 29-直流 30-V2G
     */
    @Schema(description = "电桩类型 28-交流 29-直流 30-V2G")
    private String pileType;

    /**
     * 时间利用率排序 0-升序 1-降序
     */
    @Schema(description = "时间利用率排序 0-升序 1-降序")
    private Integer timeUtilizeSort;

    /**
     * 功率利用率排序 0-升序 1-降序
     */
    @Schema(description = "功率利用率排序 0-升序 1-降序")
    private Integer powerUtilizeSort;

    /**
     * 枪均电量排序 0-升序 1-降序
     */
    @Schema(description = "枪均电量排序 0-升序 1-降序")
    private Integer gunAvgQtSort;

    /**
     * 充电时长排序 0-升序 1-降序
     */
    @Schema(description = "充电时长排序 0-升序 1-降序")
    private Integer chargeDurationSort;

    /**
     * 放电时长排序 0-升序 1-降序
     */
    @Schema(description = "放电时长排序 0-升序 1-降序")
    private Integer dischargeDurationSort;

    /**
     * 日均充电量排序 0-升序 1-降序
     */
    @Schema(description = "日均充电量排序 0-升序 1-降序")
    private Integer dayAvgQtSort;
}
