package com.sunmax.together.vo.operation.orderRecord;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OccupyPileRecordListQueryVo", description = "占桩订单记录列表查询参数")
public class OccupyPileRecordListQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

    /**
     * 当前登录用户所属租户id
     */
    @ApiModelProperty(value = "当前登录用户所属租户id", required = true)
    private String userTenantId;

    /**
     * 运营商id
     */
    @ApiModelProperty(value = "运营商id")
    private String operateUnitId;

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id")
    private String siteId;

    /**
     * 关键字类型 1-占桩订单号 2-充放电订单号 3-用户手机号
     */
    @ApiModelProperty(value = "关键字类型 1-占桩订单号 2-充放电订单号 3-用户手机号")
    private Integer keywordType;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;

    /**
     * 订单状态 1-在途 2-待支付 3-已完成 9-异常
     */
    @ApiModelProperty("订单状态 1-在途 2-待支付 3-已完成 9-异常")
    private Integer occupyState;

    /**
     * 占桩结束-开始时间
     */
    @ApiModelProperty(value = "占桩结束-开始时间")
    private String endAlsoStartDate;

    /**
     * 占桩结束-结束时间
     */
    @ApiModelProperty(value = "占桩结束-结束时间")
    private String endAlsoEndDate;

    /**
     * 电桩编码
     */
    @ApiModelProperty(value = "电桩编码")
    private String pileCode;
}
