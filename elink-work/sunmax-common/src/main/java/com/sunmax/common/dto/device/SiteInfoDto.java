package com.sunmax.common.dto.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "站点信息返回实体类")
public class SiteInfoDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * 所属租户id
     */
    @Schema(description = "所属租户id")
    private String tenantId;

    /**
     * 所属租户名称
     */
    @Schema(description = "所属租户名称")
    private String tenantName;

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
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称")
    private String createName;

    /**
     * 编辑人名称
     */
    @Schema(description = "编辑人名称")
    private String updateName;

    /**
     * 权限 1-只读 2-读写
     */
    @Schema(description = "权限 1-只读 2-读写")
    private Integer authority;

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    @Schema(description = "编辑时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

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
     * 产权方id
     */
    @Schema(description = "产权方id")
    private String propertyId;

    /**
     * 产权方名称
     */
    @Schema(description = "产权方名称")
    private String propertyName;

    /**
     * 运营商id
     */
    @Schema(description = "运营商id")
    private String operatorId;

    /**
     * 运营商名称
     */
    @Schema(description = "运营商名称")
    private String operatorName;

    /**
     * 站点扩展属性对象列表
     */
    @Schema(description = "站点扩展属性对象列表")
    private List<DeviceReaDto> siteReaList = Lists.newArrayList();

    /**
     * 站点关联场景类型信息列表
     */
    @Schema(description = "站点关联场景类型信息列表")
    private List<SiteScenarioTypeDto> siteScenarioTypeDtos;
}
