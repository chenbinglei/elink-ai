package com.sunmax.together.entity.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_invoice_record")
public class InvoiceRecordEntity {

    @Id
    @Column(name = "id", columnDefinition = "varchar(64) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;

    /**
     * 申请单号id(关联b_invoice的主键id)
     */
    @Column(name = "invoice_id", columnDefinition = "varchar(32) comment '申请单号id(关联b_invoice的主键id)'")
    private String invoiceId;

    /**
     * 操作类型 1-受理 2-开票
     */
    @Column(name = "operation_type",columnDefinition = "tinyint(1) NOT NULL comment '操作类型 1-受理 2-开票'")
    private Integer operationType;

    /**
     * 平台用户id
     */
    @Column(name = "user_id",columnDefinition = "varchar(32) NOT NULL comment '平台用户id'")
    private String userId;
}
