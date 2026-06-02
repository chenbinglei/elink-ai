package com.sunmax.together.entity.order;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 占桩记录表结构
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_occupy_pile_record")
public class OccupyPileRecordEntity extends BaseEntity {

    /**
     * 占桩订单号
     */
    @Column(name = "occupy_num",columnDefinition = "varchar(64) comment '占桩订单号'")
    private String occupyNum;

    /**
     * 所属订单记录id
     */
    @Column(name = "order_id",columnDefinition = "varchar(32) comment '所属订单记录id(关联b_order_record表里面的主键id)'")
    private String orderId;

    /**
     * 充电桩编号
     */
    @Column(name = "pile_code",columnDefinition = "varchar(64) comment '充电桩编号'")
    private String pileCode;

    /**
     * 开始时间
     */
    @Column(name = "start_time",columnDefinition = "varchar(50) comment '开始时间'")
    private String startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time",columnDefinition = "varchar(50) comment '结束时间'")
    private String endTime;

    /**
     * 时长 秒值
     */
    @Column(name = "duration",columnDefinition = "bigint(20) comment '时长 秒值'")
    private Long duration;

    /**
     * 占位订单金额
     */
    @Column(name = "order_money",columnDefinition = "decimal(9,2) comment '订单金额'")
    private BigDecimal orderMoney;

    /**
     * 占位实付金额
     */
    @Column(name = "paid_money",columnDefinition = "decimal(9,2) comment '实付金额'")
    private BigDecimal paidMoney;

    /**
     * 订单状态 1-在途 2-待支付 3-已完成 9-异常
     */
    @Column(name = "occupy_state",columnDefinition = "tinyint(1) comment '订单状态 1-在途 2-待支付 3-已完成 9-异常'")
    private Integer occupyState;

    /**
     * 订单支付状态 1-已支付 2-未支付
     */
    @Column(name = "pay_state",columnDefinition = "tinyint(1) comment '订单支付状态 1-已支付 2-未支付'")
    private Integer payState;

    /**
     * 支付时间
     */
    @Column(name = "pay_time",columnDefinition = "datetime(0) comment '支付时间'")
    private LocalDateTime payTime;

    /**
     * 占桩计费id
     */
    @Column(name = "occupy_rate_id", columnDefinition = "varchar(32) comment '占桩计费id(关联b_occupy_pile_price表唯一id)'")
    private String occupyRateId;
}
