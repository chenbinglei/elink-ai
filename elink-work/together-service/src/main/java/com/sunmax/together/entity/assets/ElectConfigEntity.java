package com.sunmax.together.entity.assets;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * 电价配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_elect_config")
public class ElectConfigEntity extends BaseTimeEntity {

    /**
     * 所属站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) comment '所属站点id'")
    private String siteId;

    /**
     * 电价模块类型 1-电网电价 2-光伏上网电价 3-光伏消纳电价 4-储能售电电价 5-储能购电电价 6-电桩售电电价 7-电桩购电电价
     * 电网电价：关联关口表正向有功电量（尖峰平谷深）
     * 光伏上网电价：关联关口表反向有功电量（尖峰平谷深）
     * 光伏消纳电价：关联（光伏并网表总发电量-上网电量）
     * 储能售电电价：关联储能计量节点并网表反向有功电量（尖峰平谷深）
     * 储能购电电价：关联储能计量节点并网表正向有功电量（尖峰平谷深）
     * 电桩售电电价：关联电桩计量节点并网表反向有功电量（尖峰平谷深）
     * 电桩购电电价：关联电桩计量节点并网表正向有功电量（尖峰平谷深）
     */
    @Column(name = "module_type", columnDefinition = "tinyint(1) comment '电价模块类型 1-电网电价 2-光伏上网电价 3-光伏消纳电价 4-储能售电电价 5-储能购电电价 6-电桩售电电价 7-电桩购电电价'")
    private Integer moduleType;

    /**
     * 策略名称
     */
    @Column(name = "strategy_name", columnDefinition = "varchar(64) comment '策略名称'")
    private String strategyName;

    /**
     * 开始日期
     */
    @Column(name = "start_date", columnDefinition = "varchar(20) comment '开始日期'")
    private String startDate;

    /**
     * 结束日期
     */
    @Column(name = "end_date", columnDefinition = "varchar(20) comment '结束日期'")
    private String endDate;

    /**
     * 定价方式 1-全天同价 2-分时段定价
     */
    @Column(name = "price_type", columnDefinition = "tinyint(1) comment '定价方式 1-全天同价 2-分时段定价'")
    private Integer priceType;

}
