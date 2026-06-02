package com.sunmax.system.dto;

import com.sunmax.common.dto.system.UserDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "UserGroupListDto", description = "用户组列表返回实体类")
public class UserGroupListDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 用户组名称
     */
    @ApiModelProperty(value = "用户组名称")
    private String groupName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;

    /**
     * 人员数
     */
    @ApiModelProperty(value = "人员数")
    private Integer peopleNumber;

    /**
     * 人员信息列表
     */
    @ApiModelProperty(value = "人员信息列表")
    private List<UserDto> userDtoList;
}
