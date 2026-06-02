package com.sunmax.device.dto.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

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
     * 设备状态 0-未注册 1-在线 2-故障 88-离线
     */
    @ApiModelProperty(value = "设备状态 0-未注册 1-在线 2-故障 88-离线")
    private Integer deviceStatus = 0;

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
     * 模型分类名称
     */
    @ApiModelProperty(value = "模型分类名称")
    private String sortName;

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
     * 设备图片路径
     */
    @ApiModelProperty(value = "设备图片路径")
    private String imagePaths;

}
