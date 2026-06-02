package com.sunmax.together.dto.monitor.assetOverview;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AssetSiteListDto", description = "资产站点列表返回实体类")
public class AssetSiteListDto {

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 站点唯一id
     */
    @ApiModelProperty(value = "站点唯一id")
    private String id;

    /**
     * 省份
     */
    @ApiModelProperty(value = "省份")
    private String province;

    /**
     * 市级
     */
    @ApiModelProperty(value = "市级")
    private String city;

    /**
     * 区县
     */
    @ApiModelProperty(value = "区县")
    private String county;

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
}
