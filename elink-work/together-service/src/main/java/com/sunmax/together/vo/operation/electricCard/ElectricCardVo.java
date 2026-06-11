package com.sunmax.together.vo.operation.electricCard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "电卡编辑参数实体类")
public class ElectricCardVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 电卡类型 1-预付费 2-后付费 3-无需付费
     */
    @Schema(description = "电卡类型 1-预付费 2-后付费 3-无需付费")
    private Integer cardType;

    /**
     * 卡面号
     */
    @Schema(description = "卡面号")
    private String cardNumber;

    /**
     * 物理卡号
     */
    @Schema(description = "物理卡号")
    private String physicalCard;

    /**
     * 车牌号
     */
    @Schema(description = "车牌号")
    private String licenseNumber;

    /**
     * 持卡人
     */
    @Schema(description = "持卡人")
    private String cardHolder;

    /**
     * 可用余额
     */
    @Schema(description = "可用余额")
    private Double currentBalance;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
