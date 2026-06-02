package com.sunmax.configure.entity;

import com.sunmax.common.entity.BaseEntity;
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
@Table(name = "b_data_source")
public class DataSourceEntity extends BaseEntity {

    /**
     * 数据源名称
     */
    @Column(name = "name", columnDefinition = "varchar(64) comment '数据源名称'")
    private String name;

    /**
     * 通信方式 1-websocket 2-http 3-mqtt
     */
    @Column(name = "type", columnDefinition = "tinyint(1) comment '通信方式 1-websocket 2-http 3-mqtt'")
    private Integer type;

    /**
     * url地址
     */
    @Column(name = "url", columnDefinition = "varchar(128) comment 'url地址'")
    private String url;

    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "varchar(255) comment '描述'")
    private String description;

    /**
     * 动态字段
     */
    @Column(name = "dynamic_field", columnDefinition = "longtext comment '动态字段'")
    private String dynamicField;

    /**
     * 请求参数Key
     */
    @Column(name = "request_key", columnDefinition = "text comment '请求参数Key'")
    private String requestKey;

}
