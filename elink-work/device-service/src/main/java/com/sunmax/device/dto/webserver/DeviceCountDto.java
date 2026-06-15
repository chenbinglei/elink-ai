package com.sunmax.device.dto.webserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "设备统计返回实体类")
public class DeviceCountDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 累计充电次数
     */
    @Schema(description = "累计充电次数")
    private Integer chargeCount = 0;

    /**
     * 累计充电量
     */
    @Schema(description = "累计充电量")
    private Double chargeQt = 0.0;

    /**
     * 累计充电金额
     */
    @Schema(description = "累计充电金额")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 累计放电次数
     */
    @Schema(description = "累计放电次数")
    private Integer dischargeCount = 0;

    /**
     * 累计放电量
     */
    @Schema(description = "累计放电量")
    private Double dischargeQt = 0.0;

    /**
     * 累计放电金额
     */
    @Schema(description = "累计放电金额")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

}
