package com.sunmax.common.model.general;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.protocol.mqtt.UpdateInfoDto;
import com.sunmax.common.dto.protocol.mqtt.web.CtrlBoardInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 充电桩通用实时数据
 */
@Data
@Schema(description = "充电桩实时数据")
public class PileRealModel {

    @Schema(description = "电桩编号")
    private String pileCode;

    @Schema(description = "电桩工作状态 -1-未知 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus;

    @Schema(description = "电桩原始状态 0-待机 1-工作 2-维护 3-故障 88-离线")
    private Integer originalStatus;

    @Schema(description = "复位次数")
    private Integer resetTimes;

    @Schema(description = "特证码")
    private Long featureCode;

//    @Schema(description = "上次心跳时间")
//    private String lastBeatTime;
//
//    @Schema(description = "上次发送报文的时间")
//    private String lastSendTime;

    @Schema(description = "当前总功率")
    private Double totalPower;

    @Schema(description = "当前充电功率")
    private Double recChargePower;

    @Schema(description = "当前放电功率")
    private Double disChargePower;

    @Schema(description = "电桩内部温度")
    private Double innerTemp;

    @Schema(description = "电力模块最高温度")
    private Double powerModMaxTemp;

    @Schema(description = "电力模块最高温度序号")
    private Integer maxTempMod;

    @Schema(description = "终端编号")
    private String terminalCode;

    @Schema(description = "消息类型 1-外网 2-内网")
    private Integer messageType;

    @Schema(description = "桩故障码列表")
    private Set<Integer> faultCodes = Sets.newHashSet();

    @Schema(description = "电力模块故障码与模块地址映射 (故障码->模块地址 Set)")
    private Map<Integer, Set<Integer>> moduleFaultAddrMap = Maps.newConcurrentMap();

    @Schema(description = "充电枪实时数据")
    private Map<String, GunRealModel> gunRealModelMap = Maps.newConcurrentMap();

    @Schema(description = "控制板信息列表 固件类型->控制板信息")
    private Map<Integer, CtrlBoardInfo> ctrlBoardInfoMap = new HashMap<>();

    @Schema(description = "固件升级列表 固件类型->固件信息")
    private Map<Integer, UpdateInfoDto> updateInfoMap = Maps.newHashMap();

    @Schema(description = "数据时间")
    private String dateTime;

    @Schema(description = "离线时间")
    private String offlineTime;

    @Data
    @Schema(description = "充电枪实时数据")
    public static class GunRealModel {

        @Schema(description = "枪编号")
        private String gunCode;

        @Schema(description = "枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
        private Integer gunStatus;

        @Schema(description = "充电枪原始状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 8-暂停 88-离线 255-故障")
        private Integer gunOriginalStatus;

        @Schema(description = "地锁状态 0-未知 1-降下 2-升起 3-运动中 4-故障")
        private Integer parkingLockState;

        @Schema(description = "车辆连接状态 0-未连接 1-半连接 2-连接 3-连接故障")
        private Integer vehicleConnState;

        @Schema(description = "枪锁状态 0-未连接 1-半连接 2-连接 3-连接故障")
        private Integer gunLockState;

        @Schema(description = "接触器k1k2状态 0-未连接 1-半连接 2-连接 3-连接故障")
        private Integer k1k2State;

        @Schema(description = "交易流水号")
        private String serialNum;

//        @Schema(description = "平台标识")
//        private String platformLogo;

        @Schema(description = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-皖能平台")
        private Integer starter;

        @Schema(description = "充电方式 0-立即充电 1-定时充电 2-自动充电")
        private Integer type;

        @Schema(description = "开始时间")
        private String startTime;

        @Schema(description = "开始Soc")
        private Integer startSoc;

        @Schema(description = "电池Soc")
        private Integer batterySoc;

        @Schema(description = "运行模式 -1-未知 0-充电模式 1-放电模式")
        private Integer runMode = -1;

        @Schema(description = "枪温度1")
        private Double gunTemp1;

        @Schema(description = "枪温度2")
        private Double gunTemp2;

        @Schema(description = "输出电压 精度1V")
        private Double outVolt;

        @Schema(description = "输出电流 精度1A")
        private Double outCurrent;

        @Schema(description = "输出功率 精度1kW")
        private Double outPower;

        @Schema(description = "需求电压 精度1V")
        private Double reqVolt;

        @Schema(description = "需求电流 精度1A")
        private Double reqCurrent;

        @Schema(description = "需求功率 精度1kW")
        private Double reqPower;

        @Schema(description = "直流电表读数 精度1kW·h")
        private Double dirMeterNum;

        @Schema(description = "交流电表读数 精度1kW·h")
        private Double alterMeterNum;

        @Schema(description = "运行时间")
        private Integer runTime;

        @Schema(description = "剩余时间")
        private Integer remainTime;

        @Schema(description = "总电量")
        private Double totalQt;

        @Schema(description = "总费用")
        private BigDecimal totalCost;

        @Schema(description = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量")
        private Integer strategy;

        @Schema(description = "充放电策略参数")
        private Double strategyCfg;

        @Schema(description = "账户类型 1-充/放电卡 2-VIN码 3-手机号")
        private Integer accountType;

        @Schema(description = "账户数据")
        private String accountData;

        @Schema(description = "预约时间")
        private String clockingTime;

        @Schema(description = "预付金额")
        private BigDecimal prepayMoney;

        @Schema(description = "枪故障码列表")
        private Set<Integer> faultCodes = Sets.newHashSet();

        @Schema(description = "最高动力蓄电池温度")
        private Integer batteryTempMax;

        @Schema(description = "最高动力蓄电池温度检测点编号")
        private Integer batteryTempMaxNo;

        @Schema(description = "最低动力蓄电池温度")
        private Integer batteryTempMin;

        @Schema(description = "最低动力蓄电池温度检测点编号")
        private Integer batteryTempMinNo;

        @Schema(description = "最高单体蓄电池电压所对应组号")
        private Integer batteryVoltageMaxGn;

        @Schema(description = "最高单体蓄电池电压 精度1V")
        private Double batteryVoltageMax;

        @Schema(description = "最低单体蓄电池电压所对应组号")
        private Integer batteryVoltageMinGn;

        @Schema(description = "最低单体蓄电池电压 精度1V")
        private Double batteryVoltageMin;

        @Schema(description = "单体最高允许充电电压 精度1V")
        private Double bcpAllowChargeCellVMax;

        @Schema(description = "最高允许充电电流 精度1A")
        private Double bcpAllowChargeCurrentMax;

        @Schema(description = "动力电池标称总能量 精度1kW.h")
        private Double bcpBatteryNominalCap;

        @Schema(description = "最高允许电压 精度1V")
        private Double bcpAllowChargeVMax;

        @Schema(description = "最高允许充电温度 精度1℃")
        private Integer bcpAllowTempMax;

        @Schema(description = "电池类型 01H:铅酸电池; 02H:氢电池; 03H:磷酸铁锂电池; 04H:锰酸锂电池; 05H:钴酸锂电池; 06H:三元电池; 07H:聚合物锂离子电池; 08H:钛酸锂电池; FFH:其他")
        private Integer batteryType;

    }


}
