package com.sunmax.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点编辑参数实体类")
public class SiteChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 所属租户id(拥有者企业)
     */
    @Schema(description = "所属租户id(拥有者企业)")
    private String tenantId;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 业主单位
     */
    @Schema(description = "业主单位")
    private String ownerUnit;

    /**
     * 运营单位
     */
    @Schema(description = "运营单位")
    private String operateUnit;

    /**
     * 投运时间
     */
    @Schema(description = "投运时间")
    private String operationDate;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Schema(description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 站点描述
     */
    @Schema(description = "站点描述")
    private String siteDescribe;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private String latitude;

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
     * 联系人
     */
    @Schema(description = "联系人")
    private String contacts;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String phone;

    /**
     * 站点图片路径
     */
    @Schema(description = "站点图片路径")
    private String imagePath;

    /**
     * 删除站点图片路径
     */
    @Schema(description = "删除站点图片路径")
    private String deleteImagePath;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Schema(description = "伪删除状态 1-正常 2-删除")
    private Integer isDelete;

    /**
     * 开放类型 1-对外开放；2-专用站点
     */
    @Schema(description = "开放类型 1-对外开放；2-专用站点")
    private Integer openType;

    /**
     * 营业时间
     */
    @Schema(description = "营业时间")
    private String businessHours;

    /**
     * 停车费用描述
     */
    @Schema(description = "停车费用描述")
    private String parkCostDesc;

    /**
     * 建筑场所：1-居民区；2-公共机构；3-企事业单位；4-写字楼；5-工业园区；6-交通枢纽；7-大型文体设施；8城市绿地；9-大型建筑配建停车场；10-路边停车位；11-城际高速服务区；12-国省道路沿线；13-城际快速公路沿线；14-其他
     */
    @Schema(description = "建筑场所：1-居民区；2-公共机构；3-企事业单位；4-写字楼；5-工业园区；6-交通枢纽；7-大型文体设施；8城市绿地；9-大型建筑配建停车场；10-路边停车位；11-城际高速服务区；12-国省道路沿线；13-城际快速公路沿线；14-其他")
    private Integer buildSite;
}
