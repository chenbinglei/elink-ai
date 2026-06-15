package com.sunmax.together.entity;

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
@Table(name = "b_electric_card_record")
public class ElectricCardRecordEntity {
    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(64) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 电卡id
     */
    @Column(name = "car_id", columnDefinition = "varchar(64) comment '电卡id'")
    private String carId;

    /**
     * 操作记录 1-新增电卡 2-编辑电卡 3-禁用电卡 4-启用电卡
     */
    @Column(name = "operation_type", columnDefinition = "tinyint(1) not null comment '操作记录 1-新增电卡 2-编辑电卡 3-禁用电卡 4-启用电卡'")
    private Integer operationType;

    /**
     * 创建人id
     */
    @Column(name = "create_id", columnDefinition = "varchar(32) comment '创建人id'", updatable = false)
    private String createId;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;


}
