package com.sunmax.together.entity.order;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 结算记录实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_settlement_record")
public class SettlementRecordEntity extends BaseEntity {

    /**
     * 所属订单号
     */
    @Column(name = "order_num", columnDefinition = "varchar(32) comment '订单号'")
    private String orderNum;

    /**
     * 结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功
     */
    @Column(name = "settlement_state",columnDefinition = "tinyint(1) comment '结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功'")
    private Integer settlementState;

    /**
     * 支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额
     */
    @Column(name = "pay_way",columnDefinition = "tinyint(1) comment '支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额'")
    private Integer payWay;

    /**
     * 原价总金额
     */
    @Column(name = "original_cost", columnDefinition = "decimal(9,2) comment '原价总金额'")
    @Builder.Default
    private BigDecimal originalCost = new BigDecimal("0.0");

    /**
     * 实付金额
     */
    @Column(name = "actual_total_cost", columnDefinition = "decimal(9,2) comment '实付金额'")
    @Builder.Default
    private BigDecimal actualTotalCost = new BigDecimal("0.0");

    /**
     * 实付电费
     */
    @Column(name = "actual_total_elect", columnDefinition = "decimal(9,2) comment '实付电费'")
    private BigDecimal actualTotalElect;

    /**
     * 实付服务费
     */
    @Column(name = "actual_total_fee", columnDefinition = "decimal(9,2) comment '实付服务费'")
    private BigDecimal actualTotalFee;

    /**
     * 电费减免
     */
    @Column(name = "total_elect_reduction", columnDefinition = "decimal(9,2) comment '电费减免'")
    private BigDecimal totalElectReduction;

    /**
     * 服务费减免
     */
    @Column(name = "total_fee_reduction", columnDefinition = "decimal(9,2) comment '服务费减免'")
    private BigDecimal totalFeeReduction;

    /**
     * 退款金额
     */
    @Column(name = "refund_money",columnDefinition = "decimal(9,2) comment '退款金额'")
    private BigDecimal refundMoney;
}
