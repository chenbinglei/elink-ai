package com.sunmax.together.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 设备功能点字段设置实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_device_field_set")
public class DeviceFieldSetEntity {

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
     * 设备功能点字段
     */
    @Column(name = "fuction_fields", columnDefinition = "longtext comment '设备功能点字段'")
    private String functionFields;

}
