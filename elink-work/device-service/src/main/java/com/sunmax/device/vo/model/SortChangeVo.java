package com.sunmax.device.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型分类编辑参数类")
public class SortChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String sortName;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;
//
//    /**
//     * 分类LOGO
//     */
//    @Schema(description = "分类LOGO")
//    private String sortLogo;

}
