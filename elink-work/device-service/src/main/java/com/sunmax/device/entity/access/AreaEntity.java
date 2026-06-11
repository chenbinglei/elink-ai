package com.sunmax.device.entity.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 全国区县实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_area")
public class AreaEntity {

    @Id
    @Column(name = "id", columnDefinition = "bigint(20) comment '主键id'")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * 全国区县id
     */
    @Column(name = "area_id", columnDefinition = "varchar(20) comment '全国区县id'")
    private String areaId;

    /**
     * 全国区县名称
     */
    @Column(name = "area_name", columnDefinition = "varchar(50) comment '全国区县名称'")
    private String areaName;

    /**
     * 全国城市id
     */
    @Column(name = "city_id", columnDefinition = "varchar(20) comment '全国城市id'")
    private String cityId;

}
