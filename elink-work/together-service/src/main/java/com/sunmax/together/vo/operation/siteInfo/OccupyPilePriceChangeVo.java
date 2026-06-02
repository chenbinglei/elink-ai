package com.sunmax.together.vo.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "OccupyPilePriceChangeVo", description = "占桩价格编辑参数")
public class OccupyPilePriceChangeVo {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id", required = true)
    private String siteId;

    /**
     * 定价类型 1-全天
     */
    @ApiModelProperty(value = "定价类型 1-全天", required = true)
    private Integer fixedType;

    /**
     * 部分时段价格信息
     */
    @ApiModelProperty(value = "部分时段价格信息", required = true)
    private String partPeriodInfo;

    /**
     * 设备类型 1-直流 2-交流 3-全部
     */
    @ApiModelProperty(value = "设备类型 1-直流 2-交流 3-全部", required = true)
    private Integer deviceType;

    /**
     * 免占桩时长(分钟)
     */
    @ApiModelProperty(value = "免占桩时长(分钟)")
    private Integer avoidDuration;

    /**
     * 配置类型 1-固定价格 2-阶梯价格
     */
    @ApiModelProperty(value = "配置类型 1-固定价格 2-阶梯价格", required = true)
    private Integer configType;

    /**
     * 配置价格数组对象信息字符串
     */
    @ApiModelProperty(value = "配置价格数组对象信息字符串")
    private String configPriceInfoStr;

    /**
     * 配置价格信息对象
     */
    @Data
    public static class ConfigPriceInfo {

        /**
         * 超时时长
         */
        @ApiModelProperty(value = "超时时长")
        private Integer timeoutDuration;

        /**
         * 收费类型 1-分钟计费 2-固定金额
         */
        @ApiModelProperty(value = "收费类型 1-分钟计费 2-固定金额")
        private Integer chargeType;

        /**
         * 收费价格
         */
        @ApiModelProperty(value = "收费价格")
        private BigDecimal chargePrice;
    }
}
