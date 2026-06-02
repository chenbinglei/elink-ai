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
@Table(name = "b_device_task_record")
public class DeviceTaskRecordEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 任务id
     */
    @Column(name = "task_id", columnDefinition = "varchar(32) comment '任务id'")
    private String taskId;

    /**
     * 站点名称
     */
    @Column(name = "site_name", columnDefinition = "varchar(64) not null comment '站点名称'")
    private String siteName;

    /**
     * 设备名称
     */
    @Column(name = "device_name", columnDefinition = "varchar(64) not null comment '设备名称'")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Column(name = "device_number", columnDefinition = "varchar(64) comment '设备序列号'")
    private String deviceNumber;

    /**
     * 源版本
     */
    @Column(name = "source_version", columnDefinition = "varchar(32) comment '源版本'")
    private String sourceVersion;

    /**
     * 目标版本
     */
    @Column(name = "target_version", columnDefinition = "varchar(32) comment '目标版本'")
    private String targetVersion;

    /**
     * 状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功
     */
    @Column(name = "status", columnDefinition = "tinyint(1) comment '状态 0-无需升级 1-等待启动 2-下载中 3-下载失败 4-升级中 5-升级失败 6-升级成功'")
    private Integer status;

    /**
     * 原因
     */
    @Column(name = "reason", columnDefinition = "varchar(255) comment '原因'")
    private String reason;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;

    /**
     * 升级时间
     */
    @Column(name = "upgrade_time", columnDefinition = "datetime(0) comment '升级时间'")
    private LocalDateTime upgradeTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time", columnDefinition = "datetime(0) comment '结束时间'")
    private LocalDateTime endTime;

}
