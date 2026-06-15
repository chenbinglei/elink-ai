package com.sunmax.together.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * 小程序用户信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_applet_user")
public class AppletUserEntity extends BaseEntity {

    /**
     * 平台类型 1-微信小程序 2-支付宝
     */
    @Column(name = "platform_type", columnDefinition = "tinyint(1) not null comment '平台类型 1-微信小程序 2-支付宝'")
    private Integer platformType;

    /**
     * 手机号码
     */
    @Column(name = "phone_num",columnDefinition = "varchar(11) not null comment '手机号码'")
    private String phoneNum;

    /**
     * 用户分组id(关联b_user_group表唯一id)
     */
    @Column(name = "group_id",columnDefinition = "varchar(32) comment '用户分组id(关联b_user_group表唯一id)'")
    private String groupId;

    /**
     * 昵称
     */
    @Column(name = "nick_name",columnDefinition = "varchar(32) comment '昵称'")
    private String nickName;

    /**
     * 邮箱
     */
    @Column(name = "mailbox",columnDefinition = "varchar(32) comment '邮箱'")
    private String mailbox;

    /**
     * 描述
     */
    @Column(name = "refer",columnDefinition = "varchar(255) comment '描述'")
    private String refer;

    /**
     * 用户状态 1-正常 2-冻结 3-注销
     */
    @Column(name = "user_state", columnDefinition = "tinyint(1) not null comment '用户状态 1-正常 2-冻结 3-注销'")
    private Integer userState;

    /**
     * 用户在普通商户AppID下的唯一标识
     */
    @Column(name = "open_id",columnDefinition = "varchar(32) comment '用户在普通商户下的唯一标识'")
    private String openid;

    /**
     * 小程序主键id
     */
    @Column(name = "applet_id",columnDefinition = "varchar(32) comment '小程序主键id'")
    private String appletId;

}
