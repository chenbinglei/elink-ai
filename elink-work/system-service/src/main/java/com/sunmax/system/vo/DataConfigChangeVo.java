package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据配置编辑参数")
public class DataConfigChangeVo {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 动态配置
     */
    @Schema(description = "动态配置")
    private String dynamicConfigs;

}
