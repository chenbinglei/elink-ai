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
 * 站点信息-旧版
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site")
public class SiteEntity extends BaseEntity {

    /**
     * 所属租户id(拥有者企业)
     */
    @Column(name = "tenant_id", columnDefinition = "varchar(64) comment '所属租户id(拥有者企业)'")
    private String tenantId;

    /**
     * 站点名称
     */
    @Column(name = "site_name", columnDefinition = "varchar(64) not null comment '站点名称'")
    private String siteName;

    /**
     * 业主单位(关联b_tenant_info租户信息表中唯一id)
     */
    @Column(name = "owner_unit", columnDefinition = "varchar(64) not null comment '业主单位(关联b_tenant_info租户信息表中唯一id)'")
    private String ownerUnit;

    /**
     * 运营单位(关联b_tenant_info租户信息表中唯一id)
     */
    @Column(name = "operate_unit", columnDefinition = "varchar(64) not null comment '运营单位(关联b_tenant_info租户信息表中唯一id)'")
    private String operateUnit;

    /**
     * 投运时间
     */
    @Column(name = "operation_date", columnDefinition = "varchar(32) comment '投运时间'")
    private String operationDate;

    /**
     * 站点描述
     */
    @Column(name = "site_describe", columnDefinition = "varchar(100) comment '站点描述'")
    private String siteDescribe;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Column(name = "site_status", columnDefinition = "tinyint(1) not null comment '站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中'")
    private Integer siteStatus;

    /**
     * 经度
     */
    @Column(name = "longitude", columnDefinition = "varchar(20) comment '经度'")
    private String longitude;

    /**
     * 纬度
     */
    @Column(name = "latitude", columnDefinition = "varchar(20) comment '纬度'")
    private String latitude;

    /**
     * 所在省份
     */
    @Column(name = "province", columnDefinition = "varchar(20) comment '省份'")
    private String province;

    /**
     * 所在市
     */
    @Column(name = "city", columnDefinition = "varchar(20) comment '所在市'")
    private String city;

    /**
     * 所在区县
     */
    @Column(name = "county", columnDefinition = "varchar(20) comment '所在区县'")
    private String county;

    /**
     * 详细地址
     */
    @Column(name = "address", columnDefinition = "varchar(100) comment '详细地址'")
    private String address;

    /**
     * 联系人
     */
    @Column(name = "contacts", columnDefinition = "varchar(20) comment '联系人'")
    private String contacts;

    /**
     * 联系电话
     */
    @Column(name = "phone", columnDefinition = "varchar(20) comment '联系电话'")
    private String phone;

    /**
     * 站点图片路径
     */
    @Column(name = "image_path", columnDefinition = "text comment '站点图片路径'")
    private String imagePath;

    /**
     * 伪删除状态 1-正常 2-删除
     */
    @Column(name = "is_delete", columnDefinition = "tinyint(1) not null comment '伪删除状态 1-正常 2-删除'")
    private Integer isDelete;

    /**
     * 开放类型 1-对外开放；2-专用站点
     */
    @Column(name = "open_type", columnDefinition = "tinyint(1) comment '开放类型 1-对外开放；2-专用站点'")
    private Integer openType;

    /**
     * 营业时间
     */
    @Column(name = "business_hours", columnDefinition = "varchar(255) comment '营业时间'")
    private String businessHours;

    /**
     * 停车费用描述
     */
    @Column(name = "parkCostDesc", columnDefinition = "varchar(255) comment '停车费用描述'")
    private String parkCostDesc;

    /**
     * 建筑场所：1-居民区；2-公共机构；3-企事业单位；4-写字楼；5-工业园区；6-交通枢纽；7-大型文体设施；8城市绿地；9-大型建筑配建停车场；10-路边停车位；11-城际高速服务区；12-国省道路沿线；13-城际快速公路沿线；14-其他
     */
    @Column(name = "build_site", columnDefinition = "tinyint(1) comment '建筑场所：1-居民区；2-公共机构；3-企事业单位；4-写字楼；5-工业园区；6-交通枢纽；7-大型文体设施；8城市绿地；9-大型建筑配建停车场；10-路边停车位；11-城际高速服务区；12-国省道路沿线；13-城际快速公路沿线；14-其他'")
    private Integer buildSite;
}
