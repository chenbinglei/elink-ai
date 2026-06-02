package com.sunmax.system.entity;

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
@Table(name = "b_custom")
public class CustomEntity extends BaseTimeEntity {

    /**
     * 平台名称
     */
    @Column(name = "platform_name", columnDefinition = "varchar(32) comment '平台名称'")
    private String platformName;

    /**
     * 平台LOGO
     */
    @Column(name = "platform_logo", columnDefinition = "varchar(1024) comment '平台LOGO'")
    private String platformLogo;

    /**
     * 大屏标题
     */
    @Column(name = "large_title", columnDefinition = "varchar(64) comment '大屏标题'")
    private String largeTitle;

    /**
     * 修改人id
     */
    @Column(name = "update_id", columnDefinition = "varchar(64) comment '修改人id'")
    private String updateId;

}
