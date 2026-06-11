package com.sunmax.together.entity.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_invoice_order")
public class InvoiceOrderEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 申请单号id
     */
    @Column(name = "invoice_id", columnDefinition = "varchar(32) comment '申请单号id(关联b_order_invoice的主键id)'")
    private String invoiceId;

    /**
     * 订单号
     */
    @Column(name = "order_num", columnDefinition = "varchar(32) comment '订单号(关联b_order_record表的订单号)'")
    private String orderNum;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;
}

