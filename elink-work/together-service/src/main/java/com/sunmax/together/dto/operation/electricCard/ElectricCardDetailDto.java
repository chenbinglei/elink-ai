package com.sunmax.together.dto.operation.electricCard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "电卡详情返回实体类")
public class ElectricCardDetailDto {
    /**
     * 电卡id
     */
    @Schema(description = "电卡id")
    private String id;

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
     * 持卡人
     */
    @Schema(description = "持卡人")
    private String cardHolder;

    /**
     * 电卡类型（例如：后付费、预付费等）
     */
    @Schema(description = "电卡类型")
    private Integer cardType;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
