package com.sunmax.device.entity.model;

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

/**
 * 模型扩展属性实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_rea")
public class ReaEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 关联资产分类id
     */
    @Column(name = "type_id", columnDefinition = "varchar(32) comment '关联资产分类id'")
    private String typeId;

    /**
     * 扩展属性名称
     */
    @Column(name = "rea_name", columnDefinition = "varchar(64) not null comment '扩展属性名称'")
    private String reaName;

    /**
     * 字段名称
     */
    @Column(name = "field_name", columnDefinition = "varchar(64) not null comment '字段名称'")
    private String fieldName;

    /**
     * 扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
     */
    @Column(name = "rea_type", columnDefinition = "tinyint(1) not null comment '扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本'")
    private Integer reaType;

//    /**
//     * 读写类型 1-只读 2-读写
//     */
//    @Column(name = "read_write_type", columnDefinition = "tinyint(1) not null comment '读写类型 1-只读 2-读写'")
//    private Integer readWriteType;

    /**
     * 是否必填 true-是 false-否
     */
    @Column(name = "required", columnDefinition = "tinyint(1) comment '是否必填 true-是 false-否'")
    private Boolean required;

//    /**
//     * 默认值
//     */
//    @Column(name = "default_value", columnDefinition = "varchar(64) comment '默认值'")
//    private String defaultValue;

    /**
     * 单位
     */
    @Column(name = "unit", columnDefinition = "varchar(20) comment '单位'")
    private String unit;

    /**
     * 额外值
     */
    @Column(name = "extra_value", columnDefinition = "text comment '额外值'")
    private String extraValue;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    public LocalDateTime createTime;

}
