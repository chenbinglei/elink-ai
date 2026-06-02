package com.sunmax.together.vo.operation.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserFeedbackQueryVo", description = "用户反馈查询对象")
public class UserFeedbackQueryVo {

    /**
     * 反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故
     */
    @ApiModelProperty(value = "反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故")
    private Integer feedbackType;

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id")
    private String siteIds;

    /**
     * 状态 1-待处理 2-处理中 3-已处理
     */
    @ApiModelProperty(value = "状态 1-待处理 2-处理中 3-已处理")
    private Integer status;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间 年月日")
    private String startDate;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间 年月日")
    private String endDate;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数")
    private Integer size;

}
