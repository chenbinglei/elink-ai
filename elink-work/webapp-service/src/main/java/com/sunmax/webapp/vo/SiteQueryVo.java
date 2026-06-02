package com.sunmax.webapp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteQueryVo", description = "找桩站点查询参数")
public class SiteQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private int page;

    /**
     * 当前页条数(传0返回全部不分页列表)
     */
    @ApiModelProperty(value = "当前页条数(传0返回全部不分页列表)", required = true)
    private int size;

    /**
     * 当前经度
     */
    @ApiModelProperty(value = "当前经度", required = true)
    private Double centerLon;

    /**
     * 当前纬度
     */
    @ApiModelProperty(value = "当前纬度", required = true)
    private Double centerLat;

    /**
     * 充电方式 29-快充(直流充电桩) 28-慢充(交流充电桩) 30-V2G(V2G充电桩)
     */
    @ApiModelProperty("充电方式 29-快充 28-慢充 30-V2G")
    private Integer chargeMode;

    /**
     * 距我多少公里内
     */
    @ApiModelProperty("距我多少公里内")
    private Double howMuchKilometer;

    /**
     * 停车费类型 0-免费停车,1-停车收费,2-限时免费,3-充电限免
     */
    @ApiModelProperty("停车费类型 0-免费停车,1-停车收费,2-限时免费,3-充电限免")
    private Integer parkCostType;

    /**
     * 只看有空闲 1-是
     */
    @ApiModelProperty("只看有空闲 1-是")
    private Integer isIdle;

    /**
     * 查询类型 1-距离近 2-价格低
     */
    @ApiModelProperty("查询类型 1-距离近 2-价格低")
    private Integer queryType;

    /**
     * 登录标识
     */
    @ApiModelProperty(value = "登录标识", required = true)
    private String appletKey;
}