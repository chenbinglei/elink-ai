package com.sunmax.device.entity.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 全国城市实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_city")
public class CityEntity {

    @Id
    @Column(name = "id", columnDefinition = "bigint(20) comment '主键id'")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;


    /**
     * 全国城市id
     */
    @Column(name = "city_id", columnDefinition = "varchar(20) comment '全国城市id'")
    private String cityId;

    /**
     * 全国城市名称
     */
    @Column(name = "city_name", columnDefinition = "varchar(50) comment '全国城市名称'")
    private String cityName;

    /**
     * 全国省id
     */
    @Column(name = "province_id", columnDefinition = "varchar(20) comment '全国省id'")
    private String provinceId;

    /**
     * 经度
     */
    @Column(name = "latitude",columnDefinition = "varchar(50) comment '经度'")
    private String latitude;

    /**
     * 纬度
     */
    @Column(name = "longitude",columnDefinition = "varchar(50) comment '纬度'")
    private String longitude;

}
