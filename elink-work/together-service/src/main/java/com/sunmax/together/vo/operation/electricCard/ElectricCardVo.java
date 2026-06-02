package com.sunmax.together.vo.operation.electricCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "ElectricCardVo", description = "电卡编辑参数实体类")
public class ElectricCardVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 电卡类型 1-预付费 2-后付费 3-无需付费
     */
    @ApiModelProperty(value = "电卡类型 1-预付费 2-后付费 3-无需付费", required = true)
    private Integer cardType;

    /**
     * 卡面号
     */
    @ApiModelProperty(value = "卡面号", required = true)
    private String cardNumber;

    /**
     * 物理卡号
     */
    @ApiModelProperty(value = "物理卡号", required = true)
    private String physicalCard;

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    private String licenseNumber;

    /**
     * 持卡人
     */
    @ApiModelProperty(value = "持卡人", required = true)
    private String cardHolder;

    /**
     * 可用余额
     */
    @ApiModelProperty(value = "可用余额", required = true)
    private Double currentBalance;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

}