package com.sunmax.together.vo.operation.feedback;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户反馈查询对象")
public class UserFeedbackQueryVo {

    /**
     * 反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故
     */
    @Schema(description = "反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故")
    private Integer feedbackType;

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 状态 1-待处理 2-处理中 3-已处理
     */
    @Schema(description = "状态 1-待处理 2-处理中 3-已处理")
    private Integer status;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间 年月日")
    private String startDate;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间 年月日")
    private String endDate;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
