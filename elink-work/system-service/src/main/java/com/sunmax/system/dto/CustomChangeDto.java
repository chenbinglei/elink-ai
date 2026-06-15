package com.sunmax.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统自定义返回实体类")
public class CustomChangeDto {

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
     * 平台LOGO
     */
    @Schema(description = "平台LOGO")
    private String platformLogo;

    /**
     * 大屏标题
     */
    @Schema(description = "大屏标题")
    private String largeTitle;

}
