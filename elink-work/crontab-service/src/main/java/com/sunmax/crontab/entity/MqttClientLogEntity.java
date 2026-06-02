package com.sunmax.crontab.entity;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_mqtt_client_log")
public class MqttClientLogEntity extends BaseTimeEntity {

    /**
     * 客户端id
     */
    @Column(name = "client_id", columnDefinition = "varchar(64) comment '客户端id'")
    private String clientId;

    /**
     * 主题
     */
    @Column(name = "topic", columnDefinition = "varchar(255) comment '主题'")
    private String topic;

    /**
     * 消息
     */
    @Column(name = "message", columnDefinition = "longtext comment '消息'")
    private String message;

    /**
     * 入参
     */
    @Column(name = "params", columnDefinition = "longtext comment '入参'")
    private String params;

    /**
     * 出参
     */
    @Column(name = "result", columnDefinition = "longtext comment '出参'")
    private String result;

    /**
     * 类型 1-发送消息 2-接收消息
     */
    @Column(name = "type", columnDefinition = "tinyint(2) comment '类型 1-发送消息 2-接收消息'")
    private Integer type;

}
