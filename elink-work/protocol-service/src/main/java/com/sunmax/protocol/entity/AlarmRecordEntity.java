package com.sunmax.protocol.entity;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * 告警原始记录表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "b_alarm_record")
public class AlarmRecordEntity extends BaseTimeEntity {

    /**
     * 设备编号
     */
    @Column(name = "device_code", columnDefinition = "varchar(64) comment '设备编号'")
    private String deviceCode;

    /**
     * 枪编号
     */
    @Column(name = "gun_code", columnDefinition = "varchar(32) comment '枪编号'")
    private String gunCode;

    /**
     * 故障码
     */
    @Column(name = "fault_code", columnDefinition = "int(10) comment '故障码'")
    private Integer faultCode;

    /**
     * 特征码
     */
    @Column(name = "feature_code", columnDefinition = "bigint(20) comment '特征码'")
    private Long featureCode;

    /**
     * 电力模块地址
     */
    @Column(name = "module_addr", columnDefinition = "bigint(20) comment '电力模块地址'")
    private Integer moduleAddr;

    /**
     * 事件id
     */
    @Column(name = "event_id", columnDefinition = "varchar(32) comment '事件id'")
    private String eventId;

    /**
     * 事件名称
     */
    @Column(name = "event_name", columnDefinition = "varchar(64) comment '事件名称'")
    private String eventName;

    /**
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @Column(name = "event_level", columnDefinition = "tinyint(1) comment '事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警'")
    private Integer eventLevel;

    /**
     * 告警状态 0-未修复 1-已修复
     */
    @Column(name = "alarm_status", columnDefinition = "tinyint(1) comment '告警状态 0-未修复 1-已修复'")
    private Integer alarmStatus;

    /**
     * 告警类型 1-通道类告警 2-电桩类告警
     */
    @Column(name = "alarm_type", columnDefinition = "tinyint(1) comment '告警类型 1-通道类告警 2-电桩类告警'")
    private Integer alarmType;

    /**
     * 忽略状态 0-未忽略 1-已忽略
     */
    @Column(name = "ignore_status", columnDefinition = "tinyint(1) comment '忽略状态 0-未忽略 1-已忽略'")
    private Integer ignoreStatus;

}
