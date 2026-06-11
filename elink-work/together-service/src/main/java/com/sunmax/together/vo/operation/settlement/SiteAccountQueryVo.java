package com.sunmax.together.vo.operation.settlement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 站点账户查询参数
 */
@Data
@Schema(description = "站点账户查询参数")
public class SiteAccountQueryVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 区域类型 1-省 2-市
     */
    @Schema(description = "区域类型 1-省 2-市")
    private Integer areaType;

    /**
     * 区域值
     */
    @Schema(description = "区域值")
    private String areaValue;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
