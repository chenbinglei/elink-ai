package com.sunmax.crontab.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点日志信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_node_log_info")
public class NodeLogInfoEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

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
     * 日志类型 1-定时任务 2-数据补录
     */
    @Column(name = "log_type", columnDefinition = "tinyint(1) comment '日志类型 1-定时任务 2-数据补录'")
    private Integer logType;

    /**
     * 数据点时间
     */
    @Column(name = "ts_time", columnDefinition = "varchar(50) comment '数据点时间'")
    private String tsTime;

    /**
     * 日志时间
     */
    @Column(name = "log_time", columnDefinition = "datetime(0) comment '日志时间'")
    public LocalDateTime logTime;

    /**
     * 日志级别 1-error 2-warning
     */
    @Column(name = "log_level", columnDefinition = "tinyint(1) comment '日志级别 1-error 2-warning'")
    private Integer logLevel;

    /**
     * 日志内容
     */
    @Column(name = "log_info", columnDefinition = "varchar(500) comment '日志内容'")
    private String logInfo;
}
