package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SitePileMonitorDto", description = "站点监控数据返回实体类")
public class SitePileMonitorDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "数据id")
    private String dataId;

    /**
     * 充电电量(度)
     */
    @ApiModelProperty(value = "充电电量(度)")
    private Double chargeOrderQt = 0.0;

    /**
     * 充电订单数量(笔)
     */
    @ApiModelProperty(value = "充电订单数量(笔)")
    private Integer chargeOrderNum = 0;

    /**
     * V2G放电电量(度)
     */
    @ApiModelProperty(value = "V2G放电电量(度)")
    private Double dischargeOrderQt = 0.0;

    /**
     * 枪均电量(度)
     */
    @ApiModelProperty(value = "枪均电量(度)")
    private Double avgChargeQt;

    /**
     * 一次充电成功率(%)
     */
    @ApiModelProperty(value = "一次充电成功率(%)")
    private Double chargeSuccessRatio;

}
