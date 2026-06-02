package com.sunmax.configure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "PelDetailDto", description = "图元详情返回实体类")
public class PelDetailDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 图元类型 1-文件上传 2-自定义图元
     */
    @ApiModelProperty(value = "图元类型 1-文件上传 2-自定义图元")
    private Integer pelType;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型 1-文件夹 2-图元")
    private Integer type;

    /**
     * 父级id
     */
    @ApiModelProperty("父级id")
    private String parentId;

    /**
     * 绑定数据名称
     */
    @ApiModelProperty(value = "绑定数据名称")
    private String dataName;

    /**
     * 绑定数据文件路径
     */
    @ApiModelProperty(value = "绑定数据文件路径")
    private String dataPath;

    /**
     * 绑定数据文件数据
     */
    @ApiModelProperty(value = "绑定数据文件数据")
    private String dataData;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 绑定数据文件数据
     */
    @ApiModelProperty(value = "绑定数据文件数据")
    private String fileData;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
