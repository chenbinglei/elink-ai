package com.sunmax.together.vo.asset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InspectionItemVo", description = "巡检项编辑实体类")
public class InspectionItemVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 巡检项名称
     */
    @ApiModelProperty(value = "巡检项名称", required = true)
    private String name;

    /**
     * 巡检内容描述
     */
    @ApiModelProperty(value = "巡检内容描述")
    private String description;

}
