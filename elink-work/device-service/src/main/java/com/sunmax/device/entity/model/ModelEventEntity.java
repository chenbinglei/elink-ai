package com.sunmax.device.entity.model;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * 模型告警事件实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_model_event")
public class ModelEventEntity extends BaseTimeEntity {

    /**
     * 关联模型表id
     */
    @Column(name = "model_id", columnDefinition = "varchar(32) comment '关联模型表id'")
    private String modelId;

    /**
     * 事件名称
     */
    @Column(name = "event_name", columnDefinition = "varchar(64) not null comment '事件名称'")
    private String eventName;

    /**
     * 事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
     */
    @Column(name = "event_level", columnDefinition = "tinyint(1) not null comment '事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警'")
    private Integer eventLevel;

    /**
     * 计算类型 1-值运算 2-位运算
     */
    @Column(name = "calculate_type", columnDefinition = "tinyint(1) not null comment '计算类型 1-值运算 2-位运算'")
    private Integer calculateType;

    /**
     * 多个功能点标识
     */
    @Column(name = "function_logos", columnDefinition = "text comment '多个功能点标识'")
    private String functionLogos;

    /**
     * 存储数据
     */
    @Column(name = "store_data", columnDefinition = "longtext not null comment '存储数据'")
    private String storeData;

    /**
     * 展示数据
     */
    @Column(name = "show_data", columnDefinition = "longtext comment '展示数据'")
    private String showData;

    /**
     * 是否允许解除 1-允许 2-不允许
     */
    @Column(name = "is_allow", columnDefinition = "tinyint(1) comment '是否允许解除 1-允许 2-不允许'")
    private Integer isAllow;

    /**
     * 事件通知
     */
    @Column(name = "event_inform", columnDefinition = "longtext comment '事件通知'")
    private String eventInform;

    /**
     * 事件描述
     */
    @Column(name = "event_desc", columnDefinition = "varchar(255) comment '事件描述'")
    private String eventDesc;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;

}
