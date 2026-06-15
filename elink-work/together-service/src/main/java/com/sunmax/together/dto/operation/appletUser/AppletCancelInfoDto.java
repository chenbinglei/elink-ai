package com.sunmax.together.dto.operation.appletUser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小程序注销信息返回实体类")
public class AppletCancelInfoDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phoneNum;

    /**
     * 用户分组id
     */
    @Schema(description = "用户分组id")
    private String groupId;

    /**
     * 用户分组名称
     */
    @Schema(description = "用户分组名称")
    private String groupName;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;

    /**
     * 小程序名称
     */
    @Schema(description = "小程序名称")
    private String appletName;

    /**
     * 申请状态 1-申请注销 2-已注销
     */
    @Schema(description = "申请状态 1-申请注销 2-已注销")
    private Integer applyState;

    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    private String createTime;
}
