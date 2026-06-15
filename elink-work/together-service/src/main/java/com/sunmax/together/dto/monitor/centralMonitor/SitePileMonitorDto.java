package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点监控数据返回实体类")
public class SitePileMonitorDto {

    /**
     * 站点id
     */
    @Schema(description = "数据id")
    private String dataId;

    /**
     * 充电电量(度)
     */
    @Schema(description = "充电电量(度)")
    private Double chargeOrderQt = 0.0;

    /**
     * 充电订单数量(笔)
     */
    @Schema(description = "充电订单数量(笔)")
    private Integer chargeOrderNum = 0;

    /**
     * V2G放电电量(度)
     */
    @Schema(description = "V2G放电电量(度)")
    private Double dischargeOrderQt = 0.0;

    /**
     * 枪均电量(度)
     */
    @Schema(description = "枪均电量(度)")
    private Double avgChargeQt;

    /**
     * 一次充电成功率(%)
     */
    @Schema(description = "一次充电成功率(%)")
    private Double chargeSuccessRatio;

}
