package com.sunmax.device.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "SiteListDto", description = "站点列表返回实体类")
public class SiteListDto {

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
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @ApiModelProperty(value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 权限 1-只读 2-读写
     */
    @ApiModelProperty(value = "权限 1-只读 2-读写")
    private Integer authority;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 业主单位
     */
    @ApiModelProperty(value = "业主单位")
    private String ownerUnit;

    /**
     * 运营单位
     */
    @ApiModelProperty(value = "运营单位")
    private String operateUnit;

    /**
     * 业主单位名称
     */
    @ApiModelProperty(value = "业主单位名称")
    private String ownerUnitName;

    /**
     * 运营单位名称
     */
    @ApiModelProperty(value = "运营单位名称")
    private String operateUnitName;

    /**
     * 站点描述
     */
    @ApiModelProperty(value = "站点描述")
    private String siteDescribe;

    /**
     * 电网状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "电网状态 1-开启 2-关闭")
    private Integer powerGridState;

    /**
     * 变配电状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "变配电状态 1-开启 2-关闭")
    private Integer tranState;

    /**
     * 电桩状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "电桩状态 1-开启 2-关闭")
    private Integer pileState;

    /**
     * 光伏状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "光伏状态 1-开启 2-关闭")
    private Integer pvState;

    /**
     * 储能状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "储能状态 1-开启 2-关闭")
    private Integer storageState;

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
