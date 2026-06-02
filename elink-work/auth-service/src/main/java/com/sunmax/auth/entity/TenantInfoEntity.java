package com.sunmax.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

/**
 * 租户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_tenant_info")
public class TenantInfoEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

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

    /**
     * 创建人员名称
     */
    @Column(name = "create_user_name",columnDefinition = "varchar(20) comment '创建人员名称'")
    private String createUserName;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime createTime;

    /**
     * 修改人员名称
     */
    @Column(name = "update_user_name",columnDefinition = "varchar(20) comment '修改人员名称'")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime updateTime;
}
