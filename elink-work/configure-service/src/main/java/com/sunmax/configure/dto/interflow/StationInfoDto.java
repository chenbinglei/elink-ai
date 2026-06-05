package com.sunmax.configure.dto.interflow;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2024/8/16 13:47
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "StationInfoDto", description = "充电站信息实体类")
public class StationInfoDto {

    /**
     * 充电站ID
     */
    @ApiModelProperty(value = "充电站ID")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 运营商ID
     */
    @ApiModelProperty(value = "运营商ID")
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 设备所属方 ID
     *
     * 设备所属运营平台组织机构代码
     */
    @ApiModelProperty(value = "充电服务运营商ID")
    @JSONField(name = "EquipmentOwnerID")
    private String equipmentOwnerId;

    /**
     * 充电站名称
     */
    @ApiModelProperty(value = "充电站名称")
    @JSONField(name = "StationName")
    private String stationName;

    /**
     * 充电站国家代码
     */
    @ApiModelProperty(value = "充电站国家代码")
    @JSONField(name = "CountryCode")
    private String countryCode;

    /**
     * 充电站省市辖区编码
     */
    @ApiModelProperty(value = "充电站省市辖区编码")
    @JSONField(name = "AreaCode")
    private String areaCode;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    @JSONField(name = "Address")
    private String address;

    /**
     * 站点电话(联系人电话)
     *
     * 能够联系场站工作人员进行协助的联系电话
     */
    @ApiModelProperty(value = "站点电话")
    @JSONField(name = "StationTel")
    private String stationTel;

    /**
     * 服务电话
     *
     * 平台服务电话，例如 400 的电话
     */
    @ApiModelProperty(value = "服务电话")
    @JSONField(name = "ServiceTel")
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
    @ApiModelProperty(value = "站点类型")
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
    @ApiModelProperty(value = "站点状态")
    @JSONField(name = "StationStatus")
    private Integer stationStatus;

    /**
     * 车位数量
     *
     * 可停放进行充电的车位总数，默认：0 未知
     */
    @ApiModelProperty(value = "车位数量")
    @JSONField(name = "ParkNums")
    private Integer parkNums;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    @JSONField(name = "StationLng")
    private Double stationLng;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    @JSONField(name = "StationLat")
    private Double stationLat;

    /**
     * 站点引导
     */
    @ApiModelProperty(value = "站点引导")
    @JSONField(name = "SiteGuide")
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
    @ApiModelProperty(value = "建设场所")
    @JSONField(name = "Construction")
    private Integer construction;

    /**
     * 站点照片
     */
    @ApiModelProperty(value = "站点照片")
    @JSONField(name = "Pictures")
    private List<String> pictures;

    /**
     * 使用车型描述
     */
    @ApiModelProperty(value = "使用车型描述")
    @JSONField(name = "MatchCars")
    private String matchCars;

    /**
     * 车位楼层及数量描述
     */
    @ApiModelProperty(value = "车位楼层及数量描述")
    @JSONField(name = "ParkInfo")
    private String parkInfo;

    /**
     * 营业时间
     */
    @ApiModelProperty(value = "营业时间")
    @JSONField(name = "BusineHours")
    private String busineHours;

    /**
     * 充电电费率
     *
     * 充电费描述
     */
    @ApiModelProperty(value = "充电电费率")
    @JSONField(name = "ElectricityFee")
    private String electricityFee;

    /**
     * 服务费率
     *
     * 服务费率描述
     */
    @ApiModelProperty(value = "服务费率")
    @JSONField(name = "ServiceFee")
    private String serviceFee;

    /**
     * 停车费
     *
     * 停车费率描述
     */
    @ApiModelProperty(value = "停车费")
    @JSONField(name = "ParkFee")
    private String parkFee;

    /**
     * 支付方式:刷卡、线上、现金
     * 其中电子钱包类卡为刷卡，
     * 身份鉴权卡、微信/支付宝、APP 为线上
     */
    @ApiModelProperty(value = "支付方式")
    @JSONField(name = "Payment")
    private String payment;

    /**
     * 是否支持预约
     * 0 为不支持预约 、 1 为支持预约 。不填默认为 0
     */
    @ApiModelProperty(value = "是否支持预约")
    @JSONField(name = "SupportOrder")
    private Integer supportOrder;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @JSONField(name = "Remark")
    private String remark;

    /**
     * 充电设备信息列表
     */
    @ApiModelProperty(value = "充电设备信息列表")
    @JSONField(name = "EquipmentInfos")
    private List<EquipmentInfoDto> equipmentInfos;

    /**
     * 站点充电计费信息
     */
    @Data
    public static class PolicyInfo {

        /**
         * 开始时间
         */
        @ApiModelProperty(value = "开始时间")
        @JSONField(name = "StartTime")
        private String startTime;

        /**
         * 结束时间
         */
        @ApiModelProperty(value = "结束时间")
        @JSONField(name = "EndTime")
        private String endTime;

        /**
         * 充电类型
         *
         * 1：慢充（交流）
         * 2：快充（直流）
         * 3：超充/极充（运营商
         * 自定义）
         * 5：无线充电
         * 6：移动充电
         * 9：其他
         */
        @ApiModelProperty(value = "充电类型")
        @JSONField(name = "ChargingType")
        private Integer chargingType;

        /**
         * 电费价格
         *
         * 时段基础电费价格，单位：元/度，保留小数点后 4 位
         */
        @ApiModelProperty(value = "电费价格")
        @JSONField(name = "ElecFee")
        private Double elecFee;

        /**
         * 服务费价格
         *
         * 时段基础服务费价格，单位：元/度，保留小数点后 4 位
         */
        @ApiModelProperty(value = "服务费价格")
        @JSONField(name = "ServiceFee")
        private Double serviceFee;

        /**
         * 规则生效日期
         *
         * 本计费规则生效日期，格式“yyyy-MM-dd“
         */
        @ApiModelProperty(value = "规则生效日期")
        @JSONField(name = "StartDate")
        private String startDate;

        /**
         * 规则失效日期
         *
         * 本计费规则失效日期，格式“yyyy-MM-dd“
         */
        @ApiModelProperty(value = "规则失效日期")
        @JSONField(name = "EndDate")
        private String endDate;
    }

    /**
     * 站点占位费信息
     */
    @Data
    public static class IdleFeeInfo {

        /**
         * 开始时间
         */
        @ApiModelProperty(value = "开始时间")
        @JSONField(name = "StartTime")
        private String startTime;

        /**
         * 结束时间
         */
        @ApiModelProperty(value = "结束时间")
        @JSONField(name = "EndTime")
        private String endTime;

        /**
         * 占位费价格
         */
        @ApiModelProperty(value = "占位费价格")
        @JSONField(name = "IdleFee")
        private Double idleFee;

        /**
         * 站点全满时占位费价格
         *
         * 站点车位全满时占位费价格，单位：元/分钟
         */
        @ApiModelProperty(value = "站点全满时占位费价格")
        @JSONField(name = "FullIdleFee")
        private Double fullIdleFee;

        /**
         * 最大占位费
         */
        @ApiModelProperty(value = "最大占位费")
        @JSONField(name = "MaxFee")
        private Double maxFee;

        /**
         * 免费时长
         * 单位：分钟
         */
        @ApiModelProperty(value = "免费时长")
        @JSONField(name = "FreeTime")
        private Integer freeTime;

        /**
         * 规则生效日期
         *
         * 本计费规则生效日期，格式“yyyy-MM-dd“
         */
        @ApiModelProperty(value = "规则生效日期")
        @JSONField(name = "StartDate")
        private String startDate;

        /**
         * 规则失效日期
         *
         * 本计费规则失效日期，格式“yyyy-MM-dd“
         */
        @ApiModelProperty(value = "规则失效日期")
        @JSONField(name = "EndDate")
        private String endDate;
    }
}
