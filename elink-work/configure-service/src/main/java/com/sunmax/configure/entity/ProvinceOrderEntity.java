package com.sunmax.configure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_province_order")
public class ProvinceOrderEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 站点编号
     */
    @Column(name = "station_id", columnDefinition = "varchar(64) comment '站点编号'")
    private String stationId;

    /**
     * 电桩编号
     */
    @Column(name = "pile_code", columnDefinition = "varchar(64) comment '电桩编号'")
    private String pileCode;

    /**
     * 枪编号
     */
    @Column(name = "gun_code", columnDefinition = "varchar(64) comment '枪编号'")
    private String gunCode;

    /**
     * 订单编号
     */
    @Column(name = "order_num", columnDefinition = "varchar(64) comment '订单编号'")
    private String orderNum;

    /**
     * 电量
     */
    @Column(name = "charge_qt", columnDefinition = "double(9,4) comment '电量'")
    private Double chargeQt;

    /**
     * 金额
     */
    @Column(name = "charge_money", columnDefinition = "double(9,4) comment '金额'")
    private Double chargeMoney;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime createTime;
}
