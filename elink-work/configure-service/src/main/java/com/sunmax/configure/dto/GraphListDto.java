package com.sunmax.configure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "图模列表返回实体类")
public class GraphListDto {

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
     * 类型 1-文件夹 2-图模文件
     */
    @Schema(description = "类型 1-文件夹 2-图模文件")
    private Integer type;

    /**
     * 状态 0-无 1-有更新 2-已发布
     */
    @Schema(description = "状态 0-无 1-有更新 2-已发布")
    private Integer status;

    /**
     * 锁定状态 0-未锁定 1-锁定
     */
    @Schema(description = "锁定状态 0-未锁定 1-锁定")
    private Integer lockStatus;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 域名id
     */
    @Schema(description = "域名id")
    private String domainId;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

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
