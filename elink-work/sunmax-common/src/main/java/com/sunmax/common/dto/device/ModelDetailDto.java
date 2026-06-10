package com.sunmax.common.dto.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "模型详情返回实体类")
public class ModelDetailDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @Schema(description = "资产分类名称")
    private String typeName;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

    /**
     * 设备数量
     */
    @Schema(description = "设备数量")
    private Integer deviceNum = 0;

    /**
     * 模型状态 0-开发中 1-已发布
     */
    @Schema(description = "模型状态 0-开发中 1-已发布")
    private Integer modelStatus;

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
    public LocalDateTime createTime;

    /**
     * 编辑时间
     */
    @Schema(description = "编辑时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    public LocalDateTime updateTime;

    /**
     * 模型描述
     */
    @Schema(description = "模型描述")
    private String modelDesc;

    /**
     * logo路径
     */
    @Schema(description = "logo路径")
    private String logoPath;

}
