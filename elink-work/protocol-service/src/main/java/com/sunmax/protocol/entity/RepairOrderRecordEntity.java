package com.sunmax.protocol.entity;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * 补单记录实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_repair_order_record")
public class RepairOrderRecordEntity extends BaseTimeEntity {

    /**
     * 订单号
     */
    @Column(name = "order_num", columnDefinition = "varchar(32) comment '订单号'")
    private String orderNum;

    /**
     * 补单状态 0-挂单 1-自动恢复 2-人工恢复
     */
    @Column(name = "repair_status", columnDefinition = "tinyint(1) comment '补单状态 0-挂单 1-自动恢复 2-人工恢复'")
    private Integer repairStatus;

    /**
     * 异常时长
     */
    @Column(name = "exception_time", columnDefinition = "varchar(32) comment '异常时长'")
    private String exceptionTime;

    /**
     * 补单操作人
     */
    @Column(name = "repair_operator", columnDefinition = "varchar(128) comment '补单操作人'")
    private String repairOperator;

}
