package com.sunmax.common.dto.crontab;

import com.sunmax.common.dto.device.SiteSetUpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "组态站点列表返回实体类")
public class ConfigurSiteListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * 所属租户id
     */
    @Schema(description = "所属租户id")
    private String tenantId;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 站点状态 1-正常 2-预警 3-通信异常 4-故障(多个以逗号分割)
     */
    @Schema(description = "站点状态 1-正常 2-预警 3-通信异常 4-故障(多个以逗号分割)")
    private String siteStates;

    /**
     * 地址信息
     */
    @Schema(description = "地址信息")
    private String location;

    /**
     * 运行天数
     */
    @Schema(description = "运行天数")
    private Long runDays;

    /**
     * 站点设置信息
     */
    @Schema(description = "站点设置信息")
    private SiteSetUpDto siteSetUpDto;
}
