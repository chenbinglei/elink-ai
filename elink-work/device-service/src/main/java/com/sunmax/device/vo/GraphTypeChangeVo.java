package com.sunmax.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 图形分类编辑参数
 */
@Data
@Schema(description = "图形分类编辑参数")
public class GraphTypeChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private String typeId;

    /**
     * 图形分类编码
     */
    @Schema(description = "图形分类编码")
    private String code;

    /**
     * 图形分类名称
     */
    @Schema(description = "图形分类名称")
    private String name;

}
