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
 * 充电桩故障实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_model_config")
public class ModelConfigEntity {

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
     * 关联参数配置表id
     */
    @Column(name = "config_id", columnDefinition = "varchar(32) comment '关联参数配置表id'")
    private String configId;

    /**
     * 字段长度
     */
    @Column(name = "field_length", columnDefinition = "int(11) not null comment '字段长度'")
    private Integer fieldLength;

    /**
     * 读写类型 1-只读 2-读写
     */
    @Column(name = "rw_type", columnDefinition = "tinyint(1) not null comment '读写类型 1-只读 2-读写'")
    private Integer rwType;

}
