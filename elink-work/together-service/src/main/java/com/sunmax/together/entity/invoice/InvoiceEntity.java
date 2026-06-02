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
import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_invoice")
public class InvoiceEntity extends BaseTimeEntity {

    /**
     * 小程序用户id
     */
    @Column(name = "applet_user_id", columnDefinition = "varchar(32) NOT NULL comment '小程序用户id'")
    private String appletUserId;

    /**
     * 发票类型 1-普通发票 2-专用发票
     */
    @Column(name = "invoice_type", columnDefinition = "tinyint(1) NOT NULL comment '发票类型 1-普通发票 2-专用发票'")
    private Integer invoiceType;

    /**
     * 抬头类型 1-个人 2-单位
     */
    @Column(name = "title_type", columnDefinition = "tinyint(1) NOT NULL comment '抬头类型 1-个人 2-单位'")
    private Integer titleType;

    /**
     * 发票抬头名称
     */
    @Column(name = "invoice_title", columnDefinition = "varchar(32) NOT NULL comment '发票抬头名称'")
    private String invoiceTitle;

    /**
     * 纳税人识别号
     */
    @Column(name = "tax_number", columnDefinition = "varchar(32) comment '纳税人识别号'")
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
     * 账户id
     */
    @Column(name = "account_id", columnDefinition = "varchar(32) comment '账户id'")
    private String accountId;

    /**
     * 发票金额
     */
    @Column(name = "invoice_amount", columnDefinition = "decimal(9,2) comment '发票金额'")
    @Builder.Default
    private BigDecimal invoiceAmount = new BigDecimal("0.0");

    /**
     * 发票状态 1-待开票 2-开票中 3-已开票 4-已撤销
     */
    @Column(name = "invoice_status",columnDefinition = "tinyint(1) NOT NULL comment '发票状态 1-待开票 2-开票中 3-已开票 4-已撤销'")
    private Integer invoiceStatus;

    /**
     * 收票人邮箱
     */
    @Column(name = "receipt_email", columnDefinition = "varchar(255) comment '收票人邮箱'")
    private String receiptEmail;

    /**
     * 发票文件路径(pdf格式)
     */
    @Column(name = "invoice_file_path", columnDefinition = "varchar(255) comment '发票文件路径(pdf格式)'")
    private String invoiceFilePath;

}