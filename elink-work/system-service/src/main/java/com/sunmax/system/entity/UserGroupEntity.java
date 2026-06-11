package com.sunmax.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 用户组表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_user_group")
public class UserGroupEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 用户组名称
     */
    @Column(name = "group_name",columnDefinition = "varchar(64) comment '用户组名称'")
    private String groupName;

    /**
     * 描述
     */
    @Column(name = "refer",columnDefinition = "varchar(64) comment '描述'")
    private String refer;

    /**
     * 所属租户id
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(32) comment '所属租户id'")
    private String tenantId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime updateTime;
}
