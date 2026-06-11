package com.sunmax.together.entity.assets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 价格应用范围
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_pirce_applied_range")
public class PirceAppliedRangeEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 价格id
     */
    @Column(name = "price_id",columnDefinition = "varchar(32) comment '价格id(关联b_charger_price_info表唯一id)'")
    private String priceId;

    /**
     * 充电桩编号
     */
    @Column(name = "pile_code",columnDefinition = "varchar(64) comment '充电桩编号'")
    private String pileCode;

    /**
     * 执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
     */
    @Column(name = "take_result",columnDefinition = "tinyint(1) comment '执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错'")
    private Integer takeResult;
}
