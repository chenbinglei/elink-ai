package com.sunmax.common.dto.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 设备基本信息返回实体类
 */
@Data
@Schema(description = "设备基本信息返回实体类")
public class DeviceBasicInfoDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @Schema(description = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @Schema(description = "运营状态 0-未知 1-投运 2-检修 3-退役")
    private Integer operateStatus;

    /**
     * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

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
     * 设备描述
     */
    @Schema(description = "设备描述")
    private String deviceDesc;

    /**
     * 读写数据对象
     */
    @Schema(description = "读写数据对象")
    private String readwriteObject;

    /**
     * 扩展属性对象
     */
    @Schema(description = "扩展属性对象")
    private Map<String, Object> reaMap = Maps.newConcurrentMap();

    /**
     * 模型id
     */
    @Schema(description = "模型id")
    public String modelId;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    public String modelName;

    /**
     * 模型logo路径
     */
    @Schema(description = "模型logo路径")
    private String logoPath;

    /**
     * 资产分类id
     */
    @Schema(description = "资产分类id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @Schema(description = "资产分类名称")
    private String typeName;

    /**
     * 模型描述
     */
    @Schema(description = "模型描述")
    private String modelDesc;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    public String siteId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;

    /**
     * 父节点名称
     */
    @Schema(description = "父节点名称")
    private String parentName;

    /**
     * 运营商名称
     */
    @Schema(description = "运营商名称")
    private String operateName;

    /**
     * 运营商id
     */
    @Schema(description = "运营商id")
    private String operateId;

    /**
     * 产权方名称
     */
    @Schema(description = "产权方名称")
    private String propertyName;

    /**
     * 产权方id
     */
    @Schema(description = "产权方id")
    private String propertyId;

    /**
     * 设备图片路径
     */
    @Schema(description = "设备图片路径")
    private String imagePaths;

}
