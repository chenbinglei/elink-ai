package com.sunmax.configure.entity.interflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 互联互通充电站信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_interflow_station")
public class InterflowStationEntity {

    /**
     * 充电站ID
     */
    @Id
    @Column(name = "station_id", columnDefinition = "varchar(64) NOT NULL comment '充电站ID'")
    private String stationId;

    /**
     * 充电站名称
     */
    @Column(name = "station_name", columnDefinition = "varchar(96) NOT NULL comment '充电站名称'")
    private String stationName;

    /**
     * 平台ID
     */
    @Column(name = "platform_id", columnDefinition = "varchar(64) NOT NULL comment '平台ID'")
    private String platformId;

    /**
     * 运营商ID
     */
    @Column(name = "operator_id", columnDefinition = "varchar(64) NOT NULL comment '运营商ID'")
    private String operatorId;

    /**
     * 设备所属方ID
     *
     * 设备所属运营平台组织机构代码
     */
    @Column(name = "equipment_owner_id", columnDefinition = "varchar(64) NOT NULL comment '设备所属方ID'")
    private String equipmentOwnerId;

    /**
     * 充电站国家代码
     */
    @Column(name = "country_code", columnDefinition = "varchar(64) NOT NULL comment '充电站国家代码'")
    private String countryCode;

    /**
     * 充电站省市辖区编码
     */
    @Column(name = "area_code", columnDefinition = "varchar(64) NOT NULL comment '充电站省市辖区编码'")
    private String areaCode;

    /**
     * 详细地址
     */
    @Column(name = "address", columnDefinition = "varchar(256) NOT NULL comment '详细地址'")
    private String address;

    /**
     * 站点电话(联系人电话)
     *
     * 能够联系场站工作人员进行协助的联系电话
     */
    @Column(name = "station_tel", columnDefinition = "varchar(30) comment '站点电话(联系人电话)'")
    private String stationTel;

    /**
     * 服务电话
     *
     * 平台服务电话，例如 400 的电话
     */
    @Column(name = "service_tel", columnDefinition = "varchar(30) NOT NULL comment '服务电话'")
    private String serviceTel;

    /**
     * 站点类型
     *
     * 1：公共
     * 50：个人
     * 100：公交（专用）
     * 101：环卫（专用）
     * 102：物流（专用）
     * 103：出租车（专用）
     * 255：其他
     */
    @Column(name = "station_type", columnDefinition = "int(10) NOT NULL comment '站点类型 1：公共,50：个人,100：公交（专用）,101：环卫（专用）,102：物流（专用）,103：出租车（专用）,255：其他'")
    private Integer stationType;

    /**
     * 站点状态
     * 0： 未知
     * 1： 建设中
     * 5： 关闭下线
     * 6： 维护中
     * 50：正常使用
     */
    @Column(name = "station_status", columnDefinition = "int(10) NOT NULL comment '站点状态 0：未知,1：建设中,5：关闭下线,6：维护中,50：正常使用'")
    private Integer stationStatus;

    /**
     * 车位数量
     *
     * 可停放进行充电的车位总数，默认：0 未知
     */
    @Column(name = "park_nums", columnDefinition = "int(10) NOT NULL comment '车位数量'")
    private Integer parkNums;

    /**
     * 经度
     */
    @Column(name = "station_lng", columnDefinition = "varchar(32) NOT NULL comment '经度'")
    private String stationLng;

    /**
     * 纬度
     */
    @Column(name = "station_lat", columnDefinition = "varchar(32) NOT NULL comment '纬度'")
    private String stationLat;

    /**
     * 站点引导
     */
    @Column(name = "site_guide", columnDefinition = "varchar(300) comment '站点引导'")
    private String siteGuide;

    /**
     * 建设场所
     *
     * 1：居民区
     * 2：公共机构
     * 3：企事业单位
     * 4：写字楼
     * 5：工业园区
     * 6：交通枢纽
     * 7：大型文体设施
     * 8：城市绿地
     * 9：大型建筑配建停车场
     * 10：路边停车位
     * 11：城际高速服务区
     * 255：其他
     */
    @Column(name = "construction", columnDefinition = "int(10) NOT NULL comment '建设场所\n" +
            "     * 1：居民区\n" +
            "     * 2：公共机构\n" +
            "     * 3：企事业单位\n" +
            "     * 4：写字楼\n" +
            "     * 5：工业园区\n" +
            "     * 6：交通枢纽\n" +
            "     * 7：大型文体设施\n" +
            "     * 8：城市绿地\n" +
            "     * 9：大型建筑配建停车场\n" +
            "     * 10：路边停车位\n" +
            "     * 11：城际高速服务区\n" +
            "     * 255：其他'")
    private Integer construction;

    /**
     * 站点照片
     */
    @Column(name = "pictures", columnDefinition = "varchar(256) comment '站点照片'")
    private String pictures;

    /**
     * 使用车型描述
     */
    @Column(name = "match_cars", columnDefinition = "varchar(256) comment '使用车型描述'")
    private String matchCars;

    /**
     * 车位楼层及数量描述
     */
    @Column(name = "parkInfo", columnDefinition = "varchar(256) comment '车位楼层及数量描述'")
    private String parkInfo;

    /**
     * 营业时间
     */
    @Column(name = "busine_hours", columnDefinition = "varchar(128) comment '营业时间'")
    private String busineHours;

    /**
     * 充电电费率
     *
     * 充电费描述
     */
    @Column(name = "electricity_fee", columnDefinition = "varchar(128) comment '充电电费率'")
    private String electricityFee;

    /**
     * 服务费率
     *
     * 服务费率描述
     */
    @Column(name = "service_fee", columnDefinition = "varchar(128) comment '服务费率'")
    private String serviceFee;

    /**
     * 停车费
     *
     * 停车费率描述
     */
    @Column(name = "park_fee", columnDefinition = "varchar(128) comment '停车费'")
    private String parkFee;

    /**
     * 支付方式:刷卡、线上、现金
     * 其中电子钱包类卡为刷卡，
     * 身份鉴权卡、微信/支付宝、APP 为线上
     */
    @Column(name = "payment", columnDefinition = "varchar(128) comment '支付方式:刷卡、线上、现金其中电子钱包类卡为刷卡，身份鉴权卡、微信/支付宝、APP 为线上'")
    private String payment;

    /**
     * 是否支持预约
     * 0 为不支持预约 、 1 为支持预约 。不填默认为 0
     */
    @Column(name = "support_order", columnDefinition = "tinyint(1) comment '是否支持预约 0 为不支持预约 、 1 为支持预约 。不填默认为 0'")
    private Integer supportOrder;

    /**
     * 备注
     */
    @Column(name = "remark", columnDefinition = "varchar(128) comment '备注'")
    private String remark;
}
