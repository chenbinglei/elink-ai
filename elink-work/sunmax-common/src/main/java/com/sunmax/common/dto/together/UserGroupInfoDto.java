package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户分组信息返回实体类")
public class UserGroupInfoDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String groupName;

    /**
     * 电费折扣(百分比值)
     */
    @Schema(description = "电费折扣(百分比值)")
    private Integer elecDiscount = 100;

    /**
     * 服务费折扣(百分比值)
     */
    @Schema(description = "服务费折扣(百分比值)")
    private Integer serviceDiscount = 100;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;
}
