package com.sunmax.together.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 用户分组和站点关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_group_be_site")
public class GroupBeSiteEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 用户分组id(关联b_user_group表唯一id)
     */
    @Column(name = "group_id",columnDefinition = "varchar(32) comment '用户分组id(关联b_user_group表唯一id)'")
    private String groupId;

    /**
     * 所属站点id(关联b_site_info表唯一id)
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) comment '所属站点id(关联b_site_info表唯一id)'")
    private String siteId;
}
