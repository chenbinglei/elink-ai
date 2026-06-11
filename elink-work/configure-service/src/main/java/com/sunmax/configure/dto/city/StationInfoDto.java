package com.sunmax.configure.dto.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/2011:49
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "充电站信息实体类")
public class StationInfoDto {

    /**
     * 充电站编码
     */
    @Schema(description = "充电站编码")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 运营商ID
     */
    @Schema(description = "运营商ID")
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 设备所属方ID
     */
    @Schema(description = "设备所属方ID")
    @JSONField(name = "EquipmentOwnerID")
    private String equipmentOwnerId;

    /**
     * 充电站名称
     */
    @Schema(description = "充电站名称")
    @JSONField(name = "StationName")
    private String stationName;

    /**
     * 充电站国家代码
     */
    @Schema(description = "充电站国家代码")
    @JSONField(name = "CountryCode")
    private String countryCode;

    /**
     * 充电站省市辖区编码
     */
    @Schema(description = "充电站省市辖区编码")
    @JSONField(name = "AreaCode")
    private String areaCode;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    @JSONField(name = "Address")
    private String address;

    /**
     * 站点电话
     */
    @Schema(description = "站点电话")
    @JSONField(name = "StationTel")
    private String stationTel;

    /**
     * 服务电话
     */
    @Schema(description = "服务电话")
    @JSONField(name = "ServiceTel")
    private String serviceTel;

    /**
     * 站点类型
     * 1：公共
     * 50：个人
     * 100：公交（专用）
     * 101：环卫（专用）
     * 102：物流（专用）
     * 103：出租车（专用）
     * 255：其他
     */
    @Schema(description = "站点类型")
    @JSONField(name = "StationType")
    private Integer stationType;

    /**
     * 站点状态
     * 0： 未知
     * 1： 建设中
     * 5： 关闭下线
     * 6： 维护中
     * 50：正常使用
     */
    @Schema(description = "站点状态")
    @JSONField(name = "StationStatus")
    private Integer stationStatus;

    /**
     * 车位数量
     */
    @Schema(description = "车位数量")
    @JSONField(name = "ParkNums")
    private Integer parkNums;

    /**
     * 经度
     */
    @Schema(description = "经度")
    @JSONField(name = "StationLng")
    private Double stationLng;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    @JSONField(name = "StationLat")
    private Double stationLat;

    /**
     * 站点引导
     */
    @Schema(description = "站点引导")
    @JSONField(name = "SiteGuide")
    private String siteGuide;

    /**
     * 建设场所
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
    @Schema(description = "建设场所")
    @JSONField(name = "Construction")
    private Integer construction;

    /**
     * 站点照片
     */
    @Schema(description = "站点照片")
    @JSONField(name = "Pictures")
    private String pictures;

    /**
     * 使用车型描述
     */
    @Schema(description = "使用车型描述")
    @JSONField(name = "MatchCars")
    private String matchCars;

    /**
     * 车位楼层及数量描述
     */
    @Schema(description = "车位楼层及数量描述")
    @JSONField(name = "ParkInfo")
    private String parkInfo;

    /**
     * 营业时间
     */
    @Schema(description = "营业时间")
    @JSONField(name = "BusineHours")
    private String busineHours;

    /**
     * 充电电费率
     */
    @Schema(description = "充电电费率")
    @JSONField(name = "ElectricityFee")
    private String electricityFee;

    /**
     * 服务费率
     */
    @Schema(description = "服务费率")
    @JSONField(name = "ServiceFee")
    private String serviceFee;

    /**
     * 停车费率描述
     */
    @Schema(description = "停车费率描述")
    @JSONField(name = "ParkFee")
    private String parkFee;

    /**
     * 支付方式:刷卡、线上、现金
     * 其中电子钱包类卡为刷卡，
     * 身份鉴权卡、微信/支付宝、APP 为线上
     */
    @Schema(description = "支付方式")
    @JSONField(name = "Payment")
    private String payment;

    /**
     * 是否支持预约
     * 0 为不支持预约 、 1 为支持预约 。不填默认为 0
     */
    @Schema(description = "是否支持预约")
    @JSONField(name = "SupportOrder")
    private Integer supportOrder;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @JSONField(name = "Remark")
    private String remark;

    /**
     * 充电设备信息列表
     */
    @Schema(description = "充电设备信息列表")
    @JSONField(name = "EquipmentInfos")
    private List<EquipmentInfoDto> equipmentInfos;
}
