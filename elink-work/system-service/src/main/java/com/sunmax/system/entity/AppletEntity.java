package com.sunmax.system.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_applet")
public class AppletEntity extends BaseEntity {

    /**
     * 小程序名称
     */
    @Column(name = "applet_name",columnDefinition = "varchar(64) comment '小程序名称'")
    private String appletName;

    /**
     * 小程序id
     */
    @Column(name = "applet_code",columnDefinition = "varchar(64) comment '小程序id'")
    private String appletCode;

    /**
     * 小程序密钥
     */
    @Column(name = "applet_secret",columnDefinition = "varchar(64) comment '小程序密钥'")
    private String appletSecret;

    /**
     * 小程序类型 1-微信 2-支付宝
     */
    @Column(name = "applet_type",columnDefinition = "tinyint(1) comment '小程序类型 1-微信 2-支付宝'")
    private Integer appletType;

    /**
     * 联系电话
     */
    @Column(name = "phone",columnDefinition = "varchar(32) comment '联系电话'")
    private String phone;

    /**
     * 邮箱
     */
    @Column(name = "email",columnDefinition = "text comment '邮箱'")
    private String email;

    /**
     * 小程序logo
     */
    @Column(name = "applet_logo",columnDefinition = "varchar(1024) comment '小程序logo'")
    private String appletLogo;

    /**
     * 公众号名称
     */
    @Column(name = "tencent_name",columnDefinition = "varchar(64) comment '公众号名称'")
    private String tencentName;

    /**
     * 公众号id
     */
    @Column(name = "tencent_code",columnDefinition = "varchar(64) comment '公众号id'")
    private String tencentCode;

    /**
     * 公众号密钥
     */
    @Column(name = "tencent_secret",columnDefinition = "varchar(64) comment '公众号密钥'")
    private String tencentSecret;

    /**
     * 公众号二维码图片
     */
    @Column(name = "tencent_image",columnDefinition = "varchar(1024) comment '公众号二维码图片'")
    private String tencentImage;

    /**
     * 绑定多个租户id
     */
    @Column(name = "tenant_ids",columnDefinition = "longtext comment '绑定多个租户id'")
    private String tenantIds;

}
