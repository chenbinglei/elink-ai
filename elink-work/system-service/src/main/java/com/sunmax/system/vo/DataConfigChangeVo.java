package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DataConfigChangeVo", description = "数据配置编辑参数")
public class DataConfigChangeVo {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 动态配置
     */
    @ApiModelProperty(value = "动态配置", required = true)
    private String dynamicConfigs;

}
