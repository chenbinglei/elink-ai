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
 * 图形实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_graph")
public class GraphEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 设备id
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) comment '设备id'")
    private String deviceId;

    /**
     * 图形分类id
     */
    @Column(name = "graph_type_id", columnDefinition = "varchar(32) comment '图形分类id'")
    private String graphTypeId;

    /**
     * 图形名称
     */
    @Column(name = "graph_name", columnDefinition = "varchar(64) comment '图形名称'")
    private String graphName;

    /**
     * 图形URL
     */
    @Column(name = "graph_url", columnDefinition = "varchar(255) comment '图形URL'")
    private String graphUrl;

    /**
     * 默认图形 1-默认 2-不默认
     */
    @Column(name = "is_default", columnDefinition = "tinyint(1) comment '默认图形 1-默认 2-不默认'")
    private Integer isDefault;

}
