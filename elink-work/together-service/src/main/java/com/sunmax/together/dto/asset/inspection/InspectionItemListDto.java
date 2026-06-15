package com.sunmax.together.dto.asset.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "巡检项配置列表返回实体类")
public class InspectionItemListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 巡检项名称
     */
    @Schema(description = "巡检项名称")
    private String name;

    /**
     * 巡检内容描述
     */
    @Schema(description = "巡检内容描述")
    private String description;

    /**
     * 图标路径
     */
    @Schema(description = "图标路径")
    private String iconPath;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
