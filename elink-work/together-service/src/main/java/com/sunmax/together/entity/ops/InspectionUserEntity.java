package com.sunmax.together.entity.ops;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 巡检节点人员实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_inspection_user")
public class InspectionUserEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 租户id
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(32) comment '租户id'")
    private String tenantId;

    /**
     * 节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认
     */
    @Column(name = "type", columnDefinition = "int(2) comment '节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认'")
    private Integer type;

    /**
     * 多个用户id
     */
    @Column(name = "user_ids", columnDefinition = "longtext comment '多个用户id 例如[用户id1,用户id2]'")
    private String userIds;

    /**
     * 最后修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '最后修改时间'")
    private LocalDateTime updateTime;

}
