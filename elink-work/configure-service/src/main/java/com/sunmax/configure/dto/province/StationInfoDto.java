package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/2011:49
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "StationInfoDto", description = "充电站信息实体类")
public class StationInfoDto {

    /**
     * 充电站编码
     */
    @ApiModelProperty(value = "充电站编码")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 浙江省充电站唯一码
     */
    @ApiModelProperty(value = "浙江省充电站唯一码")
    @JSONField(name = "StationUniqueID")
    private String stationUniqueId;

    /**
     * 运营商ID
     */
    @ApiModelProperty(value = "运营商ID")
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 充电服务运营商ID
     *
     * 充电站的设备所属方ID（原组织机构代码），为个人时填写999999999
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
     * 充电站所在县以下行政区划代码
     *
     * 填写内容为 12 位行政区划代码，1-6 位为县以上行政区划代码，7-12 位为县以下
     * 区划代码;具体参考国家统计局发布的全国统计用区划代码和城乡划分代码
     * https://www.stats.gov.cn/sj/tjbz/qhdm/
     */
    @ApiModelProperty(value = "充电站所在县以下行政区划代码")
    @JSONField(name = "AreaCodeCountryside")
    private String areaCodeCountryside;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    @JSONField(name = "Address")
    private String address;

    /**
     * 站点电话
     */
    @ApiModelProperty(value = "站点电话")
    @JSONField(name = "StationTel")
    private String stationTel;

    /**
     * 服务电话
     */
    @ApiModelProperty(value = "服务电话")
    @JSONField(name = "ServiceTel")
    private String serviceTel;

    /**
     * 站点分类
     *
     * 1：充电站
     * 3：充换电一体站
     */
    @ApiModelProperty(value = "站点分类")
    @JSONField(name = "StationClassification")
    private Integer stationClassification;

    /**
     * 站点类型
     * 1： 公共
     * 50： 个人
     * 100： 公交（专用）
     * 101： 环卫（专用）
     * 102： 物流（专用）
     * 103： 出租车（专用）
     * 104： 分时租赁（专用）
     * 105： 小区共享（专用）
     * 106： 单位 （专用）
     * 255： 其他
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
     * 1： 居民区
     * 2： 公共机构
     * 3： 企事业单位
     * 4： 写字楼
     * 5： 工业园区
     * 6： 交通枢纽
     * 7： 大型文体设施
     * 8： 城市绿地
     * 9：大型建筑配建停车场
     * 10： 路边停车位
     * 11： 城际高速服务区
     * 12： 风景区
     * 13： 公交场站
     * 14： 加油加气站
     * 15： 出租车
     * 255： 其他
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
     * 停车场产权方
     */
    @ApiModelProperty(value = "停车场产权方")
    @JSONField(name = "ParkOwner")
    private String parkOwner;

    /**
     * 停车场管理方
     */
    @ApiModelProperty(value = "停车场管理方")
    @JSONField(name = "ParkManager")
    private String parkManager;

    /**
     * 7*24 小时营业
     * 0： 否
     * 1： 是
     */
    @ApiModelProperty(value = "7*24 小时营业")
    @JSONField(name = "Is24Hour")
    private Integer is24Hour;

    /**
     * 营业时间
     */
    @ApiModelProperty(value = "营业时间")
    @JSONField(name = "BusineHours")
    private String busineHours;

    /**
     * 峰谷分时
     * 0：否
     * 1：是
     */
    @ApiModelProperty(value = "峰谷分时")
    @JSONField(name = "PeriodFee")
    private Integer periodFee;

    /**
     * 充电计费信息
     */
    @ApiModelProperty(value = "充电计费信息")
    @JSONField(name = "PolicyInfos")
    private List<PolicyInfo> policyInfos;

    /**
     * 停车费类型
     * 0：免费
     * 1：不免费
     * 2：限时免费停车
     * 3：充电限时减免
     * 255：参考场地实际收费标准
     */
    @ApiModelProperty(value = "停车费类型")
    @JSONField(name = "ParkType")
    private Integer parkType;

    /**
     * 停车费率描述
     */
    @ApiModelProperty(value = "停车费率描述")
    @JSONField(name = "ParkFee")
    private String parkFee;

    /**
     * 停车减免规则
     *
     * 站点支持的停车减免方式:
     * 1：推送车牌
     * 2：打印小票
     * 3：订单二维码
     */
    @ApiModelProperty(value = "停车减免规则")
    @JSONField(name = "ParkReductionMode")
    private List<Integer> parkReductionMode;

    /**
     * 是否收取占位费
     *
     * 0：不收取
     * 1：全天收取
     * 2：部分时段收取
     */
    @ApiModelProperty(value = "是否收取占位费")
    @JSONField(name = "IdleFeeFlag")
    private Integer idleFeeFlag;

    /**
     * 占位费信息
     */
    @ApiModelProperty(value = "占位费信息")
    @JSONField(name = "IdleFeeInfos")
    private List<IdleFeeInfo> idleFeeInfos;

    /**
     * 电费类型
     *
     * 1：商业用电
     * 2：普通工业用电
     * 3：大工业用电
     * 4：其他用电
     */
    @ApiModelProperty(value = "电费类型")
    @JSONField(name = "ElectricityType")
    private Integer electricityType;

    /**
     * 报装类型
     *
     * 是否独立报装：
     * 0：否
     * 1：是
     */
    @ApiModelProperty(value = "报装类型")
    @JSONField(name = "BusinessExpandType")
    private Integer businessExpandType;

    /**
     * 用电户号
     */
    @ApiModelProperty(value = "用电户号")
    @JSONField(name = "ElectricityAccountID")
    private String electricityAccountId;

    /**
     * 站点额定电压
     */
    @ApiModelProperty(value = "站点额定电压")
    @JSONField(name = "RatedVoltage")
    private Double ratedVoltage;

    /**
     * 报装电源容量
     */
    @ApiModelProperty(value = "报装电源容量")
    @JSONField(name = "Capacity")
    private Double capacity;

    /**
     * 站点额定总功率
     */
    @ApiModelProperty(value = "站点额定总功率")
    @JSONField(name = "RatedPower")
    private Double ratedPower;

    /**
     * 距离变电站/所最近距离km
     */
    @ApiModelProperty(value = "距离变电站/所最近距离")
    @JSONField(name = "SubstationDistance")
    private Double substationDistance;

    /**
     * 正式投运时间
     */
    @ApiModelProperty(value = "正式投运时间")
    @JSONField(name = "OfficialRunTime")
    private String officialRunTime;

    /**
     * 充电站方位
     *
     * 1：地面-停车场
     * 2：地面-路侧
     * 3：地下停车场
     * 4：立体式停车楼
     */
    @ApiModelProperty(value = "充电站方位")
    @JSONField(name = "StationOrientation")
    private Integer stationOrientation;

    /**
     * 充电站建设面积
     */
    @ApiModelProperty(value = "充电站建设面积")
    @JSONField(name = "StationArea")
    private Double stationArea;

    /**
     * 充电站人工值守
     *
     * 0：无
     * 1：有
     */
    @ApiModelProperty(value = "充电站人工值守")
    @JSONField(name = "HavePerson")
    private Integer havePerson;

    /**
     * 视频监控配套情况
     *
     * 0：无
     * 1：有
     */
    @ApiModelProperty(value = "视频监控配套情况")
    @JSONField(name = "VideoMonitor")
    private Integer videoMonitor;

    /**
     * 周边配套设施
     *
     * 1：卫生间
     * 2：便利店
     * 3：餐厅
     * 4：休息室
     * 5：雨棚
     */
    @ApiModelProperty(value = "周边配套设施")
    @JSONField(name = "SupportingFacilities")
    private List<Integer> supportingFacilities;

    /**
     * 是否有小票机
     *
     * 0：无
     * 1：有
     */
    @ApiModelProperty(value = "是否有小票机")
    @JSONField(name = "PrinterFlag")
    private Integer printerFlag;

    /**
     * 是否有道闸
     *
     * 0：无
     * 1：有
     */
    @ApiModelProperty(value = "是否有道闸")
    @JSONField(name = "BarrierFlag")
    private Integer barrierFlag;

    /**
     * 是否有地锁
     *
     * 0：无
     * 1：有
     */
    @ApiModelProperty(value = "是否有地锁")
    @JSONField(name = "ParkingLockFlag")
    private Integer parkingLockFlag;

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
     * 站点固定资产投资项目代码
     */
    @ApiModelProperty(value = "站点固定资产投资项目代码")
    @JSONField(name = "InvestmentProjectCode")
    private String investmentProjectCode;

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
    private List<EquipmentInfoDto> equipmentInfos = Lists.newArrayList();

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
