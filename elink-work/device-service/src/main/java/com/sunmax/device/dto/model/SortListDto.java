package com.sunmax.device.dto.model;

import com.sunmax.common.dto.PageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SortListDto", description = "模型分类列表返回实体类")
public class SortListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称", required = true)
    private String sortName;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    private String parentId;

    /**
     * 分类LOGO
     */
    @ApiModelProperty(value = "分类LOGO")
    private String sortLogo;

    /**
     * 关联模型数量
     */
    @ApiModelProperty(value = "关联模型数量")
    private Long modelNum;

    /**
     * 子类数量
     */
    @ApiModelProperty(value = "子类数量")
    private Long childrenNum;

    /**
     * 模型分类子目录列表
     */
    @ApiModelProperty(value = "模型分类子目录列表")
    private PageDto<SortListDto> childPage;

}
