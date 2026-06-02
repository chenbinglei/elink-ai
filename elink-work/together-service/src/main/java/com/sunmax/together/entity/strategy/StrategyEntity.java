package com.sunmax.together.entity.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

/**
 * 策略管理实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_strategy")
public class StrategyEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 站点记录id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 设备id
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) comment '设备id'")
    private String deviceId;

    /**
     * 策略模板id
     */
    @Column(name = "template_id", columnDefinition = "varchar(32) comment '策略模板id'")
    private String templateId;

    /**
     * 策略名称
     */
    @Column(name = "strategy_name", columnDefinition = "varchar(64) comment '策略名称'")
    private String strategyName;

    /**
     * 策略类型 1-边缘网关 2-云网关 3-云平台
     */
    @Column(name = "strategy_type", columnDefinition = "tinyint(1) comment '策略类型 1-边缘网关 2-云网关 3-云平台'")
    private Integer strategyType;

    /**
     * 执行状态 0-未下发 1-已下发
     */
    @Column(name = "execute_status", columnDefinition = "tinyint(1) comment '执行状态 0-未下发 1-已下发'")
    private Integer executeStatus;

    /**
     * 配置文件内容
     */
    @Column(name = "config_content", columnDefinition = "longtext comment '配置文件内容'")
    private String configContent;

    /**
     * 下发配置名称
     */
    @Column(name = "config_name", columnDefinition = "varchar(255) comment '下发配置名称'")
    private String configName;

    /**
     * 下发配置参数
     */
    @Column(name = "config_param", columnDefinition = "longtext comment '下发配置参数'")
    private String configParam;

    /**
     * 下发时间
     */
    @Column(name = "issued_time", columnDefinition = "varchar(20) comment '下发时间'")
    private String issueTime;

    /**
     * 策略代理id
     */
    @Column(name = "policy_id", columnDefinition = "int(20) comment '策略代理id'")
    private Long policyId;

    /**
     * 修改人id
     */
    @Column(name = "update_id", columnDefinition = "varchar(32) comment '修改人id'")
    private String updateId;

    /**
     * 修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '最后修改时间'")
    private LocalDateTime updateTime;

}
