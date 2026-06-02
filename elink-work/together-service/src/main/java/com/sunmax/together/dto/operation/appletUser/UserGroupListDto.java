package com.sunmax.together.dto.operation.appletUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "UserGroupListDto", description = "用户分组列表返回实体类")
public class UserGroupListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    private String groupName;

    /**
     * 电费折扣
     */
    @ApiModelProperty(value = "电费折扣")
    private Integer elecDiscount;

    /**
     * 服务费折扣
     */
    @ApiModelProperty(value = "服务费折扣")
    private Integer serviceDiscount;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;

    /**
     * 用户数量
     */
    @ApiModelProperty(value = "用户数量")
    private Integer userNum = 0;

    /**
     * 创建人员名称
     */
    @ApiModelProperty(value = "创建人员名称")
    private String createUserName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 应用站点数量
     */
    @ApiModelProperty(value = "应用站点数量")
    private Integer applySiteNum = 0;

    /**
     * 应用站点列表
     */
    @ApiModelProperty(value = "应用站点列表")
    private List<UserGroupListDto.SiteDetail> siteDetailList;

    /**
     * 站点信息
     */
    @Data
    @ApiModel(value = "SiteDetail", description = "站点信息")
    public static class SiteDetail {

        /**
         * 站点id
         */
        @ApiModelProperty(value = "站点id")
        private String id;

        /**
         * 站点名称
         */
        @ApiModelProperty(value = "站点名称")
        private String siteName;
    }
}
