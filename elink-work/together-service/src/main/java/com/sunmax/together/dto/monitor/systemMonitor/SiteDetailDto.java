package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点详情数据返回实体类")
public class SiteDetailDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 系统名称
     */
    @Schema(description = "系统名称")
    private String systemName;

    /**
     * 站点应用配置信息对象
     */
    @Schema(description = "站点应用配置信息对象")
    private String readwriteObject;

}
