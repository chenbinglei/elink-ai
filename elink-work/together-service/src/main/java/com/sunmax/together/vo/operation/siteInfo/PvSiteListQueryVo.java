package com.sunmax.together.vo.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PvSiteListQueryVo", description = "光伏站点列表查询参数")
public class PvSiteListQueryVo {

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
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 区域类型 1-省级 2-市级
     */
    @ApiModelProperty(value = "区域类型 1-省级 2-市级")
    private Integer areaType;

    /**
     * 区域
     */
    @ApiModelProperty(value = "区域")
    private String area;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @ApiModelProperty(value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 光伏类型 0-分布式商业 1-分布式户用 2-集中式
     */
    @ApiModelProperty(value = "光伏类型 0-分布式商业 1-分布式户用 2-集中式")
    private Integer pvType;

    /**
     * 消纳方式 0-自发自用 1-余电上网 2-全额上网 3-离网自用
     */
    @ApiModelProperty(value = "消纳方式 1-自发自用余电上网 2-全额上网 3-离网自用")
    private Integer consumMode;

    /**
     * 并网等级 0-0.4kV 1-10kV 2-20kV 3-35kV 4-110kV 5-220kV
     */
    @ApiModelProperty(value = "并网等级 0-0.4kV 1-10kV 2-20kV 3-35kV 4-110kV 5-220kV")
    private Integer tiedGrade;
}
