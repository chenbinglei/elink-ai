package com.sunmax.together.vo.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InverterReportQueryVo", description = "逆变器报表查询参数")
public class InverterReportQueryVo {

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
     * 多个设备id
     */
    @ApiModelProperty(value = "多个设备id(['aaa','bbb'])", required = true)
    private String deviceIds;

    /**
     * 时间维度 1-日 2-月 3-年
     */
    @ApiModelProperty(value = "时间维度 1-日 2-月 3-年", required = true)
    private Integer dateType;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private String endTime;

    /**
     * 组串容量排序 0-升序 1-降序
     */
    @ApiModelProperty(value = "组串容量排序 0-升序 1-降序")
    private Integer seriesCapacitySort;

    /**
     * 发电量排序 0-升序 1-降序
     */
    @ApiModelProperty(value = "发电量排序 0-升序 1-降序")
    private Integer generateQtSort;

    /**
     * 累计发电量排序 0-升序 1-降序
     */
    @ApiModelProperty(value = "累计发电量排序 0-升序 1-降序")
    private Integer sumGenerateQtSort;

    /**
     * 等价发电小时排序 0-升序 1-降序
     */
    @ApiModelProperty(value = "等价发电小时排序 0-升序 1-降序")
    private Integer equivGeneHourSort;

    /**
     * 峰值交流功率排序 0-升序 1-降序
     */
    @ApiModelProperty(value = "峰值交流功率排序 0-升序 1-降序")
    private Integer peakAcPowerSort;

    /**
     * 并网时长排序 0-升序 1-降序
     */
    @ApiModelProperty(value = "并网时长排序 0-升序 1-降序")
    private Integer gridHourSort;

    /**
     * 限电损失电量排序 0-升序 1-降序
     */
    @ApiModelProperty(value = "限电损失电量排序 0-升序 1-降序")
    private Integer rationLossQtSort;

    /**
     * 离散率排序 0-升序 1-降序
     */
    @ApiModelProperty(value = "离散率排序 0-升序 1-降序")
    private Integer discRateSort;
}
