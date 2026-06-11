package com.sunmax.common.dto.together.ops;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "巡检节点人员名称列表返回实体类")
public class InspectionUserNameDto {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

}
