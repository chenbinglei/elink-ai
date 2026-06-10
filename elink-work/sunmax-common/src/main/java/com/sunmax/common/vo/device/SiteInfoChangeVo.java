package com.sunmax.common.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点信息编辑参数")
public class SiteInfoChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 所属租户id(拥有者企业)
     */
    @Schema(description = "所属租户id(拥有者企业)")
    private String tenantId;

    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * 产权方id
     */
    @Schema(description = "产权方id")
    private String propertyId;

    /**
     * 运营商id
     */
    @Schema(description = "运营商id")
    private String operatorId;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Schema(description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 站点描述
     */
    @Schema(description = "站点描述")
    private String siteDescribe;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 站点模型id
     */
    @Schema(description = "站点模型id")
    private String siteModelId;

    /**
     * 站点读写数据对象
     */
    @Schema(description = "站点读写数据对象")
    private String siteReadwriteObject;

    /**
     * 站点图片路径
     */
    @Schema(description = "站点图片路径")
    private String imagePath;

    /**
     * 伪删除状态 1-正常 2-已删除
     */
    @Schema(description = "伪删除状态 1-正常 2-已删除")
    private Integer isDelete;

    /**
     * 来源类型 1-自建 2-城市充电接入
     */
    @Schema(description = "来源类型 1-自建 2-城市充电接入")
    private Integer sourceType;

    /**
     * 站点关联能源场景类型系统对象列表
     */
    @Schema(description = "站点关联能源场景类型系统对象列表")
    private String siteScenarioTypeDtos;

    @Data
    public static class SiteScenarioTypeDto {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
         */
        @Schema(description = "能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电")
        private Integer scenarioType;

        /**
         * 能源系统名称
         */
        @Schema(description = "能源系统名称")
        private String systemName;

        /**
         * 模型id
         */
        @Schema(description = "模型id")
        private String modelId;

        /**
         * 读写数据对象
         */
        @Schema(description = "读写数据对象")
        private String readwriteObject;
    }
}
