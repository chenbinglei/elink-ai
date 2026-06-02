package com.sunmax.system.entity;

import com.sunmax.system.vo.PermissionChangeVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

/**
 * 权限表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_permission")
public class PermissionEntity {

    /**
     * 唯一id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 权限类型 1-页面 2-控件
     */
    @Column(name = "permission_type", columnDefinition = "tinyint(1) comment '权限类型 1-页面 2-控件'")
    private Integer permissionType;

    /**
     * 权限编码
     */
    @Column(name = "permission_code", columnDefinition = "varchar(16) comment '权限编码'")
    private String permissionCode;

    /**
     * 权限名称
     */
    @Column(name = "permission_name", columnDefinition = "varchar(255) comment '权限名称'")
    private String permissionName;

    /**
     * URL
     */
    @Column(name = "url", columnDefinition = "varchar(255) comment 'URL'")
    private String url;

    /**
     * 父级id
     */
    @Column(name = "parent_id", columnDefinition = "varchar(32) comment '父级id'")
    private String parentId;

    /**
     * 目录顺序
     */
    @Column(name = "directory_desc", columnDefinition = "int(10) comment '目录顺序'")
    private Integer directoryDesc;

    /**
     * 图标路径
     */
    @Column(name = "icon_path", columnDefinition = "varchar(255) comment '图标路径'")
    private String iconPath;

    /**
     * 说明
     */
    @Column(name = "explanation", columnDefinition = "varchar(255) comment '说明'")
    private String explanation;

    /**
     * 所属模块id
     */
    @Column(name = "module_id", columnDefinition = "varchar(32) comment '所属模块id'")
    private String moduleId;

    /**
     * 权限状态 0-显示 1-不显示
     */
    @Column(name = "is_hidden", columnDefinition = "tinyint(1) comment '权限状态 0-显示 1-不显示'")
    private Integer isHidden;

    /**
     * 是否有界面 1-是 2-否
     */
    @Column(name = "is_layout", columnDefinition = "tinyint(1) comment '是否有界面 1-是 2-否'")
    private Integer isLayout;

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
     * 伪删除状态 0-正常 1-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(0) comment '状态 0-正常 1-删除'")
    private Integer isDelete;

    public PermissionEntity(PermissionChangeVo permissionChangeVo) {
        BeanUtils.copyProperties(permissionChangeVo,this);
    }

}
