package com.sunmax.together.entity;

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
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_income")
public class SiteIncomeEntity extends BaseTimeEntity {

    /**
     * 站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 收益模型id
     */
    @Column(name = "income_model_id", columnDefinition = "varchar(32) comment '收益模型id'")
    private String incomeModelId;

    /**
     * 设备总成本(元)
     */
    @Column(name = "device_cost", columnDefinition = "decimal(10,2) comment '设备总成本(元)'")
    private BigDecimal deviceCost;

    /**
     * 施工总费用(元)
     */
    @Column(name = "construction_cost", columnDefinition = "decimal(10,2) comment '施工总费用(元)'")
    private BigDecimal constructionCost;

    /**
     * 场地租金(元/月)
     */
    @Column(name = "site_rent", columnDefinition = "decimal(10,2) comment '场地租金(元/月)'")
    private BigDecimal siteRent;

    /**
     * 场地租金开始日期
     */
    @Column(name = "rent_start_date", columnDefinition = "varchar(20) comment '场地租金开始日期'")
    private String rentStartDate;

    /**
     * 场地租金结束日期
     */
    @Column(name = "rent_end_date", columnDefinition = "varchar(20) comment '场地租金结束日期'")
    private String rentEndDate;

    /**
     * 充电运营补贴(元/度)
     */
    @Column(name = "operation_subsidy", columnDefinition = "decimal(10,2) comment '充电运营补贴(元/度)'")
    private BigDecimal operationSubsidy;

    /**
     * 充电运营补贴开始日期
     */
    @Column(name = "subsidy_start_date", columnDefinition = "varchar(20) comment '充电运营补贴开始日期'")
    private String subsidyStartDate;

    /**
     * 充电运营补贴结束日期
     */
    @Column(name = "subsidy_end_date", columnDefinition = "varchar(20) comment '充电运营补贴结束日期'")
    private String subsidyEndDate;

    /**
     * 建设补贴(元)
     */
    @Column(name = "construction_subsidy", columnDefinition = "decimal(10,2) comment '建设补贴(元)'")
    private BigDecimal constructionSubsidy;

    /**
     * 运营成本(元/月)
     */
    @Column(name = "operation_cost", columnDefinition = "decimal(10,2) comment '运营成本(元/月)'")
    private BigDecimal operationCost;

    /**
     * 运维成本(元/月)
     */
    @Column(name = "maintain_cost", columnDefinition = "decimal(10,2) comment '运维成本(元/月)'")
    private BigDecimal maintainCost;

}
