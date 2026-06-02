package com.sunmax.crontab.dto;

import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * 站点可调功率实体类
 */
@Data
public class StationAdjustPowerDto {

    /**
     * 站点当前总功率
     */
    private BigDecimal curP = new BigDecimal("0.0");

    /**
     * 站点占用功率
     */
    private BigDecimal occupyP = new BigDecimal("0.0");

    /**
     * 站点充电可上调功率
     */
    private BigDecimal chargeUpAdjustP = new BigDecimal("0.0");

    /**
     * 站点充电可下调功率
     */
    private BigDecimal chargeDownAdjustP = new BigDecimal("0.0");

    /**
     * 站点放电可上调功率
     */
    private BigDecimal dischargeUpAdjustP = new BigDecimal("0.0");

    /**
     * 站点放电可下调功率
     */
    private BigDecimal dischargeDownAdjustP = new BigDecimal("0.0");

    /**
     * 电枪可调功率列表
     */
    private List<PileGunAdjustPDto> pileGunAdjustPList = Lists.newArrayList();

}
