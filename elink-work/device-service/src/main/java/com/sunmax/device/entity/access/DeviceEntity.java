package com.sunmax.device.entity.access;

import com.sunmax.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * 设备实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_device")
public class DeviceEntity extends BaseEntity {

    /**
     * 关联资产分类id
     */
    @Column(name = "type_id", columnDefinition = "varchar(32) not null comment '关联资产分类id'")
    private String typeId;

    /**
     * 模型id
     */
    @Column(name = "model_id", columnDefinition = "varchar(32) not null comment '模型id'")
    private String modelId;

    /**
     * 站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) not null comment '站点id'")
    private String siteId;

    /**
     * 父节点id
     */
    @Column(name = "parent_id", columnDefinition = "varchar(32) comment '父节点id'")
    private String parentId;

    /**
     * 设备名称
     */
    @Column(name = "device_name", columnDefinition = "varchar(64) not null comment '设备名称'")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Column(name = "device_number", columnDefinition = "varchar(64) comment '设备序列号'")
    private String deviceNumber;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @Column(name = "access_type", columnDefinition = "tinyint(1) not null comment '接入类型 1-直连设备 2-网关设备 3-网关子设备'")
    private Integer accessType;

    /**
     * 读写数据对象
     */
    @Column(name = "readwrite_object", columnDefinition = "longtext comment '读写数据对象'")
    private String readwriteObject;

    /**
     * 设备描述
     */
    @Column(name = "device_desc", columnDefinition = "varchar(255) comment '设备描述'")
    private String deviceDesc;

    /**
     * 设备图片路径
     */
    @Column(name = "image_paths", columnDefinition = "longtext comment '设备图片路径'")
    private String imagePaths;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @Column(name = "operate_status", columnDefinition = "tinyint(1) default 0 comment '运营状态 0-未知 1-投运 2-检修 3-退役'")
    private Integer operateStatus;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;

}
