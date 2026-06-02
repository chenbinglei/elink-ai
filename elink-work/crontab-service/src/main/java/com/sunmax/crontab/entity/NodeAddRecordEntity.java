package com.sunmax.crontab.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点数据补录表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_node_add_record")
public class NodeAddRecordEntity extends BaseEntity {

    /**
     * 设备id
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) comment '设备id'")
    private String deviceId;

    /**
     * 节点id
     */
    @Column(name = "node_id", columnDefinition = "varchar(32) comment '节点id'")
    private String nodeId;

    /**
     * 节点存储id
     */
    @Column(name = "storage_id", columnDefinition = "bigint(20) comment '节点存储id'")
    private Long storageId;

    /**
     * 开始时间
     */
    @Column(name = "start_time", columnDefinition = "varchar(50) comment '开始时间'")
    private String startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time", columnDefinition = "varchar(50) comment '结束时间'")
    private String endTime;

    /**
     * 补录状态 1-执行中 2-已完成
     */
    @Column(name = "add_record_state", columnDefinition = "tinyint(1) comment '补录状态 1-执行中 2-已完成'")
    private Integer addRecordState;
}
