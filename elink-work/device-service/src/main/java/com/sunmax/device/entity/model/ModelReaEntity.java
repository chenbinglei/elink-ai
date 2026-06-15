package com.sunmax.device.entity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 模型扩展属性关联实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_model_rea")
public class ModelReaEntity {

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
     * 关联扩展属性表id
     */
    @Column(name = "rea_id", columnDefinition = "varchar(32) comment '关联扩展属性表id'")
    private String reaId;

//    /**
//     * 读写类型 1-只读 2-读写
//     */
//    @Column(name = "read_write_type", columnDefinition = "tinyint(1) not null comment '读写类型 1-只读 2-读写'")
//    private Integer readWriteType;

    /**
     * 默认值
     */
    @Column(name = "default_value", columnDefinition = "varchar(64) comment '默认值'")
    private String defaultValue;

}
