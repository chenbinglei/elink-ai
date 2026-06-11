package com.sunmax.configure.dto.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/2016:42
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "直流桩负荷信息实体类")
public class DcEquipmentDynamicInfoDto {

    /**
     * 桩名
     */
    @Schema(description = "桩名")
    @JSONField(name = "EquipmentName")
    private String equipmentName;

    /**
     * 所属站
     */
    @Schema(description = "所属站")
    @JSONField(name = "StationId")
    private String stationId;

    /**
     * 桩有功
     * 浮点4位小数
     */
    @Schema(description = "桩有功")
    @JSONField(name = "EquipmentPower")
    private Double equipmentPower;

    /**
     * SOC
     */
    @Schema(description = "SOC")
    @JSONField(name = "SOC")
    private Integer soc;

    /**
     * 桩输出电压
     * 浮点1位小数
     */
    @Schema(description = "桩输出电压")
    @JSONField(name = "EquipmentVoltage")
    private Double equipmentVoltage;

    /**
     * 桩输出电流
     * 浮点2位小数
     */
    @Schema(description = "桩输出电流")
    @JSONField(name = "EquipmentCurrent")
    private Double equipmentCurrent;

    /**
     * 请求电压
     * 浮点1位小数
     */
    @Schema(description = "请求电压")
    @JSONField(name = "RequestVoltage")
    private Double requestVoltage;

    /**
     * 请求电流
     * 浮点2位小数
     */
    @Schema(description = "请求电流")
    @JSONField(name = "RequestCurrent")
    private Double requestCurrent;

    /**
     * 是否连接电池
     */
    @Schema(description = "是否连接电池")
    @JSONField(name = "IsConnect")
    private Integer isConnect;

    /**
     * 剩余充电时长
     * 单位：min
     */
    @Schema(description = "剩余充电时长")
    @JSONField(name = "ChargingDuration")
    private Integer chargingDuration;

    /**
     * 桩当日零点电量
     * 桩零点电表表底值，单位kwh
     */
    @Schema(description = "桩当日零点电量")
    @JSONField(name = "ZeroElectric")
    private Integer zeroElectric;

    /**
     * 运营系统内部设备id
     */
    @Schema(description = "运营系统内部设备id")
    @JSONField(name = "EquipmentId")
    private String equipmentId;

    /**
     * 所属运营商id
     */
    @Schema(description = "所属运营商id")
    @JSONField(name = "OperatorId")
    private String operatorId;

    /**
     * 上报时间
     */
    @Schema(description = "上报时间")
    @JSONField(name = "UpTime")
    private String upTime;
}
