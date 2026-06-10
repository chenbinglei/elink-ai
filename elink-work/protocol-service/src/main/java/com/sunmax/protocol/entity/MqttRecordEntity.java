package com.sunmax.protocol.entity;

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
@Table(name = "b_mqtt_record")
public class MqttRecordEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 电桩编号
     */
    @Column(name = "pile_code", columnDefinition = "varchar(64) NOT NULL comment '电桩编号'")
    private String pileCode;

    /**
     * 枪编号
     */
    @Column(name = "gun_code", columnDefinition = "varchar(64) comment '枪编号'")
    private String gunCode;

    /**
     * 报文类型 1-发送 2-接收
     */
    @Column(name = "type", columnDefinition = "tinyint(1) NOT NULL comment '报文类型 1-发送 2-接收'")
    private Integer type;

    /**
     * 协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应
     */
    @Column(name = "protocol_type", columnDefinition = "tinyint(1) NOT NULL comment '协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应'")
    private Integer protocolType;

    /**
     * 消息类型 1-外网 2-内网
     */
    @Column(name = "message_type", columnDefinition = "tinyint(1) NOT NULL comment '消息类型 1-外网 2-内网'")
    private Integer messageType;

    /**
     * 报文命令
     */
    @Column(name = "cmd", columnDefinition = "varchar(255) NOT NULL comment '报文命令'")
    private String cmd;

    /**
     * 报文内容
     */
    @Column(name = "content", columnDefinition = "longtext NOT NULL comment '报文内容'")
    private String content;

    /**
     * 时间
     */
    @CreatedDate
    @Column(name = "date_time", columnDefinition = "datetime(0) comment '时间'")
    private LocalDateTime dateTime;

}
