package com.sunmax.device.entity.access;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * 站点拓扑节点信息表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_top_node")
public class SiteTopNodeEntity extends BaseTimeEntity {

    /**
     * 站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) NOT NULL comment '站点id'")
    private String siteId;

    /**
     * 父级id
     */
    @Column(name = "parent_id", columnDefinition = "varchar(32) comment '父级id'")
    private String parentId;

    /**
     * 节点名称
     */
    @Column(name = "node_name", columnDefinition = "varchar(32) NOT NULL  comment '节点名称'")
    private String nodeName;

    /**
     * 节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆
     */
    @Column(name = "node_type", columnDefinition = "int(2) NOT NULL comment '节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆'")
    private Integer nodeType;

    /**
     * 页面扩展属性
     */
    @Column(name = "page_extend", columnDefinition = "longtext comment '页面扩展属性'")
    private String pageExtend;

    /**
     * 扩展字段属性
     */
    @Column(name = "rea_object", columnDefinition = "longtext comment '扩展字段属性'")
    private String reaObject;

    /**
     * 多个关联设备id 例如["deviceId1","deviceId2"] 储能柜存储[{"pcsId":"pcsId1","batteryId":"batteryId1"},{"pcsId":"pcsId2","batteryId":"batteryId2"}]
     */
    @Column(name = "device_ids", columnDefinition = "longtext comment '多个设备id 例如[\"deviceId1\",\"deviceId2\"] 储能柜存储[{\"pcsId\":\"pcsId1\",\"batteryId\":\"batteryId1\"},{\"pcsId\":\"pcsId2\",\"batteryId\":\"batteryId2\"}]'")
    private String deviceIds;

}
