package com.sunmax.crontab.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 系统变量实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "n_system_variable")
public class SystemVariableEntity extends BaseEntity {

    /**
     * 变量名称
     */
    @Column(name = "var_name", columnDefinition = "varchar(20) comment '变量名称'")
    private String varName;

    /**
     * 变量标识
     */
    @Column(name = "var_code", columnDefinition = "varchar(64) comment '变量标识'")
    private String varCode;

    /**
     * 变量类型 1-设备类型 2-站点类型
     */
    @Column(name = "var_type", columnDefinition = "tinyint(1) comment '变量类型 1-设备类型 2-站点类型'")
    private Integer varType;

    /**
     * 数据来源 1-计算节点 2-模型功能点
     */
    @Column(name = "data_source", columnDefinition = "tinyint(1) comment '数据来源 1-计算节点 2-模型功能点'")
    private Integer dataSource;

    /**
     * 备注
     */
    @Column(name = "remark", columnDefinition = "varchar(255) comment '备注'")
    private String remark;
}
