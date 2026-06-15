package com.sunmax.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 用户表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_user")
public class UserEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 姓名
     */
    @Column(name = "full_name",columnDefinition = "varchar(32) comment '姓名'")
    private String fullName;

    /**
     * 所属租户id
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(32) comment '所属租户id'")
    private String tenantId;

    /**
     * 所属组织id
     */
    @Column(name = "organ_id",columnDefinition = "varchar(32) comment '所属组织id'")
    private String organId;

    /**
     * 所属用户组id
     */
    @Column(name = "group_id",columnDefinition = "varchar(256) comment '所属用户组id'")
    private String groupId;

    /**
     * 角色 0-平台管理员 1-管理员 2-普通用户
     */
    @Column(name = "user_role", columnDefinition = "tinyint(1) NOT NULL comment '角色 0-平台管理员 1-管理员 2-普通用户'")
    private Integer userRole;

    /**
     * 用户账号
     */
    @Column(name = "user_account",columnDefinition = "varchar(32) comment '用户账号'")
    private String userAccount;

    /**
     * 密码
     */
    @Column(name = "password",columnDefinition = "varchar(64) comment '密码'")
    private String password;

    /**
     * 电话
     */
    @Column(name = "phone",columnDefinition = "varchar(32) comment '电话'")
    private String phone;

    /**
     * 用户头像
     */
    @Column(name = "user_profile",columnDefinition = "varchar(512) comment '用户头像'")
    private String userProfile;

    /**
     * 用户状态 0-关闭 1-开启
     */
    @Column(name = "user_state",columnDefinition = "tinyint(1) comment '用户状态 0-关闭 1-开启'")
    private Integer userState;

    /**
     * 账号到期日
     */
    @Column(name = "expire_date",columnDefinition = "varchar(12) comment '账号到期日'")
    private String expireDate;

    /**
     * 是否默认管理员账号 1-是
     */
    @Column(name = "isD_default_admin",columnDefinition = "tinyint(1) comment '是否默认管理员账号 1-是'")
    private Integer isDefaultAdmin;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime updateTime;
}
