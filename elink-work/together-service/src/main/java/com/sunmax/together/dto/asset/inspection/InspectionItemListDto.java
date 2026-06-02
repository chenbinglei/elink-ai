package com.sunmax.together.dto.asset.inspection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "InspectionItemListDto", description = "巡检项配置列表返回实体类")
public class InspectionItemListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 巡检项名称
     */
    @ApiModelProperty(value = "巡检项名称")
    private String name;

    /**
     * 巡检内容描述
     */
    @ApiModelProperty(value = "巡检内容描述")
    private String description;

    /**
     * 图标路径
     */
    @ApiModelProperty(value = "图标路径")
    private String iconPath;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
