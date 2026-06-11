package com.sunmax.protocol.dto.virtual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * 电桩电量数据返回实体类
 */
@Data
@Schema(description = "电桩电量数据返回实体类")
public class VirtualPileQtDto {

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 充电量
     */
    @Schema(description = "充电桩充电量")
    private Double recChargeQt = 0.0;

    /**
     * 放电量
     */
    @Schema(description = "充电桩放电量")
    private Double disChargeQt = 0.0;

    /**
     * 充电金额
     */
    @Schema(description = "充电桩充电金额")
    private BigDecimal recChargeMoney = BigDecimal.ZERO;

    /**
     * 放电金额
     */
    @Schema(description = "充电桩放电金额")
    private BigDecimal disChargeMoney = BigDecimal.ZERO;

    /**
     * 充电枪实时数据列表
     */
    @Schema(description = "充电枪电量数据列表")
    private List<VirtualPileQtDto.GunQtData> gunQtDataList = Lists.newArrayList();

    @Data
    @Schema(description = "充电枪电量数据")
    public static class GunQtData {

        /**
         * 枪编号
         */
        @Schema(description = "枪编号")
        private Integer gunCode;

        /**
         * 充电量
         */
        @Schema(description = "充电枪充电量")
        private Double recChargeQt = 0.0;

        /**
         * 放电量
         */
        @Schema(description = "充电枪放电量")
        private Double disChargeQt = 0.0;

        /**
         * 充电金额
         */
        @Schema(description = "充电枪充电金额")
        private BigDecimal recChargeMoney = BigDecimal.ZERO;

        /**
         * 放电金额
         */
        @Schema(description = "充电枪放电金额")
        private BigDecimal disChargeMoney = BigDecimal.ZERO;

    }

}
