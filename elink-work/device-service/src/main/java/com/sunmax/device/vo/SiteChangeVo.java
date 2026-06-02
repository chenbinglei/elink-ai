package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteChangeVo", description = "站点编辑参数实体类")
public class SiteChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 所属租户id(拥有者企业)
     */
    @ApiModelProperty(value = "所属租户id(拥有者企业)", required = true)
    private String tenantId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称", required = true)
    private String siteName;

    /**
     * 业主单位
     */
    @ApiModelProperty(value = "业主单位", required = true)
    private String ownerUnit;

    /**
     * 运营单位
     */
    @ApiModelProperty(value = "运营单位", required = true)
    private String operateUnit;

    /**
     * 投运时间
     */
    @ApiModelProperty(value = "投运时间")
    private String operationDate;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @ApiModelProperty(value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中", required = true)
    private Integer siteStatus;

    /**
     * 站点描述
     */
    @ApiModelProperty(value = "站点描述")
    private String siteDescribe;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String latitude;

    /**
     * 所在省份
     */
    @ApiModelProperty(value = "所在省份")
    private String province;

    /**
     * 所在市
     */
    @ApiModelProperty(value = "所在市")
    private String city;

    /**
     * 所在区县
     */
    @ApiModelProperty(value = "所在区县")
    private String county;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String address;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String contacts;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /**
     * 站点图片路径
     */
    @ApiModelProperty(value = "站点图片路径")
    private String imagePath;

    /**
     * 删除站点图片路径
     */
    @ApiModelProperty(value = "删除站点图片路径")
    private String deleteImagePath;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @ApiModelProperty(value = "伪删除状态 1-正常 2-删除", required = true)
    private Integer isDelete;

    /**
     * 开放类型 1-对外开放；2-专用站点
     */
    @ApiModelProperty(value = "开放类型 1-对外开放；2-专用站点")
    private Integer openType;

    /**
     * 营业时间
     */
    @ApiModelProperty(value = "营业时间")
    private String businessHours;

    /**
     * 停车费用描述
     */
    @ApiModelProperty(value = "停车费用描述")
    private String parkCostDesc;

    /**
     * 建筑场所：1-居民区；2-公共机构；3-企事业单位；4-写字楼；5-工业园区；6-交通枢纽；7-大型文体设施；8城市绿地；9-大型建筑配建停车场；10-路边停车位；11-城际高速服务区；12-国省道路沿线；13-城际快速公路沿线；14-其他
     */
    @ApiModelProperty(value = "建筑场所：1-居民区；2-公共机构；3-企事业单位；4-写字楼；5-工业园区；6-交通枢纽；7-大型文体设施；8城市绿地；9-大型建筑配建停车场；10-路边停车位；11-城际高速服务区；12-国省道路沿线；13-城际快速公路沿线；14-其他")
    private Integer buildSite;
}
