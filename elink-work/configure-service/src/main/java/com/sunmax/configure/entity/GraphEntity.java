package com.sunmax.configure.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_graph")
public class GraphEntity extends BaseEntity {

    /**
     * 名称
     */
    @Column(name = "name", columnDefinition = "varchar(64) comment '图形名称'")
    private String name;

    /**
     * 类型 1-文件夹 2-图模文件
     */
    @Column(name = "type", columnDefinition = "tinyint(1) comment '类型 1-文件夹 2-图模文件'")
    private Integer type;

    /**
     * 状态 0-无 1-有更新 2-已发布
     */
    @Column(name = "status", columnDefinition = "tinyint(1) comment '状态 0-无 1-有更新 2-已发布'")
    private Integer status;

    /**
     * 锁定状态 0-未锁定 1-锁定
     */
    @Column(name = "lock_status", columnDefinition = "tinyint(1) comment '锁定状态 0-未锁定 1-锁定'")
    private Integer lockStatus;

    /**
     * 父级id
     */
    @Column(name = "parent_id", columnDefinition = "varchar(64) comment '父级id'")
    private String parentId;

    /**
     * 域名id
     */
    @Column(name = "domain_id", columnDefinition = "varchar(64) comment '域名id'")
    private String domainId;

    /**
     * 保存文件路径
     */
    @Column(name = "file_path", columnDefinition = "varchar(255) comment '文件路径'")
    private String filePath;

    /**
     * 发布文件路径
     */
    @Column(name = "public_file_path", columnDefinition = "varchar(255) comment '文件路径'")
    private String publicFilePath;

    /**
     * 站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 设备变量标识
     */
    @Column(name = "device_variables", columnDefinition = "longtext comment '设备变量标识'")
    private String deviceVariables;

}
