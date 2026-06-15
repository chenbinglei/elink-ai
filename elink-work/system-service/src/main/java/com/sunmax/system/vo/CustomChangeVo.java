package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统自定义编辑参数实体类")
public class CustomChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 平台名称
     */
    @Schema(description = "平台名称")
    private String platformName;

    /**
     * 大屏标题
     */
    @Schema(description = "大屏标题")
    private String largeTitle;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
