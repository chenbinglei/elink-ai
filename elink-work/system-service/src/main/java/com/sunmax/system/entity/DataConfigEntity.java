package com.sunmax.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 数据配置实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_data_config")
public class DataConfigEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 数据转发id
     */
    @Column(name = "forward_id", columnDefinition = "varchar(32) comment '数据转发id'")
    private String forwardId;

    /**
     * 站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 协议编码
     */
    @Column(name = "protocol_code", columnDefinition = "varchar(64) comment '协议编码'")
    private String protocolCode;

    /**
     * 动态配置
     */
    @Column(name = "dynamic_configs", columnDefinition = "longtext comment '动态配置'")
    private String dynamicConfigs;

}
