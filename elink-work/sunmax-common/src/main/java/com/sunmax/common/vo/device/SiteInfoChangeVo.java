package com.sunmax.common.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteInfoChangeVo", description = "站点信息编辑参数")
public class SiteInfoChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 所属租户id(拥有者企业)
     */
    @ApiModelProperty(value = "所属租户id(拥有者企业)", required = true)
    private String tenantId;

    /**
     * 站点编码
     */
    @ApiModelProperty(value = "站点编码", required = true)
    private String siteCode;

    /**
     * 产权方id
     */
    @ApiModelProperty(value = "产权方id", required = true)
    private String propertyId;

    /**
     * 运营商id
     */
    @ApiModelProperty(value = "运营商id", required = true)
    private String operatorId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称", required = true)
    private String siteName;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @ApiModelProperty(value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中", required = true)
    private Integer siteStatus;

    /**
     * 站点描述
     */
    @ApiModelProperty(value = "站点描述")
    private String siteDescribe;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)
     */
    @ApiModelProperty(value = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电(可存储多个，以逗号分割)", required = true)
    private String scenarioTypes;

    /**
     * 站点模型id
     */
    @ApiModelProperty(value = "站点模型id", required = true)
    private String siteModelId;

    /**
     * 站点读写数据对象
     */
    @ApiModelProperty(value = "站点读写数据对象", required = true)
    private String siteReadwriteObject;

    /**
     * 站点图片路径
     */
    @ApiModelProperty(value = "站点图片路径")
    private String imagePath;

    /**
     * 伪删除状态 1-正常 2-已删除
     */
    @ApiModelProperty(value = "伪删除状态 1-正常 2-已删除")
    private Integer isDelete;

    /**
     * 来源类型 1-自建 2-城市充电接入
     */
    @ApiModelProperty(value = "来源类型 1-自建 2-城市充电接入")
    private Integer sourceType;

    /**
     * 站点关联能源场景类型系统对象列表
     */
    @ApiModelProperty(value = "站点关联能源场景类型系统对象列表", required = true)
    private String siteScenarioTypeDtos;

    @Data
    public static class SiteScenarioTypeDto {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
         */
        @ApiModelProperty(value = "能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电")
        private Integer scenarioType;

        /**
         * 能源系统名称
         */
        @ApiModelProperty(value = "能源系统名称")
        private String systemName;

        /**
         * 模型id
         */
        @ApiModelProperty(value = "模型id")
        private String modelId;

        /**
         * 读写数据对象
         */
        @ApiModelProperty(value = "读写数据对象")
        private String readwriteObject;
    }
}
