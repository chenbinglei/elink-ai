package com.sunmax.configure.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "图模导入参数实体类")
public class GraphImportVo {

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 状态 0-无 1-有更新 2-已发布
     */
    @Schema(description = "状态 0-无 1-有更新 2-已发布")
    private Integer status;

}
