package com.sunmax.common.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteSetUpDto", description = "站点设置返回实体类")
public class SiteSetUpDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 操作密码
     */
    @ApiModelProperty(value = "操作密码")
    private String operatePassword;

    /**
     * app展示 1-展示 2-不展示
     */
    @ApiModelProperty(value = "app展示 1-展示 2-不展示")
    private Integer appShow;

    /**
     * 光伏发电量数据来源 20-逆变器 66-并网点
     */
    @ApiModelProperty(value = "光伏发电量数据来源 20-逆变器 66-并网点")
    private Integer pvQtSource = 20;

    /**
     * 二氧化碳减排量计算系数
     */
    @ApiModelProperty(value = "二氧化碳减排量计算系数")
    private Double reduceCoeff = 0.475;

    /**
     * 节约标煤量计算系数
     */
    @ApiModelProperty(value = "节约标煤量计算系数")
    private Double tceCoeff = 0.4;

    /**
     * 等效植树计算系数
     */
    @ApiModelProperty(value = "等效植树计算系数")
    private Double treeCoeff = 18.3;

    /**
     * 站点应用配置信息对象
     */
    @ApiModelProperty(value = "站点应用配置信息对象")
    private String readwriteObject;

    /**
     * 系统名称
     */
    @ApiModelProperty(value = "系统名称")
    private String systemName;

}
