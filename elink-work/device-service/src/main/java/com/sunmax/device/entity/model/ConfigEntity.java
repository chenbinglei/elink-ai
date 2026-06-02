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
 * 模型参数配置实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_config")
public class ConfigEntity {

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
    @Column(name = "type_id", columnDefinition = "varchar(32) not null comment '关联资产分类id'")
    private String typeId;

    /**
     * 字段标识
     */
    @Column(name = "field_logo", columnDefinition = "varchar(8) not null comment '字段标识'")
    private String fieldLogo;

    /**
     * 字段名称
     */
    @Column(name = "field_name", columnDefinition = "varchar(64) not null comment '字段名称'")
    private String fieldName;

    /**
     * 字段长度
     */
    @Column(name = "field_length", columnDefinition = "int(11) not null comment '字段长度'")
    private Integer fieldLength;

    /**
     * 参数类型 1-系统参数 2-TCU参数 3-PCU参数 4-桩参数 5-枪参数
     */
    @Column(name = "param_type", columnDefinition = "tinyint(1) not null comment '参数类型 1-系统参数 2-TCU参数 3-PCU参数 4-桩参数 5-枪参数'")
    private Integer paramType;

    /**
     * 字段类型 1-数值 2-文字 3-选项
     */
    @Column(name = "field_type", columnDefinition = "tinyint(1) not null comment '字段类型 1-数值 2-文字 3-选项'")
    private Integer fieldType;

    /**
     * 读写类型 1-只读 2-读写
     */
    @Column(name = "rw_type", columnDefinition = "tinyint(1) not null comment '读写类型 1-只读 2-读写'")
    private Integer rwType;

    /**
     * 额外值
     */
    @Column(name = "extra_value", columnDefinition = "text comment '额外值'")
    private String extraValue;

    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "text comment '描述'")
    private String description;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    public LocalDateTime createTime;

}
