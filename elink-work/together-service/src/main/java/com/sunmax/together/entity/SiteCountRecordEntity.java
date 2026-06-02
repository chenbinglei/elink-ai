package com.sunmax.together.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

/**
 * 用户分组和站点关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_count_record")
public class SiteCountRecordEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 总额定功率(全部充电桩额定功率之和)
     */
    @Column(name = "total_pile_power", columnDefinition = "double(10,2) comment '额定功率'")
    @Builder.Default
    private Double totalPilePower = 0.0;

    /**
     * 总电桩枪数
     */
    @Column(name = "total_gun_num", columnDefinition = "int(11) comment '总电桩枪数'")
    @Builder.Default
    private Integer totalGunNum = 0;

    /**
     * 交流电桩枪数
     */
    @Column(name = "ac_gun_num", columnDefinition = "int(11) comment '交流电桩枪数'")
    @Builder.Default
    private Integer acGunNum = 0;

    /**
     * 直流电桩枪数
     */
    @Column(name = "dc_gun_num", columnDefinition = "int(11) comment '直流电桩枪数'")
    @Builder.Default
    private Integer dcGunNum = 0;

    /**
     * 统计日期
     */
    @Column(name = "count_date", columnDefinition = "date comment '统计日期'")
    private LocalDate countDate;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;

}
