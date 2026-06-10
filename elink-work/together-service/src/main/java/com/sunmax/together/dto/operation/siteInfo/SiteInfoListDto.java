package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点信息列表返回实体类")
public class SiteInfoListDto {

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点唯一id
     */
    @Schema(description = "站点唯一id")
    private String id;

    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * 运营商名称
     */
    @Schema(description = "运营商名称")
    private String operateUnitName;

    /**
     * 运营商id
     */
    @Schema(description = "运营商id")
    private String operateUnitId;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Schema(description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 所在省份
     */
    @Schema(description = "所在省份")
    private String province;

    /**
     * 所在市
     */
    @Schema(description = "所在市")
    private String city;

    /**
     * 所在区县
     */
    @Schema(description = "所在区县")
    private String county;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;

    /**
     * 全天开放 0-否 1-是
     */
    @Schema(description = "全天开放 0-否 1-是")
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
    @Schema(description = "建设场所 1-居民区 2-公共机构 3-企事业单位 4-写字楼 5-工业园区 6-交通枢纽 7-大型文体设施 8-城市绿地 9-大型建筑配建停车场 10-路边停车位 11-城际高速服务区 12-风景区 13-公交场站 14-加油加气站 15-出租车 16-其他")
    private Integer construction;
}
