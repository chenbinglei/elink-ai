package com.sunmax.device.entity.access;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_device_gun")
public class DeviceGunEntity extends BaseTimeEntity {

    /**
     * 充电枪编号
     */
    @Column(name = "gun_code", columnDefinition = "varchar(32) NOT NULL comment '充电枪编号'")
    private String gunCode;

    /**
     * 充电枪名称
     */
    @Column(name = "gun_name", columnDefinition = "varchar(32) comment '充电枪名称'")
    private String gunName;

    /**
     * 充电枪类型
     * 1：家用插座（模式 2）
     * 2：交流接口插座（模式3， 连接方式 B ）
     * 3：交流接口插头（带枪线，模式 3，连接方式C）
     * 4：直流接口枪头（带枪线，模式 4）
     */
    @Column(name = "type", columnDefinition = "tinyint(1) comment '充电枪类型 1-家用插座(模式2) 2-交流接口插座(模式3，连接方式B) 3-交流接口插头(带枪线，模式3，连接方式C) 4-直流接口枪头(带枪线，模式 4)'")
    private Integer type;

    /**
     * 浙江省充电设备接口唯一码
     */
    @Column(name = "connector_unique_id", columnDefinition = "varchar(28) comment '浙江省充电设备接口唯一码'")
    private String connectorUniqueId;

    /**
     * 外观
     */
    @Column(name = "appearance", columnDefinition = "varchar(20) comment '外观 例如黑白'")
    private String appearance;

    /**
     * 防护等级
     */
    @Column(name = "ipGrade", columnDefinition = "varchar(32) comment '防护等级'")
    private String ipGrade;

    /**
     * 额定电流 单位A
     */
    @Column(name = "rated_current", columnDefinition = "int(10) comment '额定电流 单位A'")
    private Integer ratedCurrent;

    /**
     * 额定功率 单位kW
     */
    @Column(name = "rated_power", columnDefinition = "double(9,1) comment '额定功率 单位kW'")
    private Double ratedPower;

    /**
     * 额定电压上限 单位V
     */
    @Column(name = "voltage_upper_limits", columnDefinition = "int(10) comment '额定电压上限 单位V'")
    private Integer voltageUpperLimits;

    /**
     * 额定电压下限 单位V
     */
    @Column(name = "voltage_lower_limits", columnDefinition = "int(10) comment '额定电压下限 单位V'")
    private Integer voltageLowerLimits;

    /**
     * 车位号
     */
    @Column(name = "park_no", columnDefinition = "varchar(20) comment '车位号'")
    private String parkNo;

    /**
     * 国家标准 1:2011 2:2015
     */
    @Column(name = "national_standard", columnDefinition = "tinyint(1) comment '国家标准 1-2011 2-2015'")
    private Integer nationalStandard;

    /**
     * 二维码解析地址清单
     */
    @Column(name = "qr_codes", columnDefinition = "varchar(255) comment '二维码解析地址清单'")
    private String qrCodes;

    /**
     * 辅助电源 1-12V 2-24V 3-兼容12V和24V
     */
    @Column(name = "auxPower", columnDefinition = "tinyint(1) comment '辅助电源'")
    private Integer auxPower;

    /**
     * 关联设备id
     */
    @Column(name = "device_id", columnDefinition = "varchar(64) NOT NULL comment '关联设备id'")
    private String deviceId;

}
