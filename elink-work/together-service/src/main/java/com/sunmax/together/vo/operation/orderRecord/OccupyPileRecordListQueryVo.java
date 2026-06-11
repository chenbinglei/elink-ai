package com.sunmax.together.vo.operation.orderRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "占桩订单记录列表查询参数")
public class OccupyPileRecordListQueryVo {

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

    /**
     * 当前登录用户所属租户id
     */
    @Schema(description = "当前登录用户所属租户id")
    private String userTenantId;

    /**
     * 运营商id
     */
    @Schema(description = "运营商id")
    private String operateUnitId;

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 关键字类型 1-占桩订单号 2-充放电订单号 3-用户手机号
     */
    @Schema(description = "关键字类型 1-占桩订单号 2-充放电订单号 3-用户手机号")
    private Integer keywordType;

    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 订单状态 1-在途 2-待支付 3-已完成 9-异常
     */
    @Schema(description = "订单状态 1-在途 2-待支付 3-已完成 9-异常")
    private Integer occupyState;

    /**
     * 占桩结束-开始时间
     */
    @Schema(description = "占桩结束-开始时间")
    private String endAlsoStartDate;

    /**
     * 占桩结束-结束时间
     */
    @Schema(description = "占桩结束-结束时间")
    private String endAlsoEndDate;

    /**
     * 电桩编码
     */
    @Schema(description = "电桩编码")
    private String pileCode;
}
