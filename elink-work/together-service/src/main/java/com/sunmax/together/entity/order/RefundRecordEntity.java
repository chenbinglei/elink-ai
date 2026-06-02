package com.sunmax.together.entity.order;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 补单记录实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_refund_record")
public class RefundRecordEntity extends BaseTimeEntity {

    /**
     * 订单号
     */
    @Column(name = "order_num", columnDefinition = "varchar(32) comment '订单号'")
    private String orderNum;

    /**
     * 退款操作人
     */
    @Column(name = "refund_operator", columnDefinition = "varchar(32) comment '退款操作人'")
    private String refundOperator;

    /**
     * 退款金额
     */
    @Column(name = "refund_amount", columnDefinition = "decimal(9,2) comment '退款金额'")
    private BigDecimal refundAmount;

    /**
     * 退款状态 1-正常退款 2-退款异常(结算记录表中实付金额-退款金额 < 0 或 没查询到结算记录 或 结算记录中实付金额没值)
     */
    @Column(name = "refund_status", columnDefinition = "tinyint(1) comment '退款状态 1-正常退款 2-退款异常(结算记录表中实付金额-退款金额 < 0)'")
    private Integer refundStatus;
}
