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
 * 站点设置表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_set_up")
public class SiteSetUpEntity extends BaseTimeEntity {

    /**
     * 站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) NOT NULL comment '站点id'")
    private String siteId;

    /**
     * 操作密码
     */
    @Column(name = "operate_password", columnDefinition = "varchar(32) NOT NULL comment '操作密码'")
    private String operatePassword;

    /**
     * app展示 1-展示 2-不展示
     */
    @Column(name = "app_show", columnDefinition = "tinyint(1) NOT NULL comment 'app展示 1-展示 2-不展示'")
    private Integer appShow;

    /**
     * 站点应用配置信息对象
     */
    @Column(name = "readwrite_object", columnDefinition = "longtext comment '站点应用配置信息对象'")
    private String readwriteObject;

    /**
     * 光伏发电量数据来源 20-逆变器 66-并网点
     */
    @Column(name = "pv_qt_source", columnDefinition = "tinyint(1) NOT NULL comment 'app展示 1-展示 2-不展示'")
    private Integer pvQtSource;

    /**
     * 二氧化碳减排量计算系数
     */
    @Column(name = "reduce_coeff", columnDefinition = "double(9,4) NOT NULL comment '二氧化碳减排量计算系数'")
    private Double reduceCoeff;

    /**
     * 节约标煤量计算系数
     */
    @Column(name = "tce_coeff", columnDefinition = "double(9,4) NOT NULL comment '节约标煤量计算系数'")
    private Double tceCoeff;

    /**
     * 等效植树计算系数
     */
    @Column(name = "tree_coeff", columnDefinition = "double(9,4) NOT NULL comment '等效植树计算系数'")
    private Double treeCoeff;

    /**
     * 系统名称
     */
    @Column(name = "system_name", columnDefinition = "varchar(255) comment '系统名称'")
    private String systemName;

}
