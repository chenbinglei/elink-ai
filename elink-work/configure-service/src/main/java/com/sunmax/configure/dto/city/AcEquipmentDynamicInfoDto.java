package com.sunmax.configure.dto.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/2016:48
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "AcEquipmentDynamicInfoDto", description = "交流桩负荷信息实体类")
public class AcEquipmentDynamicInfoDto {

    /**
     * 桩名
     */
    @ApiModelProperty(value = "桩名")
    @JSONField(name = "EquipmentName")
    private String equipmentName;

    /**
     * 所属站
     */
    @ApiModelProperty(value = "所属站")
    @JSONField(name = "StationId")
    private String stationId;

    /**
     * 桩有功
     * 浮点4位小数
     */
    @ApiModelProperty(value = "桩有功")
    @JSONField(name = "EquipmentPower")
    private Double equipmentPower;

    /**
     * 桩输出电压
     * 浮点1位小数
     */
    @ApiModelProperty(value = "桩输出电压")
    @JSONField(name = "EquipmentVoltage")
    private Double equipmentVoltage;

    /**
     * 桩输出电流
     * 浮点2位小数
     */
    @ApiModelProperty(value = "桩输出电流")
    @JSONField(name = "EquipmentCurrent")
    private Double equipmentCurrent;

    /**
     * 电压A
     * 浮点1位小数
     */
    @ApiModelProperty(value = "电压A")
    @JSONField(name = "RequestVoltageA")
    private Double requestVoltageA;

    /**
     * 电流A
     * 浮点2位小数
     */
    @ApiModelProperty(value = "电流A")
    @JSONField(name = "RequestCurrentA")
    private Double requestCurrentA;

    /**
     * 电压B
     * 浮点1位小数
     */
    @ApiModelProperty(value = "电压B")
    @JSONField(name = "RequestVoltageB")
    private Double requestVoltageB;

    /**
     * 电流B
     * 浮点2位小数
     */
    @ApiModelProperty(value = "电流B")
    @JSONField(name = "RequestCurrentB")
    private Double requestCurrentB;

    /**
     * 电压C
     * 浮点1位小数
     */
    @ApiModelProperty(value = "电压C")
    @JSONField(name = "RequestVoltageC")
    private Double requestVoltageC;

    /**
     * 电流C
     * 浮点2位小数
     */
    @ApiModelProperty(value = "电流C")
    @JSONField(name = "RequestCurrentC")
    private Double requestCurrentC;

    /**
     * 是否连接电池
     * 0：连接 1：未连接
     */
    @ApiModelProperty(value = "是否连接电池")
    @JSONField(name = "IsConnect")
    private Integer isConnect;

    /**
     * 桩当日零点电量
     * 桩零点电表表底值，单位kwh
     */
    @ApiModelProperty(value = "桩当日零点电量")
    @JSONField(name = "ZeroElectric")
    private Integer zeroElectric;

    /**
     * 运营系统内部设备id
     */
    @ApiModelProperty(value = "运营系统内部设备id")
    @JSONField(name = "EquipmentId")
    private String equipmentId;

    /**
     * 所属运营商id
     */
    @ApiModelProperty(value = "所属运营商id")
    @JSONField(name = "OperatorId")
    private String operatorId;

    /**
     * 上报时间
     */
    @ApiModelProperty(value = "上报时间")
    @JSONField(name = "UpTime")
    private String upTime;
}
