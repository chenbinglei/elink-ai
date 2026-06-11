package com.sunmax.together.dto.operation.electricCard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "电卡数据返回实体类")
public class ElectricCardDto {
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
     * 电卡余额
     */
    @Schema(description = "电卡余额")
    private double currentBalance;

    /**
     * 可用余额
     */
    @Schema(description = "可用余额")
    private double availableBalance;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 电卡状态（例如：正常、禁用）
     */
    @Schema(description = "电卡状态")
    private Integer state;

    /**
     * 创建人名称
     */

    @Schema(description = "创建人名称")
    private String createName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
