package com.sunmax.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 产品模块实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_product")
public class ProductEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
//    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
//    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 产品名称
     */
    @Column(name = "product_name",columnDefinition = "varchar(20) comment '产品名称'")
    private String productName;

    /**
     * 产品英文名称
     */
    @Column(name = "english_name",columnDefinition = "varchar(50) comment '产品英文名称'")
    private String englishName;

    /**
     * 父节点id
     */
    @Column(name = "parent_id",columnDefinition = "varchar(32) comment '父节点id'")
    private String parentId;

    /**
     * 客户端id
     */
    @Column(name = "client_id",columnDefinition = "varchar(64) comment '客户端id'")
    private String clientId;

    /**
     * 目录顺序
     */
    @Column(name = "directory_desc", columnDefinition = "int(10) comment '目录顺序'")
    private Integer directoryDesc;

    /**
     * 权限状态 0-显示 1-不显示
     */
    @Column(name = "is_hidden", columnDefinition = "tinyint(1) comment '权限状态 0-显示 1-不显示'")
    private Integer isHidden;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'")
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '最后修改时间'")
    private LocalDateTime updateTime;

    /**
     * 状态 0-正常 1-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(0) comment '状态 0-正常 1-删除'")
    private Integer isDelete;

}
