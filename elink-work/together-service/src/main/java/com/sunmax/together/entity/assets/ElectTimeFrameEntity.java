package com.sunmax.together.entity.assets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 站点充放电价格信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_elect_time_frame")
public class ElectTimeFrameEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 电价配置id
     */
    @Column(name = "elect_config_id", columnDefinition = "varchar(32) comment '电价配置id(关联b_elect_config的主键id)'")
    private String electConfigId;

    /**
     * 时段开始时间
     */
    @Column(name = "start_time",columnDefinition = "varchar(10) comment '时段开始时间'")
    private String startTime;

    /**
     * 时段结束时间
     */
    @Column(name = "end_time",columnDefinition = "varchar(10) comment '时段结束时间'")
    private String endTime;

    /**
     * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天
     */
    @Column(name = "period_type",columnDefinition = "tinyint(1) comment ' 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天'")
    private Integer periodType;

    /**
     * 电费
     */
    @Column(name = "elect_money",columnDefinition = "decimal(9,3) comment '电费'")
    private BigDecimal electMoney;

}
