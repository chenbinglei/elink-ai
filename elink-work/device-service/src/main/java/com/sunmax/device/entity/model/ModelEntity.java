package com.sunmax.device.entity.model;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * 模型设备实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_model")
public class ModelEntity extends BaseEntity {

    /**
     * 设备类型id
     */
    @Column(name = "type_id", columnDefinition = "varchar(32) not null comment '设备类型id'")
    private String typeId;

    /**
     * 模型名称
     */
    @Column(name = "model_name", columnDefinition = "varchar(32) comment '模型名称'")
    private String modelName;

    /**
     * 模型状态 0-开发中 1-已发布
     */
    @Column(name = "model_status", columnDefinition = "tinyint(1) not null comment '模型状态 0-开发中 1-已发布'")
    private Integer modelStatus;

    /**
     * 模型描述
     */
    @Column(name = "model_desc", columnDefinition = "varchar(255) comment '模型描述'")
    private String modelDesc;

//    /**
//     * 枪数量
//     */
//    @Column(name = "gun_num", columnDefinition = "int(6) comment '枪数量'")
//    private Integer gunNum;

    /**
     * logo路径
     */
    @Column(name = "logo_path", columnDefinition = "varchar(255) comment 'logo路径'")
    private String logoPath;

}
