package com.sunmax.device.entity.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_device_task")
public class DeviceTaskEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 任务名称
     */
    @Column(name = "task_name", columnDefinition = "varchar(64) comment '任务名称'")
    private String taskName;

    /**
     * 任务状态 1-待执行 2-执行中 3-执行关闭
     */
    @Column(name = "task_status", columnDefinition = "tinyint(1) comment '任务状态 1-待执行 2-执行中 3-执行关闭'")
    private Integer taskStatus;

    /**
     * 任务描述
     */
    @Column(name = "task_desc", columnDefinition = "varchar(255) comment '任务描述'")
    private String taskDesc;

    /**
     * 类型id
     */
    @Column(name = "type_id",columnDefinition = "varchar(32) comment '类型id'")
    private String typeId;

    /**
     * 设备型号
     */
    @Column(name = "equipment_model",columnDefinition = "varchar(255) comment '设备型号'")
    private String equipmentModel;

    /**
     * 固件类型
     * 1-V2G_1.0 TCP控制板
     * 2-V2G_2.0 TCP控制板
     * 3-V2G_3.0 TCP控制板
     * 4-V2G_4.0 TPU控制板
     * 5-V2G_4.0 CCU控制板
     * 6-V2G_6.0 TCP控制板
     * 7-V2G_7.0 TPU控制板
     * 8-V2G_8.0 CCU控制板
     */
    @Column(name = "firmware_type",columnDefinition = "int(3) comment '固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_8.0 CCU控制板'")
    private Integer firmwareType;

    /**
     * 目标版本号
     */
    @Column(name = "target_version",columnDefinition = "varchar(32) comment '目标版本号'")
    private String targetVersion;

    /**
     * 创建人id
     */
    @Column(name = "create_id", columnDefinition = "varchar(32) comment '创建人id'", updatable = false)
    private String createId;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;

}
