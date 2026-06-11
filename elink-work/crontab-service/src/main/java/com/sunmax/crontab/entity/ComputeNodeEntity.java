package com.sunmax.crontab.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 计算节点
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_compute_node")
public class ComputeNodeEntity extends BaseEntity {

    /**
     * 存储id
     */
    @Column(name = "storage_id", columnDefinition = "bigint(20) not null comment '存储id'")
    private Long storageId;

    /**
     * 节点编码
     */
    @Column(name = "node_code", columnDefinition = "varchar(50) comment '节点编码'")
    private String nodeCode;

    /**
     * 站点/设备id
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) comment '站点/设备id'")
    private String deviceId;

    /**
     * 所属站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '所属站点id'")
    private String siteId;

    /**
     * 节点名称
     */
    @Column(name = "node_name", columnDefinition = "varchar(50) comment '节点名称'")
    private String nodeName;

    /**
     * 单位
     */
    @Column(name = "unit", columnDefinition = "varchar(20) comment '单位'")
    private String unit;

    /**
     * 实例类型 1-设备类型 2-站点类型
     */
    @Column(name = "example_type", columnDefinition = "tinyint(1) comment '实例类型 1-设备类型 2-站点类型'")
    private Integer exampleType;

    /**
     * 策略类型 1-每次存储 2-变化存储 3-不存储
     */
    @Column(name = "strategy_type", columnDefinition = "tinyint(1) comment '策略类型 1-每次存储 2-变化存储 3-不存储'")
    private Integer strategyType;

    /**
     * 计算公式-前端用
     */
    @Column(name = "formula_front", columnDefinition = "varchar(2048) comment '计算公式-前端用'")
    private String formulaFront;

    /**
     * 计算公式-后端用
     */
    @Column(name = "formula_after", columnDefinition = "varchar(512) comment '计算公式-后端用'")
    private String formulaAfter;

    /**
     * 统计周期 自然年-y 自然月-n 日-d 时-h 分-m
     */
    @Column(name = "count_period", columnDefinition = "varchar(10) not null comment '计算周期 自然年-y 自然月-n 日-d 时-h 分-m'")
    private String countPeriod;

    /**
     * 计算周期 年-y 月-n 日-d 时-h 分-m
     */
    @Column(name = "compute_period", columnDefinition = "varchar(10) not null comment '计算周期 年-y 月-n 日-d 时-h 分-m'")
    private String computePeriod;

    /**
     * 统计周期(cron表达式)
     */
    @Column(name = "cron_expression", columnDefinition = "varchar(50) comment '计算周期(cron表达式)'")
    private String cronExpression;

    /**
     * 开始时间
     */
    @Column(name = "start_time", columnDefinition = "varchar(50) comment '开始时间'")
    private String startTime;

    /**
     * 备注
     */
    @Column(name = "remark", columnDefinition = "varchar(255) comment '备注'")
    private String remark;

}
