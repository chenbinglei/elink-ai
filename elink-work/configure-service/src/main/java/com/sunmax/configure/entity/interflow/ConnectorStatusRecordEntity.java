package com.sunmax.configure.entity.interflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 接收互联互通推送设备状态变化记录信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_connector_status_record")
public class ConnectorStatusRecordEntity {

    /**
     * 充电设备接口编码
     *
     * 格式设备编码_接口编码
     */
    @Id
    @Column(name = "connector_id", columnDefinition = "varchar(64) NOT NULL comment '充电设备接口编码(格式设备编码_接口编码)'")
    private String connectorId;

    /**
     * 接口状态
     */
    @Column(name = "status", columnDefinition = "int(10) NOT NULL comment '接口状态'")
    private Integer status;

    /**
     * 车位状态
     */
    @Column(name = "park_status", columnDefinition = "int(10) comment '车位状态'")
    private Integer parkStatus;

    /**
     * 地锁状态
     */
    @Column(name = "lock_status", columnDefinition = "int(10) comment '地锁状态'")
    private Integer lockStatus;

    /**
     * 最后修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '最后修改时间'")
    private LocalDateTime updateTime;
}
