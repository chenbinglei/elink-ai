package com.sunmax.protocol.entity;

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
 * 调控记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_dispatch_record")
public class DispatchRecordEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 场站id
     */
    @Column(name = "station_id", columnDefinition = "bigint(20) comment '场站id'")
    private Long stationId;

    /**
     * 网关编号
     */
    @Column(name = "gateway_code", columnDefinition = "varchar(64) comment '网关编号'")
    private String gatewayCode;

    /**
     * 类型 1-查询整站信息 2-调控需求下发及响应 3-调控需求终止下发及响应
     */
    @Column(name = "type", columnDefinition = "tinyint(1) comment '类型 1-查询整站信息 2-调控需求下发及响应 3-调控需求终止下发及响应'")
    private Integer type;

    /**
     * 请求数据
     */
    @Column(name = "request_data", columnDefinition = "longtext comment '请求数据'")
    private String requestData;

    /**
     * 响应数据
     */
    @Column(name = "response_data", columnDefinition = "longtext comment '响应数据'")
    private String responseData;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime createTime;

}
