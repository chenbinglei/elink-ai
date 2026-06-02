package com.sunmax.configure.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_variable")
public class VariableEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 关联图模id
     */
    @Column(name = "graph_id", columnDefinition = "varchar(32) comment '关联图模id'")
    private String graphId;

    /**
     * 变量名
     */
    @Column(name = "name", columnDefinition = "varchar(64) comment '变量名'")
    private String name;

    /**
     * 变量类型
     */
    @Column(name = "type", columnDefinition = "varchar(64) comment '变量类型'")
    private String type;

    /**
     * 关联图形数据源id
     */
    @Column(name = "graph_source_id", columnDefinition = "varchar(32) comment '关联图形数据源id'")
    private String graphSourceId;

    /**
     * 数据对象
     */
    @Column(name = "data_object", columnDefinition = "varchar(255) comment '数据对象'")
    private String dataObject;

    /**
     * 数据点
     */
    @Column(name = "data_point", columnDefinition = "varchar(255) comment '数据点'")
    private String dataPoint;

    /**
     * 数据点下标
     */
    @Column(name = "data_point_index", columnDefinition = "varchar(64) comment '数据点下标'")
    private String dataPointIndex;

    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "varchar(255) comment '描述'")
    private String description;

}
