package com.sunmax.device.entity.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 全国省实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_province")
public class ProvinceEntity {

    @Id
    @Column(name = "id", columnDefinition = "bigint(20) comment '主键id'")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * 全国省id
     */
    @Column(name = "province_id", columnDefinition = "varchar(20) comment '全国省id'")
    private String provinceId;

    /**
     * 全国省名称
     */
    @Column(name = "province_name", columnDefinition = "varchar(50) comment '全国省名称'")
    private String provinceName;

}
