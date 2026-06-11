package com.sunmax.together.vo.operation.siteInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "占桩价格编辑参数")
public class OccupyPilePriceChangeVo {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 定价类型 1-全天
     */
    @Schema(description = "定价类型 1-全天")
    private Integer fixedType;

    /**
     * 部分时段价格信息
     */
    @Schema(description = "部分时段价格信息")
    private String partPeriodInfo;

    /**
     * 设备类型 1-直流 2-交流 3-全部
     */
    @Schema(description = "设备类型 1-直流 2-交流 3-全部")
    private Integer deviceType;

    /**
     * 免占桩时长(分钟)
     */
    @Schema(description = "免占桩时长(分钟)")
    private Integer avoidDuration;

    /**
     * 配置类型 1-固定价格 2-阶梯价格
     */
    @Schema(description = "配置类型 1-固定价格 2-阶梯价格")
    private Integer configType;

    /**
     * 配置价格数组对象信息字符串
     */
    @Schema(description = "配置价格数组对象信息字符串")
    private String configPriceInfoStr;

    /**
     * 配置价格信息对象
     */
    @Data
    public static class ConfigPriceInfo {

        /**
         * 超时时长
         */
        @Schema(description = "超时时长")
        private Integer timeoutDuration;

        /**
         * 收费类型 1-分钟计费 2-固定金额
         */
        @Schema(description = "收费类型 1-分钟计费 2-固定金额")
        private Integer chargeType;

        /**
         * 收费价格
         */
        @Schema(description = "收费价格")
        private BigDecimal chargePrice;
    }
}
