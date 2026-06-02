package com.sunmax.device.dto.webserver;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "DeviceCountDto", description = "设备统计返回实体类")
public class DeviceCountDto {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 累计充电次数
     */
    @ApiModelProperty(value = "累计充电次数")
    private Integer chargeCount = 0;

    /**
     * 累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private Double chargeQt = 0.0;

    /**
     * 累计充电金额
     */
    @ApiModelProperty(value = "累计充电金额")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 累计放电次数
     */
    @ApiModelProperty(value = "累计放电次数")
    private Integer dischargeCount = 0;

    /**
     * 累计放电量
     */
    @ApiModelProperty(value = "累计放电量")
    private Double dischargeQt = 0.0;

    /**
     * 累计放电金额
     */
    @ApiModelProperty(value = "累计放电金额")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

}
