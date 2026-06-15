package com.sunmax.device.entity.access;

import com.sunmax.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * 站点信息-新版
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_info")
public class SiteInfoEntity extends BaseEntity {

    /**
     * 所属租户id(拥有者企业,关联b_tenant_info租户信息表中唯一id)
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(64) not null comment '所属租户id(拥有者企业,关联b_tenant_info租户信息表中唯一id)'")
    private String tenantId;

    /**
     * 站点名称
     */
    @Column(name = "site_name", columnDefinition = "varchar(64) not null comment '站点名称'")
    private String siteName;

    /**
     * 站点编码
     */
    @Column(name = "site_code", columnDefinition = "varchar(128) not null comment '站点编码'")
    private String siteCode;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Column(name = "site_status", columnDefinition = "tinyint(1) not null comment '站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中'")
    private Integer siteStatus;

    /**
     * 站点描述
     */
    @Column(name = "site_describe", columnDefinition = "varchar(100) comment '站点描述'")
    private String siteDescribe;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)
     */
    @Column(name = "scenario_types", columnDefinition = "varchar(64) comment '能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)'")
    private String scenarioTypes;

    /**
     * 站点模型id
     */
    @Column(name = "site_model_id", columnDefinition = "varchar(32) not null comment '站点模型id'")
    private String siteModelId;

    /**
     * 站点读写数据对象
     */
    @Column(name = "site_readwrite_object", columnDefinition = "longtext comment '站点读写数据对象'")
    private String siteReadwriteObject;

    /**
     * 站点图片路径
     */
    @Column(name = "image_path", columnDefinition = "text comment '站点图片路径'")
    private String imagePath;

    /**
     * 来源类型 1-自建 2-城市充电接入
     */
    @Column(name = "source_type", columnDefinition = "tinyint(1) not null comment '来源类型 1-自建 2-城市充电接入'")
    private Integer sourceType;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;

    /**
     * 产权方id(关联b_tenant_info租户信息表中唯一id)
     */
    @Column(name = "property_id", columnDefinition = "varchar(64) not null comment '产权方id(关联b_tenant_info租户信息表中唯一id)'")
    private String propertyId;

    /**
     * 运营商id(关联b_tenant_info租户信息表中唯一id)
     */
    @Column(name = "operator_id", columnDefinition = "varchar(64) not null comment '运营商id(关联b_tenant_info租户信息表中唯一id)'")
    private String operatorId;
}
