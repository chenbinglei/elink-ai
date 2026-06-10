package com.sunmax.together.vo.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "逆变器报表查询参数")
public class InverterReportQueryVo {

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
     * 多个设备id
     */
    @Schema(description = "多个设备id(['aaa','bbb'])")
    private String deviceIds;

    /**
     * 时间维度 1-日 2-月 3-年
     */
    @Schema(description = "时间维度 1-日 2-月 3-年")
    private Integer dateType;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 组串容量排序 0-升序 1-降序
     */
    @Schema(description = "组串容量排序 0-升序 1-降序")
    private Integer seriesCapacitySort;

    /**
     * 发电量排序 0-升序 1-降序
     */
    @Schema(description = "发电量排序 0-升序 1-降序")
    private Integer generateQtSort;

    /**
     * 累计发电量排序 0-升序 1-降序
     */
    @Schema(description = "累计发电量排序 0-升序 1-降序")
    private Integer sumGenerateQtSort;

    /**
     * 等价发电小时排序 0-升序 1-降序
     */
    @Schema(description = "等价发电小时排序 0-升序 1-降序")
    private Integer equivGeneHourSort;

    /**
     * 峰值交流功率排序 0-升序 1-降序
     */
    @Schema(description = "峰值交流功率排序 0-升序 1-降序")
    private Integer peakAcPowerSort;

    /**
     * 并网时长排序 0-升序 1-降序
     */
    @Schema(description = "并网时长排序 0-升序 1-降序")
    private Integer gridHourSort;

    /**
     * 限电损失电量排序 0-升序 1-降序
     */
    @Schema(description = "限电损失电量排序 0-升序 1-降序")
    private Integer rationLossQtSort;

    /**
     * 离散率排序 0-升序 1-降序
     */
    @Schema(description = "离散率排序 0-升序 1-降序")
    private Integer discRateSort;
}
