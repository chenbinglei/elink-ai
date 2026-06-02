package com.sunmax.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 租户应用授权信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_tenant_apply_empower")
public class TenantApplyEmpowerEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 所属租户id
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(32) comment '所属租户id'")
    private String tenantId;

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
