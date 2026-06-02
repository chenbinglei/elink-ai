package com.sunmax.together.dto.operation.appletUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppletCancelInfoDto", description = "小程序注销信息返回实体类")
public class AppletCancelInfoDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 小程序用户id
     */
    @ApiModelProperty(value = "小程序用户id")
    private String appletUserId;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String phoneNum;

    /**
     * 用户分组id
     */
    @ApiModelProperty(value = "用户分组id")
    private String groupId;

    /**
     * 用户分组名称
     */
    @ApiModelProperty(value = "用户分组名称")
    private String groupName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;

    /**
     * 小程序名称
     */
    @ApiModelProperty(value = "小程序名称")
    private String appletName;

    /**
     * 申请状态 1-申请注销 2-已注销
     */
    @ApiModelProperty(value = "申请状态 1-申请注销 2-已注销")
    private Integer applyState;

    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请时间")
    private String createTime;
}
