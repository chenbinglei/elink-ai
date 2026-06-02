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
 * 设备拓扑点实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_device_topology")
public class DeviceTopologyEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 左边设备id
     */
    @Column(name = "left_device_id", columnDefinition = "varchar(32) comment '左边设备id'")
    private String leftDeviceId;

    /**
     * 左边节点id
     */
    @Column(name = "left_node_id", columnDefinition = "varchar(32) comment '左边节点id'")
    private String leftNodeId;

    /**
     * 右边设备id
     */
    @Column(name = "right_device_id", columnDefinition = "varchar(32) comment '右边设备id'")
    private String rightDeviceId;

    /**
     * 右边节点id
     */
    @Column(name = "right_node_id", columnDefinition = "varchar(32) comment '右边节点id'")
    private String rightNodeId;

}
