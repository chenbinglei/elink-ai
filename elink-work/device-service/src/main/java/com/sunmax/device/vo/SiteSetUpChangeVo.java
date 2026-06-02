package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteSetUpChangeVo", description = "站点设置编辑参数实体类")
public class SiteSetUpChangeVo {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 操作密码
     */
    @ApiModelProperty(value = "操作密码", required = true)
    private String operatePassword;

    /**
     * app展示 1-展示 2-不展示
     */
    @ApiModelProperty(value = "app展示 1-展示 2-不展示", required = true)
    private Integer appShow;

    /**
     * 光伏发电量数据来源 20-逆变器 66-并网点
     */
    @ApiModelProperty(value = "光伏发电量数据来源 20-逆变器 66-并网点", required = true)
    private Integer pvQtSource;

    /**
     * 二氧化碳减排量计算系数
     */
    @ApiModelProperty(value = "二氧化碳减排量计算系数", required = true)
    private Double reduceCoeff;

    /**
     * 节约标煤量计算系数
     */
    @ApiModelProperty(value = "节约标煤量计算系数", required = true)
    private Double tceCoeff;

    /**
     * 等效植树计算系数
     */
    @ApiModelProperty(value = "等效植树计算系数", required = true)
    private Double treeCoeff;

    /**
     * 站点应用配置信息对象
     */
    @ApiModelProperty(value = "站点应用配置信息对象", required = true)
    private String readwriteObject;

    /**
     * 系统名称
     */
    @ApiModelProperty(value = "系统名称")
    private String systemName;

}
