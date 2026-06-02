package com.sunmax.together.entity;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_large_setting")
public class LargeSettingEntity extends BaseTimeEntity {

    /**
     * 用户id
     */
    @Column(name = "user_id", columnDefinition = "varchar(32) comment '用户id'")
    private String userId;

    /**
     * 设置数据
     */
    @Column(name = "setting_data", columnDefinition = "longtext comment '设置数据'")
    private String settingData;

}
