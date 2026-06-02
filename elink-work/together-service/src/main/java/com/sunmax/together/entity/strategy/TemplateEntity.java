package com.sunmax.together.entity.strategy;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * 策略模板实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_template")
public class TemplateEntity extends BaseEntity {

    /**
     * 模板名称
     */
    @Column(name = "template_name", columnDefinition = "varchar(64) comment '模板名称'")
    private String templateName;

    /**
     * 策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略
     */
    @Column(name = "strategy_type", columnDefinition = "tinyint(1) comment '策略类型 策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略'")
    private Integer strategyType;

    /**
     * 策略说明名称
     */
    @Column(name = "explain_name", columnDefinition = "varchar(255) comment '策略说明名称'")
    private String explainName;

    /**
     * 策略说明路径
     */
    @Column(name = "explain_path", columnDefinition = "text comment '策略说明路径'")
    private String explainPath;

    /**
     * 配置文件名称
     */
    @Column(name = "config_name", columnDefinition = "varchar(255) comment '配置文件名称'")
    private String configName;

    /**
     * 配置文件路径
     */
    @Column(name = "config_path", columnDefinition = "text comment '配置文件路径'")
    private String configPath;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;

}
