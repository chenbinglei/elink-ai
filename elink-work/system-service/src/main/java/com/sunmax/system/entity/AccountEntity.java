package com.sunmax.system.entity;

import com.sunmax.common.entity.BaseTimeEntity;
import com.sunmax.system.vo.AccountVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * 企业账户信息表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_account")
public class AccountEntity extends BaseTimeEntity {

    /**
     * 租户id
     */
    @Column(name = "tenant_id",columnDefinition = "varchar(32) comment '关联租户id'")
    private String tenantId;

    /**
     * 平台类型 1-微信平台 2-支付宝平台
     */
    @Column(name = "platform_type",columnDefinition = "tinyint(1) comment '平台类型 1-微信平台 2-支付宝平台'")
    private Integer platformType;

    /**
     * 基于微信小程序
     * 商户类型 1-商户号 2-个人openid
     */
    @Column(name = "mch_type",columnDefinition = "tinyint(1) comment '商户类型 1-商户号 2-个人openid'")
    private Integer mchType;

    /**
     * 商户号
     */
    @Column(name = "mch_id",columnDefinition = "varchar(50) comment '商户号'")
    private String mchId;

    /**
     * 商户名称
     */
    @Column(name = "mch_name",columnDefinition = "varchar(50) comment '商户名称'")
    private String mchName;

    /**
     * 商户密钥
     */
    @Column(name = "mch_key",columnDefinition = "varchar(64) comment '商户密钥'")
    private String mchKey;

    /**
     * API类型 1-平台证书 2-微信支付公钥
     */
    @Column(name = "api_type",columnDefinition = "tinyint(1) comment 'API类型 1-平台证书 2-微信支付公钥'")
    private Integer apiType;

    /**
     * APIv3密钥
     */
    @Column(name = "api_v3_key",columnDefinition = "varchar(64) comment 'APIv3密钥'")
    private String apiV3Key;

    /**
     * 商户证书序列号
     */
    @Column(name = "serial_no",columnDefinition = "varchar(64) comment '商户证书序列号'")
    private String serialNo;

    /**
     * 商户key路径
     */
    @Column(name = "key_pem_path",columnDefinition = "varchar(255) comment '商户key路径'")
    private String keyPemPath;

    /**
     * 平台RSA证书序列号(商户公钥id)
     */
    @Column(name = "rsa_serial_no",columnDefinition = "varchar(255) comment '平台RSA证书序列号(商户公钥id)'")
    private String rsaSerialNo;

    /**
     * 商户公钥路径(pub_key.pem)
     */
    @Column(name = "pub_key_path",columnDefinition = "varchar(255) comment '商户公钥路径(pub_key.pem)'")
    private String pubKeyPath;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;

    public AccountEntity(AccountVo accountVo) {
        BeanUtils.copyProperties(accountVo, this);
    }

}
