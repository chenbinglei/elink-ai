package com.sunmax.device.dto.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

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
     * 设备状态 0-未注册 1-在线 2-故障 88-离线
     */
    @Schema(description = "设备状态 0-未注册 1-在线 2-故障 88-离线")
    private Integer deviceStatus = 0;

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
     * 模型分类名称
     */
    @Schema(description = "模型分类名称")
    private String sortName;

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
     * 设备图片路径
     */
    @Schema(description = "设备图片路径")
    private String imagePaths;

}
