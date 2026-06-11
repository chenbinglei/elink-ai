package com.sunmax.together.dto.monitor.assetOverview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "资产站点列表返回实体类")
public class AssetSiteListDto {

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
     * 省份
     */
    @Schema(description = "省份")
    private String province;

    /**
     * 市级
     */
    @Schema(description = "市级")
    private String city;

    /**
     * 区县
     */
    @Schema(description = "区县")
    private String county;

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
}
