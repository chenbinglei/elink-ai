package com.sunmax.device.entity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 模型拓扑点实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_model_topology")
public class ModelTopologyEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 关联模型表id
     */
    @Column(name = "model_id", columnDefinition = "varchar(32) comment '关联模型表id'")
    private String modelId;

    /**
     * 节点名称
     */
    @Column(name = "node_name", columnDefinition = "varchar(64) comment '节点名称'")
    private String nodeName;

}
