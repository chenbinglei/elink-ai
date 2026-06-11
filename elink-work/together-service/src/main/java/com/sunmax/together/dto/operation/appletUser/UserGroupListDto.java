package com.sunmax.together.dto.operation.appletUser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户分组列表返回实体类")
public class UserGroupListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String groupName;

    /**
     * 电费折扣
     */
    @Schema(description = "电费折扣")
    private Integer elecDiscount;

    /**
     * 服务费折扣
     */
    @Schema(description = "服务费折扣")
    private Integer serviceDiscount;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;

    /**
     * 用户数量
     */
    @Schema(description = "用户数量")
    private Integer userNum = 0;

    /**
     * 创建人员名称
     */
    @Schema(description = "创建人员名称")
    private String createUserName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 应用站点数量
     */
    @Schema(description = "应用站点数量")
    private Integer applySiteNum = 0;

    /**
     * 应用站点列表
     */
    @Schema(description = "应用站点列表")
    private List<UserGroupListDto.SiteDetail> siteDetailList;

    /**
     * 站点信息
     */
    @Data
    @Schema(description = "站点信息")
    public static class SiteDetail {

        /**
         * 站点id
         */
        @Schema(description = "站点id")
        private String id;

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;
    }
}
