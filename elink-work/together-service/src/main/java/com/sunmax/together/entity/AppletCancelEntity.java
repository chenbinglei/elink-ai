package com.sunmax.together.entity;

import com.sunmax.common.entity.BaseTimeEntity;
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

/**
 * 小程序注销申请信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_applet_cancel")
public class AppletCancelEntity extends BaseTimeEntity {

    /**
     * 小程序名称
     */
    @Column(name = "applet_name",columnDefinition = "varchar(32) not null comment '小程序名称'")
    private String appletName;

    /**
     * 小程序用户id(关联b_applet_user表唯一id)
     */
    @Column(name = "applet_user_id", columnDefinition = "varchar(32) not null comment '小程序用户id(关联b_applet_user表唯一id)'")
    private String appletUserId;

    /**
     * 申请状态 1-申请注销 2-已注销
     */
    @Column(name = "apply_state", columnDefinition = "tinyint(1) not null comment '申请状态 1-申请注销 2-已注销'")
    private Integer applyState;
}
