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
 * 组织架构信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_organ_structure")
public class OrganStructureEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 组织名称
     */
    @Column(name = "organ_name",columnDefinition = "varchar(64) comment '组织名称'")
    private String organName;

    /**
     * 父级id
     */
    @Column(name = "parent_id", columnDefinition = "varchar(32) comment '父级id'")
    private String parentId;

    /**
     * 排序号
     */
    @Column(name = "sort_number",columnDefinition = "int(5) comment '排序号'")
    private Integer sortNumber;

    /**
     * 所属租户id
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(32) comment '所属租户id'")
    private String tenantId;
}
