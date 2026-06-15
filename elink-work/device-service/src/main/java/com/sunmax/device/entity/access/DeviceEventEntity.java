package com.sunmax.device.entity.access;

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
 * 设备事件实体类
 */

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_device_event")
public class DeviceEventEntity extends BaseTimeEntity {

    /**
     * 关联设备id
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) comment '关联设备id'")
    private String deviceId;

    /**
     * 关联模型事件id
     */
    @Column(name = "event_id", columnDefinition = "varchar(32) comment '关联模型事件id'")
    private String eventId;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @Column(name = "event_status", columnDefinition = "tinyint(1) comment '事件状态 0-未恢复 1-已修复'")
    private Integer eventStatus;

    /**
     * 事件来源
     */
    @Column(name = "event_source", columnDefinition = "longtext comment '事件来源'")
    private String eventSource;

    /**
     * 忽略状态 0-未忽略 1-已忽略
     */
    @Column(name = "ignore_status", columnDefinition = "tinyint(1) comment '忽略状态 0-未忽略 1-已忽略'")
    private Integer ignoreStatus;

}
