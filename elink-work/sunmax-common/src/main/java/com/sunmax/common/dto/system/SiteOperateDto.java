package com.sunmax.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点关联运营配置返回实体类")
public class SiteOperateDto {

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 所属运营商id
     */
    @Schema(description = "所属运营商id")
    private String operateId;

    /**
     * 资源编号
     */
    @Schema(description = "资源编号")
    private String resourceSn;

}
