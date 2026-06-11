package com.sunmax.device.entity.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 图形分类实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_graph_type")
public class GraphTypeEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 关联资产分类id
     */
    @Column(name = "type_id", columnDefinition = "varchar(32) not null comment '关联资产分类id'")
    private String typeId;

    /**
     * 图形分类编码
     */
    @Column(name = "code", columnDefinition = "varchar(32) not null comment '图形分类编码'")
    private String code;

    /**
     * 图形分类名称
     */
    @Column(name = "name", columnDefinition = "varchar(32) not null comment '图形分类名称'")
    private String name;

}
