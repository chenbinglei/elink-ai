package com.sunmax.configure.entity;

import com.sunmax.common.entity.BaseEntity;
import com.sunmax.configure.vo.PelChangeVo;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_pel")
public class PelEntity extends BaseEntity {

    /**
     * 名称
     */
    @Column(name = "name", columnDefinition = "varchar(64) comment '名称'")
    private String name;

    /**
     * 图元类型 1-文件上传 2-自定义图元
     */
    @Column(name = "pel_type", columnDefinition = "tinyint(1) comment '图元类型 1-文件上传 2-自定义图元'")
    private Integer pelType;

    /**
     * 绑定数据名称
     */
    @Column(name = "data_name", columnDefinition = "varchar(255) comment '绑定数据名称'")
    private String dataName;

    /**
     * 绑定数据文件路径
     */
    @Column(name = "data_path", columnDefinition = "varchar(1024) comment '绑定数据文件路径'")
    private String dataPath;

    /**
     * 文件名称
     */
    @Column(name = "file_name", columnDefinition = "varchar(255) comment '文件名称'")
    private String fileName;

    /**
     * 文件路径
     */
    @Column(name = "file_path", columnDefinition = "varchar(1024) comment '文件路径'")
    private String filePath;

    /**
     * 类型 1-文件夹 2-图元
     */
    @Column(name = "type", columnDefinition = "tinyint(1) comment '类型 1-文件夹 2-图元'")
    private Integer type;

    /**
     * 父节点id
     */
    @Column(name = "parent_id", columnDefinition = "varchar(32) comment '父节点id'")
    private String parentId;

    public PelEntity(PelChangeVo pelChangeVo) {
        BeanUtils.copyProperties(pelChangeVo, this);
    }

}
