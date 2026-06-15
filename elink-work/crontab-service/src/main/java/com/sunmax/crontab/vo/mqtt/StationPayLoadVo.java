package com.sunmax.crontab.vo.mqtt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点可调参数")
public class StationPayLoadVo {

    /**
     * 实时总有功功率
     */
    private Double signedActivePower;

    /**
     * 实时正向总有功功率
     */
    private Double activePower;

    /**
     * 实时反向总有功功率
     */
    private Double activePowerRev;

    /**
     * 带符号的实时A相有功功率
     */
    private Double signedActivePowerA;

    /**
     * 实时正向A相有功功率
     */
    private Double activePowerA;

    /**
     * 实时反向A相有功功率
     */
    private Double activePowerARev;

    /**
     * 带符号的实时B相有功功率
     */
    private Double signedActivePowerB;

    /**
     * 实时正向B相有功功率
     */
    private Double activePowerB;

    /**
     * 实时反向B相有功功率
     */
    private Double activePowerBRev;

    /**
     * 带符号的实时C相有功功率
     */
    private Double signedActivePowerC;

    /**
     * 实时正向C相有功功率
     */
    private Double activePowerC;

    /**
     * 实时反向C相有功功率
     */
    private Double activePowerCRev;

    /**
     * 带符号的实时总无功功率
     */
    private Double signedReactivePower;

    /**
     * 实时正向总无功功率
     */
    private Double reactivePower;

    /**
     * 实时反向总无功功率
     */
    private Double reactivePowerRev;

    /**
     * 带符号的实时A相无功功率
     */
    private Double signedReactivePowerA;

    /**
     * 实时正向A相无功功率
     */
    private Double reactivePowerA;

    /**
     * 实时反向A相无功功率
     */
    private Double reactivePowerARev;

    /**
     * 带符号的实时B相无功功率
     */
    private Double signedReactivePowerB;

    /**
     * 实时正向B相无功功率
     */
    private Double reactivePowerB;

    /**
     * 实时反向B相无功功率
     */
    private Double reactivePowerBRev;

    /**
     * 带符号的实时C相无功功率
     */
    private Double signedReactivePowerC;

    /**
     * 实时正向C相无功功率
     */
    private Double reactivePowerC;

    /**
     * 实时反向C相无功功率
     */
    private Double reactivePowerCRev;

    /**
     * 实时充电最大可上调总功率
     */
    private Double maxChargeUp = 0.0;

    /**
     * 实时充电最大可下调总功率
     */
    private Double maxChargeDown = 0.0;

    /**
     * 实时放电最大可上调总功率
     */
    private Double maxDischargeUp = 0.0;

    /**
     * 实时放电最大可下调总功率
     */
    private Double maxDischargeDown = 0.0;

    /**
     * 实时A相电压
     */
    private Double voltageA;

    /**
     * 实时B相电压
     */
    private Double voltageB;

    /**
     * 实时C相电压
     */
    private Double voltageC;

    /**
     * 实时A相电流
     */
    private Double currentA;

    /**
     * 实时B相电流
     */
    private Double currentB;

    /**
     * 实时C相电流
     */
    private Double currentC;

    /**
     * 实时总功率因数
     */
    private Double pf;

    /**
     * 实时A相功率因数
     */
    private Double pfA;

    /**
     * 实时B相功率因数
     */
    private Double pfB;

    /**
     * 实时C相功率因数
     */
    private Double pfC;

    /**
     * 实时电网频率
     */
    private Double frequency;

    /**
     * 带符号的当前历史累计总有功电能
     */
    private Double signedActiveEnergy;

    /**
     * 当前历史累计正向总有功电能
     */
    private Double activeEnergy;

    /**
     * 当前历史累计反向总有功电能
     */
    private Double activeEnergyRev;

    /**
     * 带符号的当前历史累计A相有功电能
     */
    private Double signedActiveEnergyA;

    /**
     * 当前历史累计正向A相有功电能
     */
    private Double activeEnergyA;

    /**
     * 当前历史累计反向A相有功电能
     */
    private Double activeEnergyARev;

    /**
     * 带符号的当前历史累计B相有功电能
     */
    private Double signedActiveEnergyB;

    /**
     * 当前历史累计正向B相有功电能
     */
    private Double activeEnergyB;

    /**
     * 当前历史累计反向B相有功电能
     */
    private Double activeEnergyBRev;

    /**
     * 带符号的当前历史累计C相有功电能
     */
    private Double signedActiveEnergyC;

    /**
     * 当前历史累计正向C相有功电能
     */
    private Double activeEnergyC;

    /**
     * 当前历史累计反向C相有功电能
     */
    private Double activeEnergyCRev;

    /**
     * 带符号的当前历史累计总有功电能
     */
    private Double signedReactiveEnergy;

    /**
     * 当前历史累计正向总有功电能
     */
    private Double reactiveEnergy;

    /**
     * 当前历史累计反向总有功电能
     */
    private Double reactiveEnergyRev;

    /**
     * 带符号的当前历史累计A相有功电能
     */
    private Double signedReactiveEnergyA;

    /**
     * 当前历史累计正向A相有功电能
     */
    private Double reactiveEnergyA;

    /**
     * 当前历史累计反向A相有功电能
     */
    private Double reactiveEnergyARev;

    /**
     * 带符号的当前历史累计B相有功电能
     */
    private Double signedReactiveEnergyB;

    /**
     * 当前历史累计正向B相有功电能
     */
    private Double reactiveEnergyB;

    /**
     * 当前历史累计反向B相有功电能
     */
    private Double reactiveEnergyBRev;

    /**
     * 带符号的当前历史累计C相有功电能
     */
    private Double signedReactiveEnergyC;

    /**
     * 当前历史累计正向C相有功电能
     */
    private Double reactiveEnergyC;

    /**
     * 当前历史累计反向C相有功电能
     */
    private Double reactiveEnergyCRev;

}
