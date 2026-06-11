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
 * 站点白名单模式设置表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_roster_mode")
public class SiteRosterModeEntity {

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
     * 名单模式 1-仅白名单用户可用 2-白名单用户免费充电
     */
    @Column(name = "roster_mode",columnDefinition = "tinyint(1) NOT NULL comment '名单模式 1-仅白名单用户可用 2-白名单用户免费充电'")
    private Integer rosterMode;
}
