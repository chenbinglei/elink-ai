package com.sunmax.together.entity;

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

/**
 * 用户分组信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_user_group")
public class UserGroupEntity extends BaseEntity {

    /**
     * 分组名称
     */
    @Column(name = "group_name",columnDefinition = "varchar(32) not null comment '分组名称'")
    private String groupName;

    /**
     * 电费折扣
     */
    @Column(name = "elec_discount",columnDefinition = "int(10) not null comment '电费折扣'")
    private Integer elecDiscount;

    /**
     * 服务费折扣
     */
    @Column(name = "service_discount",columnDefinition = "int(10) not null comment '服务费折扣'")
    private Integer serviceDiscount;

    /**
     * 描述
     */
    @Column(name = "refer",columnDefinition = "varchar(255) comment '描述'")
    private String refer;
}
