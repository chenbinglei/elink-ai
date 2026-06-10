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
 * 接收互联互通推送充电状态记录信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_charge_state_record")
public class ChargeStateRecordEntity {

    /**
     * 充电设备接口编码
     *
     * 格式设备编码_接口编码
     */
    @Id
    @Column(name = "connector_id", columnDefinition = "varchar(64) NOT NULL comment '充电设备接口编码(格式设备编码_接口编码)'")
    private String connectorId;

    /**
     * 接收推送数据对象
     */
    @Column(name = "push_data_object", columnDefinition = "longtext comment '接收推送数据对象'")
    private String pushDataObject;

    /**
     * 最后修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '最后修改时间'")
    private LocalDateTime updateTime;
}
