package com.sunmax.device.entity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

/**
 * 模型标准功能实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_function")
public class FunctionEntity {

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
     * 功能名称
     */
    @Column(name = "function_name", columnDefinition = "varchar(64) not null comment '标准功能名称'")
    private String functionName;

    /**
     * 功能标识
     */
    @Column(name = "function_logo", columnDefinition = "varchar(32) not null comment '功能标识'")
    private String functionLogo;

    /**
     * 功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
     */
    @Column(name = "function_type", columnDefinition = "tinyint(1) not null comment '功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调'")
    private Integer functionType;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @Column(name = "data_type", columnDefinition = "tinyint(1) not null comment '数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)'")
    private Integer dataType;

    /**
     * 取值范围
     */
    @Column(name = "value_range", columnDefinition = "varchar(255) comment '取值范围'")
    private String valueRange;

    /**
     * 精度 1-1 2-0.1 3-0.01 4-0.001 5-0.0001 6-0.00001
     */
    @Column(name = "accuracy", columnDefinition = "tinyint(1) comment '精度 1-1 2-0.1 3-0.01 4-0.001 5-0.0001 6-0.00001'")
    private Integer accuracy;

    /**
     * 单位
     */
    @Column(name = "unit", columnDefinition = "varchar(32) comment '单位'")
    private String unit;

    /**
     * 数据对象 {key:value} 字符串直接存长度
     */
    @Column(name = "data_object", columnDefinition = "longtext comment '数据对象 {key:value} 字符串直接存长度'")
    private String dataObject;

    /**
     * 描述
     */
    @Column(name = "function_desc", columnDefinition = "varchar(255) comment '描述'")
    private String functionDesc;

    /**
     * 字段编码
     */
    @Column(name = "field_code", columnDefinition = "varchar(32) comment '字段编码'")
    private String fieldCode;

    /**
     * 字段类型 1-充电桩类型 2-充电枪类型
     */
    @Column(name = "field_type", columnDefinition = "tinyint(1) comment '字段类型 1-充电桩类型 2-充电枪类型'")
    private Integer fieldType;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    public LocalDateTime createTime;

}
