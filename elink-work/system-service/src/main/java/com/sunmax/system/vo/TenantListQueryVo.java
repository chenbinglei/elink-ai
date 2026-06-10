package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租户列表查询参数")
public class TenantListQueryVo {

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

    /**
     * 关键字类型 1-租户名称 2-租户ID
     */
    @Schema(description = "关键字类型 1-租户名称 2-租户ID")
    private Integer keywordType;

    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;
}
