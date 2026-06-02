package com.sunmax.together.dto.operation.electricCard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "ElectricCardDto", description = "电卡数据返回实体类")
public class ElectricCardDto {
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
     * 电卡余额
     */
    @ApiModelProperty(value = "电卡余额")
    private double currentBalance;

    /**
     * 可用余额
     */
    @ApiModelProperty(value = "可用余额")
    private double availableBalance;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String creator;

    /**
     * 电卡状态（例如：正常、禁用）
     */
    @ApiModelProperty(value = "电卡状态")
    private Integer state;

    /**
     * 创建人名称
     */

    @ApiModelProperty(value = "创建人名称")
    private String createName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
