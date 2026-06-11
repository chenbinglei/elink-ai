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
 * 网关和平台关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_gatway_platform")
public class GatWayPlatformEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 网关id
     */
    @Column(name = "gateway_id", columnDefinition = "varchar(32) not null comment '网关id'")
    private String gatewayId;

    /**
     * 平台id
     */
    @Column(name = "platform_id", columnDefinition = "varchar(32) not null comment '平台id'")
    private String platformId;
}
