package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点关口拓扑节点数据返回实体类")
public class SiteGateTopDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String nodeName;

}
