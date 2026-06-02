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
@Table(name = "b_graph_source")
public class GraphSourceEntity {

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
     * 名称
     */
    @Column(name = "name", columnDefinition = "varchar(64) comment '名称'")
    private String name;

    /**
     * 数据源id
     */
    @Column(name = "data_source_id", columnDefinition = "varchar(32) comment '数据源id'")
    private String dataSourceId;

    /**
     * 请求参数
     */
    @Column(name = "request_value", columnDefinition = "longtext comment '请求参数'")
    private String requestValue;

    /**
     * 响应参数
     */
    @Column(name = "response_value", columnDefinition = "longtext comment '响应参数'")
    private String responseValue;

}
