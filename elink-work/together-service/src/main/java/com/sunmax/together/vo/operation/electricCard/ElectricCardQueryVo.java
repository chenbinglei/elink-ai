package com.sunmax.together.vo.operation.electricCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ElectricCardQueryVo", description = "电卡查询参数")
public class ElectricCardQueryVo {
    /**
     * 电卡 ID
     */
    @ApiModelProperty(value = "电卡ID")
    private String id;    // 电卡 ID
    /**
     * 持卡人
     */
    @ApiModelProperty(value = "持卡人")
    private String cardHolder;        // 持卡人
    /**
     * 卡面号
     */
    @ApiModelProperty(value = "卡面号")
    private String cardNumber;        // 卡面号
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
