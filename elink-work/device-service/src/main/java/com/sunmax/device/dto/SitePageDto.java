package com.sunmax.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "SitePageDto", description = "站点分页列表返回实体类")
public class SitePageDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 所属租户id(拥有者企业)
     */
    @ApiModelProperty(value = "所属租户id(拥有者企业)")
    private String tenantId;

    /**
     * 所属租户名称
     */
    @ApiModelProperty(value = "所属租户名称")
    private String tenantName;

    /**
     * 站点编码
     */
    @ApiModelProperty(value = "站点编码")
    private String siteCode;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
     */
    @ApiModelProperty(value = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电")
    private String scenarioTypes;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @ApiModelProperty(value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 站点描述
     */
    @ApiModelProperty(value = "站点描述")
    private String siteDescribe;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    public String createId;

    /**
     * 编辑人名称
     */
    @ApiModelProperty(value = "编辑人名称")
    private String updateName;

    /**
     * 编辑时间
     */
    @ApiModelProperty(value = "编辑时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 编辑时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
