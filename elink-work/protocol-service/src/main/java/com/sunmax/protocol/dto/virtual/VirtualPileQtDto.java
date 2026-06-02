package com.sunmax.protocol.dto.virtual;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * 电桩电量数据返回实体类
 */
@Data
@ApiModel(value = "PileQtDto", description = "电桩电量数据返回实体类")
public class VirtualPileQtDto {

    /**
     * 充电桩编号
     */
    @ApiModelProperty(value = "充电桩编号")
    private String pileCode;

    /**
     * 充电量
     */
    @ApiModelProperty(value = "充电桩充电量")
    private Double recChargeQt = 0.0;

    /**
     * 放电量
     */
    @ApiModelProperty(value = "充电桩放电量")
    private Double disChargeQt = 0.0;

    /**
     * 充电金额
     */
    @ApiModelProperty(value = "充电桩充电金额")
    private BigDecimal recChargeMoney = BigDecimal.ZERO;

    /**
     * 放电金额
     */
    @ApiModelProperty(value = "充电桩放电金额")
    private BigDecimal disChargeMoney = BigDecimal.ZERO;

    /**
     * 充电枪实时数据列表
     */
    @ApiModelProperty(value = "充电枪电量数据列表")
    private List<VirtualPileQtDto.GunQtData> gunQtDataList = Lists.newArrayList();

    @Data
    @ApiModel(value = "GunQtData", description = "充电枪电量数据")
    public static class GunQtData {

        /**
         * 枪编号
         */
        @ApiModelProperty(value = "枪编号")
        private Integer gunCode;

        /**
         * 充电量
         */
        @ApiModelProperty(value = "充电枪充电量")
        private Double recChargeQt = 0.0;

        /**
         * 放电量
         */
        @ApiModelProperty(value = "充电枪放电量")
        private Double disChargeQt = 0.0;

        /**
         * 充电金额
         */
        @ApiModelProperty(value = "充电枪充电金额")
        private BigDecimal recChargeMoney = BigDecimal.ZERO;

        /**
         * 放电金额
         */
        @ApiModelProperty(value = "充电枪放电金额")
        private BigDecimal disChargeMoney = BigDecimal.ZERO;

    }

}
