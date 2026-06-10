package com.sunmax.together.dto.asset.inspection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "节点人员设置列表返回实体类")
public class InspectionUserListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认
     */
    @Schema(description = "节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认")
    private Integer type;

    /**
     * 多个用户id
     */
    @Schema(description = "多个用户id")
    private String userIds;

    /**
     * 用户数量
     */
    @Schema(description = "用户数量")
    private Integer userNum;

}
