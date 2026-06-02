package com.sunmax.together.vo.operation.appletUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AppletUserQueryVo", description = "小程序用户查询参数")
public class AppletUserQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

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
     * 用户状态 1-正常 2-冻结
     */
    @ApiModelProperty(value = "用户状态 1-正常 2-冻结")
    private Integer userState;
}
