package com.sunmax.together.dto.operation.electricCard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "ElectricCardDetailDto", description = "电卡详情返回实体类")
public class ElectricCardDetailDto {
    /**
     * 电卡id
     */
    @ApiModelProperty(value = "电卡id")
    private String id;

    /**
     * 卡面号
     */
    @ApiModelProperty(value = "卡面号")
    private String cardNumber;

    /**
     * 物理卡号
     */
    @ApiModelProperty(value = "物理卡号")
    private String physicalCard;

    /**
     * 持卡人
     */
    @ApiModelProperty(value = "持卡人")
    private String cardHolder;

    /**
     * 电卡类型（例如：后付费、预付费等）
     */
    @ApiModelProperty(value = "电卡类型")
    private Integer cardType;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
