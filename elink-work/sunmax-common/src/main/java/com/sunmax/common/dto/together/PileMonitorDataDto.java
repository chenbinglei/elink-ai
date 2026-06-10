package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩监控数据返回实体类")
public class PileMonitorDataDto {

    /**
     * 数据id
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
