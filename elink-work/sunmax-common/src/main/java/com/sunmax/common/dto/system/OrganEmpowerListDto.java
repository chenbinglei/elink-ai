package com.sunmax.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "资产授权列表信息返回实体类")
public class OrganEmpowerListDto {


    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

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
     * 权限 1-只读 2-读写
     */
    @Schema(description = "权限 1-只读 2-读写")
    private Integer authority;
}
