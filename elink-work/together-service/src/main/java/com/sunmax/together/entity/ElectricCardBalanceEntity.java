package com.sunmax.together.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_electric_card_balance")
public class ElectricCardBalanceEntity {

    /**
     * 交易单号id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(64) comment '交易单号id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 电卡id
     */
    @Column(name = "car_id", columnDefinition = "varchar(64) comment '电卡id'")
    private String carId;

    /**
     * 操作记录 1-退款 2-解冻 3-消费 4-冻结 5-充值
     */
    @Column(name = "trade_type", columnDefinition = "tinyint(1) not null comment '操作记录 1-退款 2-解冻 3-消费 4-冻结 5-充值'")
    private Integer tradeType;

    /**
     * 交易金额
     */
    @Column(name = "trade_balance", columnDefinition = "double(9,2) comment '交易金额'")
    private Double tradeBalance;

    /**
     * 变动后余额
     */
    @Column(name = "after_trade_balance", columnDefinition = "double(9,2) comment '变动后余额'")
    private Double afterTradeBalance;

    /**
     * 交易时间
     */
    @Column(name = "trade_time", columnDefinition = "varchar(32) comment '交易时间'")
    private String tradeTime;


}
