package com.sunmax.together.vo.operation.orderRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单记录列表查询参数")
public class OrderRecordQueryVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

    /**
     * 多个订单类型 0-充电订单 1-放电订单 2-离线放电订单
     */
    @Schema(description = "多个订单类型 0-充电订单 1-放电订单 2-离线放电订单")
    private String orderTypes;

    /**
     * 当前登录用户所属租户id
     */
    @Schema(description = "当前登录用户所属租户id")
    private String userTenantId;

    /**
     * 运营商id
     */
    @Schema(description = "运营商id")
    private String operateUnitId;

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 关键字类型 1-订单号 2-用户手机号 3-vin码 4-电卡ID 5-桩编码
     */
    @Schema(description = "关键字类型 1-订单号 2-用户手机号 3-vin码 4-电卡ID 5-桩编码")
    private Integer keywordType;

    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 订单状态 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中
     */
    @Schema(description = "订单状态 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中")
    private Integer orderStatus;

    /**
     * 充电开始-开始时间
     */
    @Schema(description = "充电开始-开始时间")
    private String startAlsoStartDate;

    /**
     * 充电开始-结束时间
     */
    @Schema(description = "充电开始-结束时间")
    private String startAlsoEndDate;

    /**
     * 创建时间-开始时间
     */
    @Schema(description = "创建时间-开始时间")
    private String createAlsoStartDate;

    /**
     * 创建时间-结束时间
     */
    @Schema(description = "创建时间-结束时间")
    private String createAlsoEndDate;

    /**
     * 充电结束-开始时间
     */
    @Schema(description = "充电结束-开始时间")
    private String endAlsoStartDate;

    /**
     * 充电结束-结束时间
     */
    @Schema(description = "充电结束-结束时间")
    private String endAlsoEndDate;

    /**
     * 启动方式 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制、7-离线卡启动
     */
    @Schema(description = "启动方式 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制、7-离线卡启动")
    private Integer runMode;

    /**
     * 补单状态 0-挂单 1-自动恢复 2-人工恢复 3-正常
     */
    @Schema(description = "补单状态 0-挂单 1-自动恢复 2-人工恢复 3-正常")
    private Integer repairStatus;

    /**
     * 电桩类型 5-交流 6-直流 7-V2G
     */
    @Schema(description = "电桩类型 5-交流 6-直流 7-V2G")
    private Integer pileType;

    /**
     * 平台标识
     */
    @Schema(description = "平台标识")
    private String platformLogo;

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @Schema(description = "充电枪编号")
    private Integer gunCode;

    /**
     * 电量最小值
     */
    @Schema(description = "电量最小值")
    private Double minQt;

    /**
     * 电量最大值
     */
    @Schema(description = "电量最大值")
    private Double maxQt;

    /**
     * 时长最小值(单位：小时)
     */
    @Schema(description = "时长最小值(单位：小时)")
    private Double minDuration;

    /**
     * 时长最大值(单位：小时)
     */
    @Schema(description = "时长最大值(单位：小时)")
    private Double maxDuration;

    /**
     * 异常类型 0-无异常 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0
     */
    @Schema(description = "异常类型 0-无异常 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0")
    private Integer abnormalType;
}
