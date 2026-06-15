package com.sunmax.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 充电平台信息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_charge_platform_info")
public class ChargePlatformInfoEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 平台标识
     */
    @Column(name = "platform_logo", columnDefinition = "varchar(20) comment '平台标识'")
    private String platformLogo;

    /**
     * 平台名称
     */
    @Column(name = "platform_name", columnDefinition = "varchar(50) comment '平台名称'")
    private String platformName;

    /**
     * ip地址
     */
    @Column(name = "ip_address", columnDefinition = "varchar(50) comment 'ip地址'")
    private String ipAddress;

    /**
     * 端口号
     */
    @Column(name = "port_number", columnDefinition = "varchar(10) comment '端口号'")
    private String portNumber;

    /**
     * 协议类型 ykcProtShadow-云快充
     */
    @Column(name = "protocol_type", columnDefinition = "varchar(32) comment '协议类型 ykcProtShadow-云快充'")
    private String protocolType;
}
