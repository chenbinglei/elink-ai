package com.sunmax.together.entity.order;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * 用户订单记录信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_user_record")
public class UserRecordEntity extends BaseEntity {

    /**
     * 所属订单号
     */
    @Column(name = "order_Num", columnDefinition = "varchar(32) comment '所属订单号'")
    private String orderNum;

    /**
     * 企业账户
     */
    @Column(name = "enterprise_account", columnDefinition = "varchar(32) comment '企业账户'")
    private String enterpriseAccount;

    /**
     * 车队名称
     */
    @Column(name = "fleet_name", columnDefinition = "varchar(64) comment '车队名称'")
    private String fleetName;

    /**
     * 车牌号
     */
    @Column(name = "plate_number", columnDefinition = "varchar(64) comment '车牌号'")
    private String plateNumber;

    /**
     * 开票状态 1-已开发票 2-未开发票
     */
    @Column(name = "invoicing_state",columnDefinition = "tinyint(1) comment '开票状态 1-已开发票 2-未开发票'")
    private Integer invoicingState;
}
