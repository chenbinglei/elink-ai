package com.sunmax.common.dto.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 设备基本信息返回实体类
 */
@Data
@ApiModel(value = "DeviceBasicInfoDto", description = "设备基本信息返回实体类")
public class DeviceBasicInfoDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @ApiModelProperty(value = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @ApiModelProperty(value = "运营状态 0-未知 1-投运 2-检修 3-退役")
    private Integer operateStatus;

    /**
     * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 创建人名称
     */
    @ApiModelProperty(value = "创建人名称")
    private String createName;

    /**
     * 编辑人名称
     */
    @ApiModelProperty(value = "编辑人名称")
    private String updateName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    @ApiModelProperty(value = "编辑时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 设备描述
     */
    @ApiModelProperty(value = "设备描述")
    private String deviceDesc;

    /**
     * 读写数据对象
     */
    @ApiModelProperty(value = "读写数据对象")
    private String readwriteObject;

    /**
     * 扩展属性对象
     */
    @ApiModelProperty(value = "扩展属性对象")
    private Map<String, Object> reaMap = Maps.newConcurrentMap();

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    public String modelId;

    /**
     * 模型名称
     */
    @ApiModelProperty(value = "模型名称")
    public String modelName;

    /**
     * 模型logo路径
     */
    @ApiModelProperty(value = "模型logo路径")
    private String logoPath;

    /**
     * 资产分类id
     */
    @ApiModelProperty(value = "资产分类id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @ApiModelProperty(value = "资产分类名称")
    private String typeName;

    /**
     * 模型描述
     */
    @ApiModelProperty(value = "模型描述")
    private String modelDesc;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    public String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    private String parentId;

    /**
     * 父节点名称
     */
    @ApiModelProperty(value = "父节点名称")
    private String parentName;

    /**
     * 运营商名称
     */
    @ApiModelProperty(value = "运营商名称")
    private String operateName;

    /**
     * 运营商id
     */
    @ApiModelProperty(value = "运营商id")
    private String operateId;

    /**
     * 产权方名称
     */
    @ApiModelProperty(value = "产权方名称")
    private String propertyName;

    /**
     * 产权方id
     */
    @ApiModelProperty(value = "产权方id")
    private String propertyId;

    /**
     * 设备图片路径
     */
    @ApiModelProperty(value = "设备图片路径")
    private String imagePaths;

}
