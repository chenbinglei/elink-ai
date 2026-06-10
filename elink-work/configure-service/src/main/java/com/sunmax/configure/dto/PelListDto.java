package com.sunmax.configure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "图元列表返回实体类")
public class PelListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 图元类型 1-文件上传 2-自定义图元
     */
    @Schema(description = "图元类型 1-文件上传 2-自定义图元")
    private Integer pelType;

    /**
     * 类型
     */
    @Schema(description = "类型 1-文件夹 2-图元")
    private Integer type;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 绑定数据名称
     */
    @Schema(description = "绑定数据名称")
    private String dataName;

    /**
     * 绑定数据文件路径
     */
    @Schema(description = "绑定数据文件路径")
    private String dataPath;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
