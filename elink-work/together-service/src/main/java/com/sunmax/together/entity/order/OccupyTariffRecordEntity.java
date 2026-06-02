package com.sunmax.together.entity.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 占桩计费详情记录历史数据表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_occupy_tariff_record")
public class OccupyTariffRecordEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 所属占桩订单记录id
     */
    @Column(name = "occupy_id", columnDefinition = "varchar(32) comment '所属占桩订单记录id'")
    private String occupyId;

    /**
     * 计费时段
     */
    @Column(name = "tariff_period", columnDefinition = "varchar(32) comment '计费时段'")
    private String tariffPeriod;

    /**
     * 免占桩时长(分钟)
     */
    @Column(name = "avoid_duration",columnDefinition = "int(10) comment '免占桩时长(分钟)'")
    private Integer avoidDuration;

    /**
     * 收费标准
     */
    @Column(name = "tariff_standard", columnDefinition = "varchar(128) comment '收费标准'")
    private String tariffStandard;
}
