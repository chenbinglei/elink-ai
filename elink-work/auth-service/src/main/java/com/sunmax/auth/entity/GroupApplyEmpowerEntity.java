package com.sunmax.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 用户组应用授权配置信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_group_apply_empower")
public class GroupApplyEmpowerEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 所属用户组id
     */
    @Column(name = "group_id", columnDefinition = "varchar(32) comment '所属用户组id'")
    private String groupId;

    /**
     * 所属模块id
     */
    @Column(name = "module_id", columnDefinition = "varchar(32) comment '所属模块id'")
    private String moduleId;

    /**
     * 所属权限id
     */
    @Column(name = "permission_id", columnDefinition = "varchar(32) comment '所属权限id'")
    private String permissionId;

    /**
     * 操作 1-开启 2-关闭
     */
    @Column(name = "operate",columnDefinition = "int(1) comment '操作 1-开启 2-关闭'")
    private Integer operate;
}
