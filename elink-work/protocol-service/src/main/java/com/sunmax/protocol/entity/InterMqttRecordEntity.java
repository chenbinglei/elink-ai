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
@Table(name = "b_inter_mqtt_record")
public class InterMqttRecordEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 命令码
     */
    @Column(name = "cmd", columnDefinition = "varchar(10) comment '命令码'")
    private String cmd;

    /**
     * 设备编号
     */
    @Column(name = "device_code", columnDefinition = "varchar(64) comment '设备编号'")
    private String deviceCode;

    /**
     * 数据内容
     */
    @Column(name = "content", columnDefinition = "longtext comment '数据内容'")
    private String content;

    /**
     * 报文类型 0-发送 1-接收
     */
    @Column(name = "type", columnDefinition = "tinyint(1) NOT NULL comment '报文类型 0-发送 1-接收'")
    private Integer type;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) NOT NULL comment '创建时间'")
    private LocalDateTime createTime;

}
