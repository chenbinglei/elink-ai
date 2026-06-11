package com.sunmax.system.dto;

import com.sunmax.common.dto.system.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户组列表返回实体类")
public class UserGroupListDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 用户组名称
     */
    @Schema(description = "用户组名称")
    private String groupName;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;

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
}
