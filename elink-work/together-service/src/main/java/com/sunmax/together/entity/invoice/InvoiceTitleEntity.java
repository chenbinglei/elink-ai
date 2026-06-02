package com.sunmax.together.entity.invoice;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * 发票抬头实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_invoice_title")
public class InvoiceTitleEntity extends BaseTimeEntity {

    /**
     * 发票类型 1-普通发票 2-专用发票
     */
    @Column(name = "invoice_type", columnDefinition = "tinyint(1) NOT NULL comment '发票类型 1-普通发票 2-专用发票'")
    private Integer invoiceType;

    /**
     * 小程序用户id
     */
    @Column(name = "applet_user_id", columnDefinition = "varchar(32) NOT NULL comment '小程序用户id'")
    private String appletUserId;

    /**
     * 抬头类型 1-个人 2-单位
     */
    @Column(name = "title_type", columnDefinition = "tinyint(1) NOT NULL comment '抬头类型 1-个人 2-单位'")
    private Integer titleType;

    /**
     * 发票抬头
     */
    @Column(name = "invoice_title", columnDefinition = "varchar(32) NOT NULL comment '发票抬头'")
    private String invoiceTitle;

    /**
     * 纳税人识别号,单位税号
     */
    @Column(name = "tax_number", columnDefinition = "varchar(32) NOT NULL comment '纳税人识别号'")
    private String taxNumber;

    /**
     * 注册地址
     */
    @Column(name = "registered_address", columnDefinition = "varchar(32) comment '注册地址'")
    private String registeredAddress;

    /**
     * 注册电话
     */
    @Column(name = "registered_phone", columnDefinition = "varchar(32) comment '注册电话'")
    private String registeredPhone;

    /**
     * 开户银行
     */
    @Column(name = "bank_name", columnDefinition = "varchar(32) comment '开户银行'")
    private String bankName;

    /**
     * 银行账号
     */
    @Column(name = "bank_account", columnDefinition = "varchar(32) comment '银行账号'")
    private String bankAccount;

    /**
     * 是否设为默认 1-是 2-否
     */
    @Column(name = "is_default", columnDefinition = "tinyint(1) NOT NULL comment '是否设为默认 1-是 2-否'")
    private Integer isDefault;

}
