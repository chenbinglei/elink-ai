package com.sunmax.device.dto.webserver;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "SiteCountDto", description = "站点统计数据返回实体类")
public class SiteCountDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 累计充电次数
     */
    @ApiModelProperty(value = "累计充电次数")
    private Integer chargeCount = 0;

    /**
     * 站点累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private Double chargeQt = 0.0;

    /**
     * 累计充电金额
     */
    @ApiModelProperty(value = "累计充电金额")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 站点累计放电次数
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

    /**
     * 设备统计数据
     */
    @ApiModelProperty(value = "设备统计数据")
    private List<DeviceCountDto> deviceCountList = Lists.newArrayList();

}
