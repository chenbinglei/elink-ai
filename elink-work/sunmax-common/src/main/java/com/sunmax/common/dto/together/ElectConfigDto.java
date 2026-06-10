package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "电价配置返回实体类")
public class ElectConfigDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 电价模块类型 1-电网电价 2-光伏上网电价 3-光伏消纳电价 4-储能售电电价 5-储能购电电价 6-电桩售电电价 7-电桩购电电价
     * 电网电价：关联关口表正向有功电量（尖峰平谷深）
     * 光伏上网电价：关联关口表反向有功电量（尖峰平谷深）
     * 光伏消纳电价：关联（光伏并网表总发电量-上网电量）
     * 储能售电电价：关联储能计量节点并网表反向有功电量（尖峰平谷深）
     * 储能购电电价：关联储能计量节点并网表正向有功电量（尖峰平谷深）
     * 电桩售电电价：关联电桩计量节点并网表反向有功电量（尖峰平谷深）
     * 电桩购电电价：关联电桩计量节点并网表正向有功电量（尖峰平谷深）
     */
    @Schema(description = "电价模块类型 1-电网电价 2-光伏上网电价 3-光伏消纳电价 4-储能售电电价 5-储能购电电价 6-电桩售电电价 7-电桩购电电价")
    private Integer moduleType;

    /**
     * 策略名称
     */
    @Schema(description = "策略名称")
    private String strategyName;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private String startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private String endDate;

    /**
     * 定价方式 1-全天同价 2-分时段定价
     */
    @Schema(description = "定价方式 1-全天同价 2-分时段定价")
    private Integer priceType;

    /**
     * 电价配置时段列表
     */
    @Schema(description = "电价配置时段列表")
    private List<ElectTimeFrame> electTimeFrameList = Lists.newArrayList();


    @Data
    @Schema(description = "电价配置时段返回实体类")
    public static class ElectTimeFrame {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 电价配置id
         */
        @Schema(description = "电价配置id")
        private String electConfigId;

        /**
         * 时段开始时间
         */
        @Schema(description = "时段开始时间")
        private String startTime;

        /**
         * 时段结束时间
         */
        @Schema(description = "时段结束时间")
        private String endTime;

        /**
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天
         */
        @Schema(description = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天")
        private Integer periodType;

        /**
         * 电费
         */
        @Schema(description = "电费")
        private BigDecimal electMoney;

    }

}
