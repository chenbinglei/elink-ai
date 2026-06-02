package com.sunmax.webapp.entity.trade;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 充电订单交易明细实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_recharge_trade")
public class RechargeTradeEntity extends BaseTimeEntity {

    /**
     * 交易订单号
     */
    @Column(name = "order_num",columnDefinition = "varchar(64) comment '交易订单号'")
    private String orderNum;

    /**
     * 退款订单号
     */
    @Column(name = "refund_num",columnDefinition = "varchar(64) comment '退款订单号'")
    private String refundNum;

    /**
     * 交易流水号
     */
    @Column(name = "flow_num",columnDefinition = "varchar(64) comment '交易流水号'")
    private String flowNum;

    /**
     * 交易金额
     */
    @Column(name = "trade_money",columnDefinition = "decimal(9,2) NOT NULL comment '交易金额'")
    @Builder.Default
    private BigDecimal tradeMoney = new BigDecimal("0.0");

    /**
     * 交易类型 1-充电预付 2-充电退款
     */
    @Column(name = "trade_type",columnDefinition = "tinyint(1) comment '交易类型 1-充电预付 2-充电退款'")
    private Integer tradeType;

    /**
     * 交易状态 1-处理中 2-处理成功 3-处理失败
     */
    @Column(name = "trade_status",columnDefinition = "tinyint(1) comment '交易状态 1-处理中 2-处理成功 3-处理失败'")
    private Integer tradeStatus;

    /**
     * 交易方式 1-微信 2-支付宝 3-银联商户
     */
    @Column(name = "trade_way",columnDefinition = "tinyint(1) comment '交易方式 1-微信 2-支付宝 3-银联商户'")
    private Integer tradeWay;

    /**
     * 明细类型 1-收入 2-支出
     */
    @Column(name = "detail_type",columnDefinition = "tinyint(1) comment '明细类型 1-收入 2-支出'")
    private Integer detailType;

    /**
     * 交易状态编码
     */
    @Column(name = "trade_state",columnDefinition = "varchar(64) comment '交易状态编码'")
    private String tradeState;

    /**
     * 交易状态描述
     */
    @Column(name = "trade_state_desc",columnDefinition = "varchar(255) comment '交易状态描述'")
    private String tradeStateDesc;

    /**
     * 账户id
     */
    @Column(name = "account_id",columnDefinition = "varchar(32) comment '账户id'")
    private String accountId;

    /**
     * 小程序用户id
     */
    @Column(name = "applet_user_id",columnDefinition = "varchar(32) comment '小程序用户id'")
    private String appletUserId;

    /**
     * 退款类型 1-启动失败退款 2-充电完成退款 3-平台人工退款
     */
    @Column(name = "type",columnDefinition = "tinyint(1) comment '退款类型 1-启动失败退款 2-充电完成退款 3-平台人工退款'")
    private Integer type;

    /**
     * 站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

}
