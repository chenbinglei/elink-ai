package com.sunmax.together.entity.ops;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * 巡检项实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_inspection_item")
public class InspectionItemEntity extends BaseTimeEntity {

    /**
     * 站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) not null comment '关联站点id'")
    private String siteId;

    /**
     * 巡检项名称
     */
    @Column(name = "name",columnDefinition = "varchar(64) not null comment '巡检项名称'")
    private String name;

    /**
     * 巡检内容描述
     */
    @Column(name = "description",columnDefinition = "longtext comment '巡检内容描述'")
    private String description;

    /**
     * 图标地址
     */
    @Column(name = "icon_path",columnDefinition = "varchar(255) comment '图标地址'")
    private String iconPath;

}
