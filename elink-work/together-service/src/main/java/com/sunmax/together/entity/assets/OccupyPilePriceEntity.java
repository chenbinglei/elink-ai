package com.sunmax.together.entity.assets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 占桩价格信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_occupy_pile_price")
public class OccupyPilePriceEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 所属站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) comment '所属站点id'")
    private String siteId;

    /**
     * 定价类型 1-全天
     */
    @Column(name = "fixed_type",columnDefinition = "tinyint(1) comment '定价类型 1-全天'")
    @Builder.Default
    private Integer fixedType = 1;

    /**
     * 部分时段价格信息
     */
    @Column(name = "part_period_info",columnDefinition = "text comment '部分时段价格信息'")
    @Builder.Default
    private String partPeriodInfo = "00:00-23:59";

    /**
     * 设备类型 1-直流 2-交流
     */
    @Column(name = "device_type",columnDefinition = "tinyint(1) comment '设备类型 1-直流 2-交流'")
    private Integer deviceType;

    /**
     * 免占桩时长(分钟)
     */
    @Column(name = "avoid_duration",columnDefinition = "int(10) comment '免占桩时长(分钟)'")
    private Integer avoidDuration;

    /**
     * 配置类型 1-固定价格 2-阶梯价格
     */
    @Column(name = "config_type",columnDefinition = "tinyint(1) comment '配置类型 1-固定价格 2-阶梯价格'")
    private Integer configType;

    /**
     * 配置价格信息
     */
    @Column(name = "config_price_info",columnDefinition = "text comment '配置价格信息'")
    private String configPriceInfo;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;
}
