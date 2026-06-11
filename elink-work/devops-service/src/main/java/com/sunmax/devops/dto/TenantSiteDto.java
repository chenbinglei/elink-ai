package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租户场站数据返回实体类")
public class TenantSiteDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;

    /**
     * 类型 1-租户 2-站点
     */
    @Schema(description = "类型 1-租户 2-站点")
    private Integer type;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 位置
     */
    @Schema(description = "位置")
    private String location;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

}
