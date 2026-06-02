package com.sunmax.device.entity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 模型资产分类实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_asset_type")
public class AssetTypeEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 资产分类名称
     */
    @Column(name = "type_name", columnDefinition = "varchar(64) comment '资产分类名称'")
    private String typeName;

    /**
     * 父节点id
     */
    @Column(name = "parent_id", columnDefinition = "varchar(32) comment '父节点id'")
    private String parentId;

    /**
     * 类型 1-目录 2-资产
     */
    @Column(name = "type", columnDefinition = "tinyint(1) not null comment '类型 1-目录 2-资产'")
    private Integer type;

//    /**
//     * 伪删除状态 1-正常 2-删除
//     */
//    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
//    private Integer isDelete;

}
