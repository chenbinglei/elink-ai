package com.sunmax.together.entity.ops;

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
 * 巡检项实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_inspection_task")
public class InspectionTaskEntity extends BaseTimeEntity {

    /**
     * 租户id
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(32) comment '租户id'")
    private String tenantId;

    /**
     * 任务名称
     */
    @Column(name = "task_name", columnDefinition = "varchar(32) NOT NULL comment '任务名称'")
    private String taskName;

    /**
     * 任务描述
     */
    @Column(name = "task_desc", columnDefinition = "varchar(255) comment '任务描述'")
    private String taskDesc;

    /**
     * 当前处理人用户id
     */
    @Column(name = "user_id", columnDefinition = "varchar(32) comment '当前处理人用户id'")
    private String userId;

    /**
     * 任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结
     */
    @Column(name = "task_status", columnDefinition = "tinyint(1) comment '任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结'")
    private Integer taskStatus;

}
