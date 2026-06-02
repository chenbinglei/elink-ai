package com.sunmax.together.dto.operation.feedback;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "UserFeedbackDetailDto", description = "用户反馈详情返回实体类")
public class UserFeedbackDetailDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故
     */
    @ApiModelProperty(value = "反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故")
    private Integer feedbackType;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String imageUrl;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    private String userPhone;

    /**
     * 状态 1-待处理 2-处理中 3-已处理
     */
    @ApiModelProperty(value = "状态 1-待处理 2-处理中 3-已处理")
    private Integer status;

    /**
     * 反馈时间
     */
    @ApiModelProperty(value = "反馈时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 受理时间
     */
    @ApiModelProperty(value = "受理时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptTime;

    /**
     * 处理完成时间
     */
    @ApiModelProperty(value = "处理完成时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

}
