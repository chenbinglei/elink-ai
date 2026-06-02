package com.sunmax.together.vo.operation.electricCard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ElectricCardQueryVo", description = "交易查询参数")
public class ElectricCardTradeVo {
    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型", required = true)
    private Integer tradeType;
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endDate;

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

}
