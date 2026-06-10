package com.sunmax.device.entity.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 站点能源场景类型关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_scenario_type")
public class ScenarioTypeEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
     */
    @Column(name = "scenario_type", columnDefinition = "tinyint(1) not null comment '能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电'")
    private Integer scenarioType;

    /**
     * 能源系统名称
     */
    @Column(name = "system_name", columnDefinition = "varchar(32) comment '能源系统名称'")
    private String systemName;

    /**
     * 模型id
     */
    @Column(name = "model_id", columnDefinition = "varchar(32) comment '模型id'")
    private String modelId;

    /**
     * 读写数据对象
     */
    @Column(name = "readwrite_object", columnDefinition = "longtext comment '读写数据对象'")
    private String readwriteObject;

    /**
     * 所属站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '所属站点id'")
    private String siteId;


}
