package com.sunmax.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "组织结构树形结构信息返回实体类")
public class OrganStructureTreeDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 组织名称
     */
    @Schema(description = "组织名称")
    private String organName;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer sortNumber;

    /**
     * 所属租户id
     */
    @Schema(description = "所属租户id")
    private String tenantId;

    /**
     * 人员数
     */
    @Schema(description = "人员数")
    private Integer peopleNumber;

    /**
     * 人员信息列表
     */
    @Schema(description = "人员信息列表")
    private List<UserDto> userDtoList;

    /**
     * 子级列表
     */
    @Schema(description = "子级列表")
    private List<OrganStructureTreeDto> childrenList;
}
