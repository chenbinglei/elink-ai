package com.sunmax.together.vo.operation.orderRecord;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OrderRecordListQueryVo", description = "订单记录列表查询参数")
public class OrderRecordQueryVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

    /**
     * 多个订单类型 0-充电订单 1-放电订单 2-离线放电订单
     */
    @ApiModelProperty(value = "多个订单类型 0-充电订单 1-放电订单 2-离线放电订单", required = true)
    private String orderTypes;

    /**
     * 当前登录用户所属租户id
     */
    @ApiModelProperty(value = "当前登录用户所属租户id", required = true)
    private String userTenantId;

    /**
     * 运营商id
     */
    @ApiModelProperty(value = "运营商id")
    private String operateUnitId;

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id")
    private String siteId;

    /**
     * 关键字类型 1-订单号 2-用户手机号 3-vin码 4-电卡ID 5-桩编码
     */
    @ApiModelProperty(value = "关键字类型 1-订单号 2-用户手机号 3-vin码 4-电卡ID 5-桩编码")
    private Integer keywordType;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;

    /**
     * 订单状态 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中
     */
    @ApiModelProperty(value = "订单状态 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中")
    private Integer orderStatus;

    /**
     * 充电开始-开始时间
     */
    @ApiModelProperty(value = "充电开始-开始时间")
    private String startAlsoStartDate;

    /**
     * 充电开始-结束时间
     */
    @ApiModelProperty(value = "充电开始-结束时间")
    private String startAlsoEndDate;

    /**
     * 创建时间-开始时间
     */
    @ApiModelProperty(value = "创建时间-开始时间")
    private String createAlsoStartDate;

    /**
     * 创建时间-结束时间
     */
    @ApiModelProperty(value = "创建时间-结束时间")
    private String createAlsoEndDate;

    /**
     * 充电结束-开始时间
     */
    @ApiModelProperty(value = "充电结束-开始时间")
    private String endAlsoStartDate;

    /**
     * 充电结束-结束时间
     */
    @ApiModelProperty(value = "充电结束-结束时间")
    private String endAlsoEndDate;

    /**
     * 启动方式 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制、7-离线卡启动
     */
    @ApiModelProperty(value = "启动方式 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制、7-离线卡启动")
    private Integer runMode;

    /**
     * 补单状态 0-挂单 1-自动恢复 2-人工恢复 3-正常
     */
    @ApiModelProperty(value = "补单状态 0-挂单 1-自动恢复 2-人工恢复 3-正常")
    private Integer repairStatus;

    /**
     * 电桩类型 5-交流 6-直流 7-V2G
     */
    @ApiModelProperty("电桩类型 5-交流 6-直流 7-V2G")
    private Integer pileType;

    /**
     * 平台标识
     */
    @ApiModelProperty(value = "平台标识")
    private String platformLogo;

    /**
     * 充电桩编号
     */
    @ApiModelProperty("充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @ApiModelProperty("充电枪编号")
    private Integer gunCode;

    /**
     * 电量最小值
     */
    @ApiModelProperty("电量最小值")
    private Double minQt;

    /**
     * 电量最大值
     */
    @ApiModelProperty("电量最大值")
    private Double maxQt;

    /**
     * 时长最小值(单位：小时)
     */
    @ApiModelProperty("时长最小值(单位：小时)")
    private Double minDuration;

    /**
     * 时长最大值(单位：小时)
     */
    @ApiModelProperty("时长最大值(单位：小时)")
    private Double maxDuration;

    /**
     * 异常类型 0-无异常 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0
     */
    @ApiModelProperty("异常类型 0-无异常 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0")
    private Integer abnormalType;
}
