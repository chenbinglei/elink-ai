package com.sunmax.system.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

/**
 * 租户信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_tenant_info")
public class TenantInfoEntity extends BaseEntity {

    /**
     * 租户名称
     */
    @Column(name = "tenant_name",columnDefinition = "varchar(64) comment '租户名称'")
    private String tenantName;

    /**
     * 超管账号
     */
    @Column(name = "super_account",columnDefinition = "varchar(32) comment '超管账号'")
    private String superAccount;

    /**
     * 超管密码
     */
    @Column(name = "password",columnDefinition = "varchar(64) comment '超管密码'")
    private String password;

    /**
     * 租户状态 0-关闭 1-开启
     */
    @Column(name = "tenant_state",columnDefinition = "tinyint(1) comment '租户状态 0-关闭 1-开启'")
    private Integer tenantState;

    /**
     * 地址
     */
    @Column(name = "address",columnDefinition = "varchar(256) comment '地址'")
    private String address;

    /**
     * 组织机构代码
     */
    @Column(name = "organization_code",columnDefinition = "varchar(64) comment '组织机构代码'")
    private String organizationCode;

    /**
     * 营业执照
     */
    @Column(name = "business_license",columnDefinition = "varchar(2048) comment '营业执照'")
    private String businessLicense;

    /**
     * 描述
     */
    @Column(name = "refer",columnDefinition = "varchar(64) comment '描述'")
    private String refer;

    /**
     * 标识
     */
    @Column(name = "logo",columnDefinition = "varchar(1024) comment '标识'")
    private String logo;
}
