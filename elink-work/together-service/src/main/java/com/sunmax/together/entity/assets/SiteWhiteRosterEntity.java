package com.sunmax.together.entity.assets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 站点白名单信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_white_roster")
public class SiteWhiteRosterEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 所属站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) NOT NULL comment '所属站点id'")
    private String siteId;

    /**
     * 鉴权类型 1-用户 2-车辆
     */
    @Column(name = "authority_type",columnDefinition = "tinyint(1) NOT NULL comment '鉴权类型 1-用户 2-车辆'")
    private Integer authorityType;

    /**
     * 鉴权账户
     */
    @Column(name = "authority_account",columnDefinition = "varchar(50) NOT NULL comment '鉴权账户'")
    private String authorityAccount;

    /**
     * 备注
     */
    @Column(name = "notes",columnDefinition = "varchar(255) comment '备注'")
    private String notes;
}
