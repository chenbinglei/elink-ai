package com.sunmax.device.entity.access;

import com.sunmax.common.entity.BaseTimeEntity;
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
 * 站点拓扑节点数据项实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_top_item")
public class SiteTopItemEntity extends BaseTimeEntity {

    /**
     * 节点id
     */
    @Column(name = "node_id",columnDefinition = "varchar(32) NOT NULL comment '节点id'")
    private String nodeId;

    /**
     * 数据编号
     */
    @Column(name = "data_code",columnDefinition = "varchar(32) NOT NULL comment '数据编号'")
    private String dataCode;

    /**
     * 数据名称
     */
    @Column(name = "data_name",columnDefinition = "varchar(32) NOT NULL comment '数据名称'")
    private String dataName;

    /**
     * 数据展示名称
     */
    @Column(name = "show_name",columnDefinition = "varchar(32) NOT NULL comment '数据展示名称'")
    private String showName;

    /**
     * 数据展示类型 1-显示 2-隐藏
     */
    @Column(name = "show_type",columnDefinition = "tinyint(1) NOT NULL comment '数据展示类型 1-显示 2-隐藏'")
    private Integer showType;

    /**
     * 数据位置类型 1-上 2-下 3-左 4-右
     */
    @Column(name = "position_type",columnDefinition = "tinyint(1) NOT NULL comment '数据位置类型 1-上 2-下 3-左 4-右'")
    private Integer positionType;


}
