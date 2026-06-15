package com.sunmax.webapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "找桩站点查询参数")
public class SiteQueryVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private int page;

    /**
     * 当前页条数(传0返回全部不分页列表)
     */
    @Schema(description = "当前页条数(传0返回全部不分页列表)")
    private int size;

    /**
     * 当前经度
     */
    @Schema(description = "当前经度")
    private Double centerLon;

    /**
     * 当前纬度
     */
    @Schema(description = "当前纬度")
    private Double centerLat;

    /**
     * 充电方式 29-快充(直流充电桩) 28-慢充(交流充电桩) 30-V2G(V2G充电桩)
     */
    @Schema(description = "充电方式 29-快充 28-慢充 30-V2G")
    private Integer chargeMode;

    /**
     * 距我多少公里内
     */
    @Schema(description = "距我多少公里内")
    private Double howMuchKilometer;

    /**
     * 停车费类型 0-免费停车,1-停车收费,2-限时免费,3-充电限免
     */
    @Schema(description = "停车费类型 0-免费停车,1-停车收费,2-限时免费,3-充电限免")
    private Integer parkCostType;

    /**
     * 只看有空闲 1-是
     */
    @Schema(description = "只看有空闲 1-是")
    private Integer isIdle;

    /**
     * 查询类型 1-距离近 2-价格低
     */
    @Schema(description = "查询类型 1-距离近 2-价格低")
    private Integer queryType;

    /**
     * 登录标识
     */
    @Schema(description = "登录标识")
    private String appletKey;
}
