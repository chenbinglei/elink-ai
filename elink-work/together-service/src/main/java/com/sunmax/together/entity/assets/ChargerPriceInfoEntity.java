package com.sunmax.together.entity.assets;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 站点充放电价格信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_charger_price_info")
public class ChargerPriceInfoEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(16) comment '主键id'")
    private String id;

    /**
     * 所属站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) comment '所属站点id'")
    private String siteId;

    /**
     * 生效类型 1-立即生效 2-自定义时间
     */
    @Column(name = "take_type",columnDefinition = "tinyint(1) comment '生效类型 1-立即生效 2-自定义时间'")
    private Integer takeType;

    /**
     * 生效时间
     */
    @Column(name = "take_time",columnDefinition = "varchar(20) comment '生效时间'")
    private String takeTime;

    /**
     * 设备类型 1-直流 2-交流
     */
    @Column(name = "device_type",columnDefinition = "tinyint(1) comment '设备类型 1-直流 2-交流'")
    private Integer deviceType;

    /**
     * 定价类型 1-全天同价 2-分时段定价
     */
    @Column(name = "fixed_type",columnDefinition = "tinyint(1) comment '定价类型 1-全天同价 2-分时段定价'")
    private Integer fixedType;

    /**
     * 价格类型 1-充电 2-放电
     */
    @Column(name = "price_type",columnDefinition = "tinyint(1) comment '价格类型 1-充电 2-放电'")
    private Integer priceType;

    /**
     * 价格状态 1-生效中 2-待生效 3-已失效
     */
    @Column(name = "price_state",columnDefinition = "tinyint(1) comment '价格状态 1-生效中 2-待生效 3-已失效'")
    private Integer priceState;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;
}
