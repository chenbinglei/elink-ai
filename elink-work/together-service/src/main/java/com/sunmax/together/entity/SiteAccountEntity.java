package com.sunmax.together.entity;

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
 * 站点账户信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_account")
public class SiteAccountEntity extends BaseTimeEntity {

    /**
     * 站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 租户id
     */
    @Column(name = "tenant_id",columnDefinition = "varchar(32) comment '租户id'")
    private String tenantId;

    /**
     * 支付平台 1-微信
     */
    @Column(name = "pay_platform",columnDefinition = "tinyint(1) comment '支付平台 1-微信'")
    private Integer payPlatform;

    /**
     * 租户账号id
     */
    @Column(name = "account_id",columnDefinition = "varchar(32) comment '租户账号id'")
    private String accountId;

    /**
     * 类型 1-收款账户 2-付款账户 3-分帐账户
     */
    @Column(name = "type",columnDefinition = "tinyint(1) comment '类型 1-收款账户 2-付款账户 3-分帐账户'")
    private Integer type;

    /**
     * 比例(%)
     */
    @Column(name = "ratio",columnDefinition = "double(9,2) comment '比例(%)'")
    private Double ratio;

}
