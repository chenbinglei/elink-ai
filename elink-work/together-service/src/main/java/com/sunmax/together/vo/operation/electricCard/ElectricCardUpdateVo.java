package com.sunmax.together.vo.operation.electricCard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电卡编辑参数根据电卡详情得到的数据转换成vo")
public class ElectricCardUpdateVo {
    /**
     * 电卡类型
     */
    @Schema(description = "电卡类型")
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
     * 电卡 ID
     */
    @Schema(description = "电卡 ID")
    private String id;
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

}
