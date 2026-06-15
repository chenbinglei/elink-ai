package com.sunmax.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Transient;
import java.util.List;

/**
 * 树形交互类
 */
@Data
@Schema(description = "TreeCommon")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeCommonDto {

    /**
     * id
     */
    @Schema(description = "子节点id")
    private String id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 父id
     */
    @Schema(description = "父节点id")
    private String parentId;

    /**
     * 企业id
     */
    @Schema(description = "企业id")
    private String companyId;

    /**
     * 类型
     */
    @Schema(description = "类型")
    private Integer type;

//
//    /**
//     * 子节点条数
//     */
//    @Schema(description = "子节点条数")
//    private Long total;

    @Transient
    private List<TreeCommonDto> children;

}
