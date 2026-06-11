package com.sunmax.together.dto.operation.feedback;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户反馈列表返回实体类")
public class UserFeedbackListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故
     */
    @Schema(description = "反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故")
    private Integer feedbackType;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 状态 1-待处理 2-处理中 3-已处理
     */
    @Schema(description = "状态 1-待处理 2-处理中 3-已处理")
    private Integer status;

    /**
     * 反馈时间
     */
    @Schema(description = "反馈时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
