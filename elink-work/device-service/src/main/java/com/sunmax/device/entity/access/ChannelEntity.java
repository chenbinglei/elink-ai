package com.sunmax.device.entity.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 通道实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_channel")
public class ChannelEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 设备id
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) not null comment '设备id'")
    private String deviceId;

    /**
     * 设备序列号
     */
    @Column(name = "device_number", columnDefinition = "varchar(64) not null comment '设备序列号'")
    private String deviceNumber;

    /**
     * 通道名称
     */
    @Column(name = "channel_name", columnDefinition = "varchar(64) not null comment '通道名称'")
    private String channelName;

    /**
     * 协议类型 MQTT,HTTP,TCP
     */
    @Column(name = "protocol_type", columnDefinition = "varchar(32) not null comment '协议类型 MQTT,HTTP,TCP'")
    private String protocolType;

    /**
     * 接入协议
     */
    @Column(name = "access_protocol", columnDefinition = "varchar(64) not null comment '接入协议'")
    private String accessProtocol;

    /**
     * IP地址
     */
    @Column(name = "ip", columnDefinition = "varchar(64) comment 'IP地址'")
    private String ip;

    /**
     * 端口号
     */
    @Column(name = "port", columnDefinition = "int(10) comment '端口号'")
    private Integer port;

    /**
     * 最后修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '最后修改时间'")
    private LocalDateTime updateTime;

}
