package com.sunmax.together.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import lombok.EqualsAndHashCode;

/**
 * 电卡信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_electric_card")
public class ElectricCardEntity extends BaseEntity {

    /**
     * 电卡类型 1-预付费 2-后付费 3-无需付费
     */
    @Column(name = "card_type", columnDefinition = "tinyint(1) not null comment '电卡类型 1-预付费 2-后付费 3-无需付费'")
    private Integer cardType;

    /**
     * 卡面号
     */
    @Column(name = "card_number", columnDefinition = "varchar(32) comment '卡面号'")
    private String cardNumber;

    /**
     * 物理卡号
     */
    @Column(name = "physical_card", columnDefinition = "varchar(32) comment '物理卡号'")
    private String physicalCard;

    /**
     * 车牌号
     */
    @Column(name = "license_number", columnDefinition = "varchar(32) comment '车牌号'")
    private String licenseNumber;

    /**
     * 持卡人
     */
    @Column(name = "card_holder", columnDefinition = "varchar(32) comment '持卡人'")
    private String cardHolder;

    /**
     * 状态 1-正常 2-禁用
     */
    @Column(name = "state", columnDefinition = "tinyint(1) comment '状态 1-正常 2-禁用'")
    private Integer state;

    /**
     * 可用余额
     */
    @Column(name = "current_balance", columnDefinition = "double(9,2) comment '可用余额'")
    private double currentBalance;


}