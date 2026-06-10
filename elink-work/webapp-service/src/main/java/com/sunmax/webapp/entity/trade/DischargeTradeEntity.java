package com.sunmax.webapp.entity.trade;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * V2G钱包交易明细实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_discharge_trade")
public class DischargeTradeEntity extends BaseTimeEntity {

    /**
     * 交易订单号
     */
    @Column(name = "order_num",columnDefinition = "varchar(64) comment '交易订单号'")
    private String orderNum;

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
     * 交易类型 1-V2G收益存入 2-余额提现
     */
    @Column(name = "trade_type",columnDefinition = "tinyint(1) comment '交易类型 1-V2G收益存入 2-余额提现'")
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
     * 交易后用户余额(小程序交易明细里面用到)
     */
    @Column(name = "trade_balance",columnDefinition = "decimal(9,2) NOT NULL comment '交易金额'")
    @Builder.Default
    private BigDecimal tradeBalance = new BigDecimal("0.0");

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
     * 站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

}
