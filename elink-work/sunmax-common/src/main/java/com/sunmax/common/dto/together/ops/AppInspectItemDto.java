package com.sunmax.common.dto.together.ops;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppInspectItemDto", description = "巡检项数据返回实体类")
public class AppInspectItemDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 巡检项名称
     */
    @ApiModelProperty(value = "巡检项名称")
    private String name;

    /**
     * 巡检内容描述
     */
    @ApiModelProperty(value = "巡检内容描述")
    private String description;

    /**
     * 图标地址
     */
    @ApiModelProperty(value = "图标地址")
    private String iconPath;

}
