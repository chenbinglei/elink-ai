package com.sunmax.configure.entity.interflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 接收互联互通推送充电订单记录信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_charge_order_record")
public class ChargeOrderRecordEntity {

    /**
     * 充电订单号 (是)
     */
    @Id
    @Column(name = "start_charge_seq", columnDefinition = "varchar(32) NOT NULL comment '充电订单号'")
    private String startChargeSeq;

    /**
     * 接口编码 (是)
     */
    @Column(name = "connector_id", columnDefinition = "varchar(64) NOT NULL comment '充电设备接口编码(格式设备编码_接口编码)'")
    private String connectorId;

    /**
     * 充电开始时间 (是)
     */
    @Column(name = "start_time", columnDefinition = "varchar(64) NOT NULL comment '充电开始时间'")
    private String startTime;

    /**
     * 充电结束时间 (是)
     */
    @Column(name = "end_time", columnDefinition = "varchar(64) NOT NULL comment '充电结束时间'")
    private String endTime;

    /**
     * 累计充电量 (是)
     */
    @Column(name = "total_power", columnDefinition = "double(9,2) NOT NULL comment '累计充电量'")
    @Builder.Default
    private Double totalPower = 0.0;

    /**
     * 总电费 (是)
     */
    @Column(name = "total_elec_money", columnDefinition = "double(9,2) NOT NULL comment '总电费'")
    @Builder.Default
    private Double totalElecMoney = 0.0;

    /**
     * 总服务费 (是)
     */
    @Column(name = "total_sevice_money", columnDefinition = "double(9,2) NOT NULL comment '总服务费'")
    @Builder.Default
    private Double totalSeviceMoney = 0.0;

    /**
     * 累计总金额 (是)
     */
    @Column(name = "total_money", columnDefinition = "double(9,2) NOT NULL comment '累计总金额'")
    @Builder.Default
    private Double totalMoney = 0.0;

    /**
     * 充电结束原因 (是)
     *
     * 0：用户手动停止充电；
     * 1：客户归属地运营商平台停止充电；
     * 2： BMS 停止充电；
     * 3：充电机设备故障；
     * 4：连接器断开；
     * 大于 5,自定义，未知情况
     */
    @Column(name = "stop_reason", columnDefinition = "int(10) NOT NULL comment '充电结束原因'")
    private Integer stopReason;

    /**
     * 时段数N (否)
     */
    @Column(name = "sum_period", columnDefinition = "int(10) comment '时段数N'")
    private Integer sumPeriod;

    /**
     * Vin码 (否)
     */
    @Column(name = "vin", columnDefinition = "varchar(20) comment 'Vin码'")
    private String vin;

    /**
     * 车牌号 (否)
     */
    @Column(name = "license_plate", columnDefinition = "varchar(32) comment '车牌号'")
    private String licensePlate;

    /**
     * 充电明细信息对象
     */
    @Column(name = "charge_details_object", columnDefinition = "longtext comment '充电明细信息对象'")
    private String chargeDetailsObject;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '最后修改时间'")
    private LocalDateTime updateTime;
}
