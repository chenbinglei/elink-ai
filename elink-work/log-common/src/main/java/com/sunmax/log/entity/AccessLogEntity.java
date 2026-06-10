package com.sunmax.log.entity;

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
@Table(name = "b_access_log")
public class AccessLogEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;

    /**
     * 客户端id
     */
    @Column(name = "client_id", columnDefinition = "varchar(32) comment '客户端id'")
    private String clientId;

    /**
     * 用户账号
     */
    @Column(name = "user_account", columnDefinition = "varchar(255) comment '用户账号'")
    private String userAccount;

    /**
     * 远程ip地址
     */
    @Column(name = "remote_addr", columnDefinition = "varchar(20) comment '远程ip地址'")
    private String remoteAddr;

    /**
     * 远程端口
     */
    @Column(name = "remote_port", columnDefinition = "int(11) comment '远程端口'")
    private Integer remotePort;

    /**
     * 本地ip地址
     */
    @Column(name = "local_addr", columnDefinition = "varchar(20) comment '本地ip地址'")
    private String localAddr;

    /**
     * 本地端口
     */
    @Column(name = "local_port", columnDefinition = "int(11) comment '本地端口'")
    private Integer localPort;

    /**
     * 方法
     */
    @Column(name = "method", columnDefinition = "varchar(20) comment '方法'")
    private String method;

    /**
     * url
     */
    @Column(name = "url", columnDefinition = "varchar(255) comment 'url'")
    private String url;

    /**
     * 操作内容
     */
    @Column(name = "content", columnDefinition = "longtext comment '操作内容'")
    private String content;

    /**
     * 响应状态码
     */
    @Column(name = "code", columnDefinition = "int(11) comment '响应状态码'")
    private Integer code;

    /**
     * 响应结果描述
     */
    @Column(name = "message", columnDefinition = "varchar(255) comment '响应结果描述'")
    private String message;

}
