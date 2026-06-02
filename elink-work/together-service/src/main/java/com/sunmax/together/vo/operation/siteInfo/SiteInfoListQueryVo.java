package com.sunmax.together.vo.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteInfoListQueryVo", description = "站点信息列表查询参数")
public class SiteInfoListQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
     */
    @ApiModelProperty(value = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电", required = true)
    private Integer scenarioTypes;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @ApiModelProperty(value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

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
     * 关键字类型 1-站点名称
     */
    @ApiModelProperty(value = "关键字类型 1-站点名称")
    private Integer keywordType;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;

    /**
     * 全天开放 0-否 1-是
     */
    @ApiModelProperty(value = "全天开放 0-否 1-是")
    private Integer openAllDay;

    /**
     * 建设场所
     * 1： 居民区
     * 2： 公共机构
     * 3： 企事业单位
     * 4： 写字楼
     * 5： 工业园区
     * 6： 交通枢纽
     * 7： 大型文体设施
     * 8： 城市绿地
     * 9：大型建筑配建停车场
     * 10： 路边停车位
     * 11： 城际高速服务区
     * 12： 风景区
     * 13： 公交场站
     * 14： 加油加气站
     * 15： 出租车
     * 255： 其他
     */
    @ApiModelProperty(value = "建设场所 1-居民区 2-公共机构 3-企事业单位 4-写字楼 5-工业园区 6-交通枢纽 7-大型文体设施 8-城市绿地 9-大型建筑配建停车场 10-路边停车位 11-城际高速服务区 12-风景区 13-公交场站 14-加油加气站 15-出租车 16-其他")
    private Integer construction;

}
