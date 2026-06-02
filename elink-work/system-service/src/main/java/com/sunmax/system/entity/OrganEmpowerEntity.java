package com.sunmax.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 资产授权信息表(组织架构和资产关联)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_organ_empower")
public class OrganEmpowerEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 组织架构id
     */
    @Column(name = "organ_id", columnDefinition = "varchar(32) comment '组织架构id'")
    private String organId;

    /**
     * 权限 1-只读 2-读写
     */
    @Column(name = "authority",columnDefinition = "int(1) comment '权限 1-只读 2-读写'")
    private Integer authority;

    /**
     * 所属租户id
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(32) comment '所属租户id'")
    private String tenantId;
}
