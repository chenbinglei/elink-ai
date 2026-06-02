package com.sunmax.device.entity.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 站点关联方信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_affiliates_info")
public class AffiliatesInfoEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 关联方类型 1-用能单位 2-产权单位 3-运营单位 4-建设单位 5-供电单位(多个以逗号分割)
     */
    @Column(name = "affiliate_types", columnDefinition = "varchar(10) not null comment '关联方类型 1-用能单位 2-产权单位 3-运营单位 4-建设单位 5-供电单位(多个以逗号分割)'")
    private String affiliateTypes;

    /**
     * 所属租户id(拥有者企业,关联b_tenant_info租户信息表中唯一id)
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(64) not null comment '所属租户id(拥有者企业,关联b_tenant_info租户信息表中唯一id)'")
    private String tenantId;

    /**
     * 联系人名
     */
    @Column(name = "contacts_name", columnDefinition = "varchar(32) comment '联系人名'")
    private String contactsName;

    /**
     * 联系人电话
     */
    @Column(name = "contacts_phone", columnDefinition = "varchar(32) comment '联系人电话'")
    private String contactsPhone;

    /**
     * 所属站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) not null comment '所属站点id'")
    private String siteId;
}
