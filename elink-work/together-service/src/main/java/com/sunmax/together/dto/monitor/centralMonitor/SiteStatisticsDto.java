package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "场站统计返回实体类")
public class SiteStatisticsDto {

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
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Schema(description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 系统数据列表
     */
    @Schema(description = "系统列表")
    private List<SystemStatisticsDto> devices;

}
