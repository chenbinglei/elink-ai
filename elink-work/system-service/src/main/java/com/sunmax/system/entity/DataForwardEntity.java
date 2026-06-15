package com.sunmax.system.entity;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

/**
 * 数据转发实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_data_forward")
public class DataForwardEntity extends BaseTimeEntity {

    /**
     * 通道名称
     */
    @Column(name = "channel_name", columnDefinition = "varchar(64) comment '通道名称'")
    private String channelName;

    /**
     * 接入协议类型 1-mqtt 2-http
     */
    @Column(name = "protocol_type", columnDefinition = "tinyint(1) comment '接入协议类型 1-mqtt 2-http'")
    private Integer protocolType;

    /**
     * 接入协议标识
     */
    @Column(name = "protocol_code", columnDefinition = "varchar(32) comment '接入协议标识'")
    private String protocolCode;

    /**
     * 动态字段
     */
    @Column(name = "dynamic_fields", columnDefinition = "longtext comment '动态字段'")
    private String dynamicFields;

    /**
     * 地址
     */
    @Column(name = "address", columnDefinition = "varchar(255) comment '地址'")
    private String address;

    /**
     * 状态 1-启用 2-断开
     */
    @Column(name = "status", columnDefinition = "tinyint(1) comment '状态 1-启用 2-断开'")
    private Integer status;

}
