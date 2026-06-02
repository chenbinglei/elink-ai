package com.sunmax.together.entity.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.AUTO;

/**
 * 充放电计费详情记录历事数据表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_charge_tariff_record")
public class ChargeTariffRecordEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 订单号
     */
    @Column(name = "order_num", columnDefinition = "varchar(32) comment '订单号'")
    private String orderNum;

    /**
     * 时段电价
     */
    @Column(name = "elect_price", columnDefinition = "decimal(9,2) comment '计费时段'")
    private BigDecimal electPrice;

    /**
     * 时段服务费价
     */
    @Column(name = "service_price", columnDefinition = "decimal(9,2) comment '时段服务费价'")
    private BigDecimal servicePrice;

    /**
     * 充/放电量
     */
    @Column(name = "recharge_qt", columnDefinition = "double(9,2) comment '充/放电量'")
    private Double rechargeQt;

    /**
     * 电费
     */
    @Column(name = "elect_money",columnDefinition = "decimal(9,2) comment '电费'")
    private BigDecimal electMoney;

    /**
     * 服务费
     */
    @Column(name = "service_money",columnDefinition = "decimal(9,2) comment '服务费'")
    private BigDecimal serviceMoney;

    /**
     * 充电时长（时分秒）
     */
    @Column(name = "charge_duration", columnDefinition = "varchar(30) comment '充电时长（分钟）'")
    private String chargeDuration;

    /**
     * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天
     */
    @Column(name = "period_type",columnDefinition = "tinyint(1) comment ' 时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天'")
    private Integer periodType;

}
