package com.sunmax.device.dto.model;

import com.sunmax.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型分类列表返回实体类")
public class SortListDto {

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

    /**
     * 分类LOGO
     */
    @Schema(description = "分类LOGO")
    private String sortLogo;

    /**
     * 关联模型数量
     */
    @Schema(description = "关联模型数量")
    private Long modelNum;

    /**
     * 子类数量
     */
    @Schema(description = "子类数量")
    private Long childrenNum;

    /**
     * 模型分类子目录列表
     */
    @Schema(description = "模型分类子目录列表")
    private PageDto<SortListDto> childPage;

}
