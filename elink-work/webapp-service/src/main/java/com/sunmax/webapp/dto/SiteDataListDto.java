package com.sunmax.webapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "找桩站点查询参数")
public class SiteDataListDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Schema(description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 快充空闲数量
     */
    @Schema(description = "快充空闲数量")
    private Integer fastIdleNum;

    /**
     * 快充总数量
     */
    @Schema(description = "快充总数量")
    private Integer fastTotalNum;

    /**
     * 慢充空闲数量
     */
    @Schema(description = "慢充空闲数量")
    private Integer slowIdleNum;

    /**
     * 慢充总数量
     */
    @Schema(description = "慢充总数量")
    private Integer slowTotalNum;

    /**
     * 距离
     */
    @Schema(description = "距离 km")
    private Double distance;

    /**
     * 停车费用类型 0-免费停车,1-不免费,2-限时免费,3-充电限免
     */
    @Schema(description = "停车费用类型 0-免费停车,1-不免费,2-限时免费,3-充电限免")
    private int parkCostType;

    /**
     * 充电价格
     */
    @Schema(description = "充电价格")
    private BigDecimal chargePrice;

    /**
     * 位置信息
     */
    @Schema(description = "位置信息")
    private String location;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double stationLng;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double stationLat;
}
