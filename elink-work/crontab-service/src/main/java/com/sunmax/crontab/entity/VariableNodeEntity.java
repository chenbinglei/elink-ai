package com.sunmax.crontab.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 变量和节点关联实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "n_variable_node")
public class VariableNodeEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 变量id
     */
    @Column(name = "var_id", columnDefinition = "varchar(32) comment '变量id'")
    private String varId;

    /**
     * 设备/站点/模型id(如果数据来源是模型功能点，则这里就是模型id)
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) comment '设备/站点/模型id(如果数据来源是模型功能点，则这里就是模型id)'")
    private String deviceId;

    /**
     * 节点id
     */
    @Column(name = "node_id", columnDefinition = "varchar(32) comment '节点id'")
    private String nodeId;

    /**
     * 存储id
     */
    @Column(name = "storage_id", columnDefinition = "bigint(20) comment '存储id'")
    private Long storageId;

    /**
     * 功能点id
     */
    @Column(name = "function_id", columnDefinition = "varchar(32) comment '功能点id'")
    private String functionId;
}
