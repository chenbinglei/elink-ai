package com.sunmax.together.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

/**
 * 电桩状态时长
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_pile_state_duration")
public class PileStateDurationEntity {

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
    @Column(name = "site_id",columnDefinition = "varchar(32) NOT NULL comment '所属站点id'")
    private String siteId;

    /**
     * 充电桩编号
     */
    @Column(name = "pile_code",columnDefinition = "varchar(64) comment '充电桩编号'")
    private String pileCode;

    /**
     * 电桩状态 -1-未知 1-在线 2-维护 3-故障 88-离线 99-未注册
     */
    @Column(name = "work_state", columnDefinition = "int(4) comment '电桩状态 -1-未知 1-在线 2-维护 3-故障 88-离线 99-未注册'")
    private Integer workState;

    /**
     * 时长(秒)
     */
    @Column(name = "duration", columnDefinition = "bigint(20) comment '时长(秒)'")
    public Long duration;

    /**
     * 状态开始时间
     */
    @Column(name = "start_time", columnDefinition = "datetime(0) comment '状态开始时间'")
    private LocalDateTime startTime;

    /**
     * 状态结束时间
     */
    @Column(name = "end_time", columnDefinition = "datetime(0) comment '状态结束时间'")
    private LocalDateTime endTime;

    /**
     * 统计日期
     */
    @Column(name = "count_date",columnDefinition = "varchar(64) comment '统计日期'")
    private String countDate;
}
