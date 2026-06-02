package com.sunmax.together.vo.operation.electricCard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ElectricCardUpdateVo", description = "电卡编辑参数根据电卡详情得到的数据转换成vo")
public class ElectricCardUpdateVo {
    /**
     * 电卡类型
     */
    @ApiModelProperty(value = "电卡类型", required = true)
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
     * 电卡 ID
     */
    @ApiModelProperty(value = "电卡 ID", required = true)
    private String id;
    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    private String licenseNumber;
    /**
     * 持卡人
     */
    @ApiModelProperty(value = "持卡人")
    private String cardHolder;

}
