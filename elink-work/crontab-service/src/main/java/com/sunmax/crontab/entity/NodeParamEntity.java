package com.sunmax.crontab.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_node_param")
public class NodeParamEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 所属节点id
     */
    @Column(name = "node_id", columnDefinition = "varchar(32) comment '所属节点id'")
    private String nodeId;

    /**
     * 参数类型 1-功能点 2-节点
     */
    @Column(name = "param_type", columnDefinition = "tinyint(1) comment '参数类型 1-功能点 2-节点'")
    private Integer paramType;

    /**
     * 设备/站点id
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) comment '设备/站点id'")
    private String deviceId;

    /**
     * 参数名称
     */
    @Column(name = "param_name", columnDefinition = "varchar(32) comment '参数名称'")
    private String paramName;

    /**
     * 来源标识(功能点类型-功能点标识，节点类型-节点存储id)
     */
    @Column(name = "source_code", columnDefinition = "varchar(256) comment '来源标识(功能点类型-功能点标识，节点类型-节点存储id)'")
    private String sourceCode;

    /**
     * 来源id
     */
    @Column(name = "source_id", columnDefinition = "varchar(256) comment '来源id'")
    private String sourceId;

    /**
     * 索引号
     */
    @Column(name = "index_num",columnDefinition = "int(10) comment '索引号'")
    private Integer indexNum;

    /**
     * 最大值
     */
    @Column(name = "max_value", columnDefinition = "bigint(20) comment '最大值'")
    private Long maxValue;

    /**
     * 最小值
     */
    @Column(name = "min_value", columnDefinition = "bigint(20) comment '最小值'")
    private Long minValue;

    /**
     * 缺省值
     */
    @Column(name = "default_value", columnDefinition = "double(9,2) comment '缺省值'")
    private Double defaultValue;

    /**
     * 索引是否可修改(前端使用字段) 0-是 1-否
     */
    @Column(name = "is_index_disabled", columnDefinition = "tinyint(1) comment '索引是否可修改(前端使用字段) 0-是 1-否'")
    private Integer isIndexDisabled;
}
