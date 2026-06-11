package com.sunmax.device.entity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 模型标准功能关联实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_model_function")
public class ModelFunctionEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 关联模型表id
     */
    @Column(name = "model_id", columnDefinition = "varchar(32) comment '关联模型表id'")
    private String modelId;

    /**
     * 关联标准功能表id
     */
    @Column(name = "function_id", columnDefinition = "varchar(32) comment '关联标准功能表id'")
    private String functionId;

    /**
     * 序列号
     */
    @Column(name = "serial_num", columnDefinition = "int(11) not null comment '序列号'")
    private Integer serialNum;

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
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;

}
