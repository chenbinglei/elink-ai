package com.sunmax.together.entity.order;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * 订单记录实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_order_record")
public class OrderRecordEntity extends BaseTimeEntity {

    /**
     * 所属站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) NOT NULL comment '所属站点id'")
    private String siteId;

    /**
     * 订单号
     */
    @Column(name = "order_num",columnDefinition = "varchar(64) NOT NULL comment '订单号'")
    private String orderNum;

    /**
     * 是否有序 1-是 2-否
     */
    @Column(name = "is_orderly",columnDefinition = "tinyint(1) comment '是否有序 1-是 2-否'")
    private Integer isOrderly;

    /**
     * 预付金额
     */
    @Column(name = "prepay_money",columnDefinition = "decimal(9,2) comment '预付金额'")
    private BigDecimal prepayMoney;

    /**
     * 运行模式 0-充电订单 1,2-放电订单
     */
    @Column(name = "run_mode",columnDefinition = "tinyint(1) NOT NULL comment '运行模式 0-充电订单 1,2-放电订单'")
    private Integer runMode;

    /**
     * 订单状态 0-未进行 1-充放电中 2-充放电完成 3-启动失败 4-订单挂起 5-订单取消 6-预约中
     */
    @Column(name = "order_status",columnDefinition = "tinyint(1) NOT NULL comment '订单状态 0-未进行 1-充放电中 2-充放电完成 3-启动失败 4-订单挂起 5-订单取消 6-预约中'")
    private Integer orderStatus;

    /**
     * 充放电方式 0-立即充电 1-定时充电 2-自动充电
     */
    @Column(name = "type",columnDefinition = "tinyint(1) comment '充放电方式 0-立即充电 1-定时充电 2-自动充电'")
    private Integer type;

    /**
     * 定时充放电时间
     */
    @Column(name = "clocking_time",columnDefinition = "varchar(32) comment '定时充放电时间'")
    private String clockingTime;

    /**
     * 账号类型 1-充/放电卡ID 2-VIN码 3-手机号
     */
    @Column(name = "account_type",columnDefinition = "tinyint(1) comment '账号类型 1-充/放电卡 2-VIN码 3-手机号'")
    private Integer accountType;

    /**
     * 账号数据
     */
    @Column(name = "account_data",columnDefinition = "varchar(32) comment '账号数据'")
    private String accountData;

    /**
     * 卡面号
     */
    @Column(name = "card_number", columnDefinition = "varchar(32) comment '卡面号'")
    private String cardNumber;

    /**
     * 充电桩编号
     */
    @Column(name = "pile_code",columnDefinition = "varchar(64) NOT NULL comment '充电桩编号'")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @Column(name = "gun_code",columnDefinition = "int(10) NOT NULL comment '充电枪编号'")
    private Integer gunCode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-离线卡启动
     */
    @Column(name = "starter",columnDefinition = "tinyint(1) comment '发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-离线卡启动'")
    private Integer starter;

    /**
     * 来源平台标识
     */
    @Column(name = "platform_logo",columnDefinition = "varchar(32) comment '来源平台标识'")
    private String platformLogo;

    /**
     * 策略类型  0 满充/放空；1 定 SOC; 2 定金额；3 定电量
     */
    @Column(name = "strategy_type", columnDefinition = "tinyint(1) comment '策略类型 0 满充/放空；1 定 SOC; 2 定金额；3 定电量'")
    private Integer strategyType;

    /**
     * 策略启动时间
     */
    @Column(name = "strategy_time", columnDefinition = "varchar(32) comment '策略启动时间'")
    private String strategyTime;

    /**
     * 定量策略值
     *      *  策略类型 1：0～100      精度 1%；
     *      *  策略类型 2：0～100000   精度 0.001 元
     *      *  策略类型 3：0～100000   精度 0.001kW·h
     *
     */
    @Column(name = "strategy_cfg", columnDefinition = "double(10,3) comment '定量策略值'")
    private Double strategyCfg;

    /**
     * 开始充/放电时间
     */
    @Column(name = "start_time", columnDefinition = "varchar(32) comment '开始充/放电时间'")
    private String startTime;

    /**
     * 结束充/放电时间
     */
    @Column(name = "end_time", columnDefinition = "varchar(32) comment '结束充/放电时间'")
    private String endTime;

    /**
     * 开始直流电表读数
     */
    @Column(name = "start_dir_meter", columnDefinition = "double(9,4) comment '开始直流电表读数'")
    private Double startDirMeter;

    /**
     * 结束直流电表读数
     */
    @Column(name = "end_dir_Meter", columnDefinition = "double(9,4) comment '结束直流电表读数'")
    private Double endDirMeter;

    /**
     * 本次充/放电总电量
     */
    @Column(name = "total_qt", columnDefinition = "double(9,4) comment '本次充/放电总电量'")
    @Builder.Default
    private Double totalQt = 0.0;

    /**
     * 本次充/放电总费用
     */
    @Column(name = "total_cost", columnDefinition = "decimal(9,4) comment '本次充/放电总费用'")
    @Builder.Default
    private BigDecimal totalCost = new BigDecimal("0.0");

    /**
     * 本次充/放电总电费
     */
    @Column(name = "total_elect", columnDefinition = "decimal(9,4) comment '本次充/放电总电费'")
    @Builder.Default
    private BigDecimal totalElect = new BigDecimal("0.0");

    /**
     * 本次充/放电总服务费
     */
    @Column(name = "total_fee", columnDefinition = "decimal(9,4) comment '本次充/放电总服务费'")
    @Builder.Default
    private BigDecimal totalFee = new BigDecimal("0.0");

    /**
     * 本次充/放电尖时总电量
     */
    @Column(name = "j_qt", columnDefinition = "double(9,4) comment '本次充/放电尖时总电量'")
    @Builder.Default
    private Double jQt = 0.0;

    /**
     * 本次充/放电尖时总电费
     */
    @Column(name = "j_elect", columnDefinition = "decimal(9,4) comment '本次充/放电尖时总电费'")
    @Builder.Default
    private BigDecimal jElect = new BigDecimal("0.0");

    /**
     * 本次充/放电尖时总服务费
     */
    @Column(name = "j_fee", columnDefinition = "decimal(9,4) comment '本次充/放电尖时总服务费'")
    @Builder.Default
    private BigDecimal jFee = new BigDecimal("0.0");

    /**
     * 本次充/放电峰时总电量
     */
    @Column(name = "f_qt", columnDefinition = "double(9,4) comment '本次充/放电峰时总电量'")
    @Builder.Default
    private Double fQt = 0.0;

    /**
     * 本次充/放电峰时总电费
     */
    @Column(name = "f_elect", columnDefinition = "decimal(9,4) comment '本次充/放电峰时总电费'")
    @Builder.Default
    private BigDecimal fElect = new BigDecimal("0.0");

    /**
     * 本次充/放电峰时总服务费
     */
    @Column(name = "f_fee", columnDefinition = "decimal(9,4) comment '本次充/放电峰时总服务费'")
    @Builder.Default
    private BigDecimal fFee = new BigDecimal("0.0");

    /**
     * 本次充/放电平时总电量
     */
    @Column(name = "p_qt", columnDefinition = "double(9,4) comment '本次充/放电平时总电量'")
    @Builder.Default
    private Double pQt = 0.0;

    /**
     * 本次充/放电平时总电费
     */
    @Column(name = "p_elect", columnDefinition = "decimal(9,4) comment '本次充/放电平时总电费'")
    @Builder.Default
    private BigDecimal pElect = new BigDecimal("0.0");

    /**
     * 本次充/放电平时总服务费
     */
    @Column(name = "p_fee", columnDefinition = "decimal(9,4) comment '本次充/放电平时总服务费'")
    @Builder.Default
    private BigDecimal pFee = new BigDecimal("0.0");

    /**
     * 本次充/放电谷时总电量
     */
    @Column(name = "g_qt", columnDefinition = "double(9,4) comment '本次充/放电谷时总电量'")
    @Builder.Default
    private Double gQt = 0.0;

    /**
     * 本次充/放电谷时总电费
     */
    @Column(name = "g_elect", columnDefinition = "decimal(9,4) comment '本次充/放电谷时总电费'")
    @Builder.Default
    private BigDecimal gElect = new BigDecimal("0.0");

    /**
     * 本次充/放电谷时总服务费
     */
    @Column(name = "g_fee", columnDefinition = "decimal(9,4) comment '本次充/放电谷时总服务费'")
    @Builder.Default
    private BigDecimal gFee = new BigDecimal("0.0");

    /**
     * 开始 SOC
     */
    @Column(name = "start_soc", columnDefinition = "int(4) comment '开始 SOC'")
    private Integer startSoc;

    /**
     * 结束 SOC
     */
    @Column(name = "end_soc", columnDefinition = "int(4) comment '开始 SOC'")
    private Integer endSoc;

    /**
     * 停止码
     */
    @Column(name = "stop_reason", columnDefinition = "varchar(64) comment '停止码'")
    private String stopReason;

    /**
     * 停止详细原因
     */
    @Column(name = "stop_detail_reason", columnDefinition = "varchar(255) comment '停止详细原因'")
    private String stopDetailReason;

    /**
     * 车量 vin 码
     */
    @Column(name = "bus_vin", columnDefinition = "varchar(64) comment '车量vin码'")
    private String busVin;

    /**
     * 费率模型ID
     */
    @Column(name = "rate_template_id", columnDefinition = "varchar(32) comment '费率模型ID'")
    private String rateTemplateId;

    /**
     * 有效时段总数 取值 0～48
     */
    @Column(name = "time_frame_num", columnDefinition = "int(4) comment '有效时段总数 取值 0～48'")
    private Integer timeFrameNum;

    /**
     * 关联时段表id
     */
    @Column(name = "time_frame_id", columnDefinition = "varchar(32) comment '关联时段表id'")
    private String timeFrameId;

    /**
     * 异常码 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0(多选)
     */
    @Column(name = "abnormal_code", columnDefinition = "varchar(255) comment '异常码 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0(多选)'")
    private String abnormalCode;

}
