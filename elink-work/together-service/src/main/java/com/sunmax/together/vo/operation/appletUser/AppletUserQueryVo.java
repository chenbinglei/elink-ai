package com.sunmax.together.vo.operation.appletUser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "小程序用户查询参数")
public class AppletUserQueryVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

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
     * 用户状态 1-正常 2-冻结
     */
    @Schema(description = "用户状态 1-正常 2-冻结")
    private Integer userState;
}
