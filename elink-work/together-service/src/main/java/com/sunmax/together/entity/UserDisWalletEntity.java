package com.sunmax.together.entity;

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
 * 用户V2G钱包实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_user_dis_wallet")
public class UserDisWalletEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 小程序用户id
     */
    @Column(name = "applet_user_id", columnDefinition = "varchar(32) comment '小程序用户id'")
    private String appletUserId;

    /**
     * 账户id
     */
    @Column(name = "account_id", columnDefinition = "varchar(32) comment '账户id'")
    private String accountId;

    /**
     * 账户余额
     */
    @Column(name = "balance", columnDefinition = "decimal(10,2) comment '账户余额'")
    @Builder.Default
    private BigDecimal balance = new BigDecimal("0.0");

    /**
     * 冻结余额
     */
    @Column(name = "freeze_balance", columnDefinition = "decimal(10,2) comment '账户冻结余额'")
    @Builder.Default
    private BigDecimal freezeBalance = new BigDecimal("0.0");

}
