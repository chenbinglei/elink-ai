package com.sunmax.common.model.general;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.protocol.mqtt.UpdateInfoDto;
import com.sunmax.common.dto.protocol.mqtt.web.CtrlBoardInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 充电桩通用实时数据
 */
@Data
@ApiModel(value = "PileRealModel", description = "充电桩实时数据")
public class PileRealModel {

    @ApiModelProperty(value = "电桩编号")
    private String pileCode;

    @ApiModelProperty(value = "电桩工作状态 -1-未知 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus;

    @ApiModelProperty(value = "电桩原始状态 0-待机 1-工作 2-维护 3-故障 88-离线")
    private Integer originalStatus;

    @ApiModelProperty(value = "复位次数")
    private Integer resetTimes;

    @ApiModelProperty(value = "特证码")
    private Long featureCode;

//    @ApiModelProperty(value = "上次心跳时间")
//    private String lastBeatTime;
//
//    @ApiModelProperty(value = "上次发送报文的时间")
//    private String lastSendTime;

    @ApiModelProperty(value = "当前总功率")
    private Double totalPower;

    @ApiModelProperty(value = "当前充电功率")
    private Double recChargePower;

    @ApiModelProperty(value = "当前放电功率")
    private Double disChargePower;

    @ApiModelProperty(value = "电桩内部温度")
    private Double innerTemp;

    @ApiModelProperty(value = "电力模块最高温度")
    private Double powerModMaxTemp;

    @ApiModelProperty(value = "电力模块最高温度序号")
    private Integer maxTempMod;

    @ApiModelProperty(value = "终端编号")
    private String terminalCode;

    @ApiModelProperty(value = "消息类型 1-外网 2-内网")
    private Integer messageType;

    @ApiModelProperty(value = "桩故障码列表")
    private Set<Integer> faultCodes = Sets.newHashSet();

    @ApiModelProperty(value = "电力模块故障码与模块地址映射 (故障码->模块地址 Set)")
    private Map<Integer, Set<Integer>> moduleFaultAddrMap = Maps.newConcurrentMap();

    @ApiModelProperty(value = "充电枪实时数据")
    private Map<String, GunRealModel> gunRealModelMap = Maps.newConcurrentMap();

    @ApiModelProperty(value = "控制板信息列表 固件类型->控制板信息")
    private Map<Integer, CtrlBoardInfo> ctrlBoardInfoMap = new HashMap<>();

    @ApiModelProperty(value = "固件升级列表 固件类型->固件信息")
    private Map<Integer, UpdateInfoDto> updateInfoMap = Maps.newHashMap();

    @ApiModelProperty(value = "数据时间")
    private String dateTime;

    @ApiModelProperty(value = "离线时间")
    private String offlineTime;

    @Data
    @ApiModel(value = "GunRealModel", description = "充电枪实时数据")
    public static class GunRealModel {

        @ApiModelProperty(value = "枪编号")
        private String gunCode;

        @ApiModelProperty(value = "枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
        private Integer gunStatus;

        @ApiModelProperty(value = "充电枪原始状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 8-暂停 88-离线 255-故障")
        private Integer gunOriginalStatus;

        @ApiModelProperty(value = "地锁状态 0-未知 1-降下 2-升起 3-运动中 4-故障")
        private Integer parkingLockState;

        @ApiModelProperty(value = "车辆连接状态 0-未连接 1-半连接 2-连接 3-连接故障")
        private Integer vehicleConnState;

        @ApiModelProperty(value = "枪锁状态 0-未连接 1-半连接 2-连接 3-连接故障")
        private Integer gunLockState;

        @ApiModelProperty(value = "接触器k1k2状态 0-未连接 1-半连接 2-连接 3-连接故障")
        private Integer k1k2State;

        @ApiModelProperty(value = "交易流水号")
        private String serialNum;

//        @ApiModelProperty(value = "平台标识")
//        private String platformLogo;

        @ApiModelProperty(value = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-皖能平台")
        private Integer starter;

        @ApiModelProperty(value = "充电方式 0-立即充电 1-定时充电 2-自动充电")
        private Integer type;

        @ApiModelProperty(value = "开始时间")
        private String startTime;

        @ApiModelProperty(value = "开始Soc")
        private Integer startSoc;

        @ApiModelProperty(value = "电池Soc")
        private Integer batterySoc;

        @ApiModelProperty(value = "运行模式 -1-未知 0-充电模式 1-放电模式")
        private Integer runMode = -1;

        @ApiModelProperty(value = "枪温度1")
        private Double gunTemp1;

        @ApiModelProperty(value = "枪温度2")
        private Double gunTemp2;

        @ApiModelProperty(value = "输出电压 精度1V")
        private Double outVolt;

        @ApiModelProperty(value = "输出电流 精度1A")
        private Double outCurrent;

        @ApiModelProperty(value = "输出功率 精度1kW")
        private Double outPower;

        @ApiModelProperty(value = "需求电压 精度1V")
        private Double reqVolt;

        @ApiModelProperty(value = "需求电流 精度1A")
        private Double reqCurrent;

        @ApiModelProperty(value = "需求功率 精度1kW")
        private Double reqPower;

        @ApiModelProperty(value = "直流电表读数 精度1kW·h")
        private Double dirMeterNum;

        @ApiModelProperty(value = "交流电表读数 精度1kW·h")
        private Double alterMeterNum;

        @ApiModelProperty(value = "运行时间")
        private Integer runTime;

        @ApiModelProperty(value = "剩余时间")
        private Integer remainTime;

        @ApiModelProperty(value = "总电量")
        private Double totalQt;

        @ApiModelProperty(value = "总费用")
        private BigDecimal totalCost;

        @ApiModelProperty(value = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量")
        private Integer strategy;

        @ApiModelProperty(value = "充放电策略参数")
        private Double strategyCfg;

        @ApiModelProperty(value = "账户类型 1-充/放电卡 2-VIN码 3-手机号")
        private Integer accountType;

        @ApiModelProperty(value = "账户数据")
        private String accountData;

        @ApiModelProperty(value = "预约时间")
        private String clockingTime;

        @ApiModelProperty(value = "预付金额")
        private BigDecimal prepayMoney;

        @ApiModelProperty(value = "枪故障码列表")
        private Set<Integer> faultCodes = Sets.newHashSet();

        @ApiModelProperty(value = "最高动力蓄电池温度")
        private Integer batteryTempMax;

        @ApiModelProperty(value = "最高动力蓄电池温度检测点编号")
        private Integer batteryTempMaxNo;

        @ApiModelProperty(value = "最低动力蓄电池温度")
        private Integer batteryTempMin;

        @ApiModelProperty(value = "最低动力蓄电池温度检测点编号")
        private Integer batteryTempMinNo;

        @ApiModelProperty(value = "最高单体蓄电池电压所对应组号")
        private Integer batteryVoltageMaxGn;

        @ApiModelProperty(value = "最高单体蓄电池电压 精度1V")
        private Double batteryVoltageMax;

        @ApiModelProperty(value = "最低单体蓄电池电压所对应组号")
        private Integer batteryVoltageMinGn;

        @ApiModelProperty(value = "最低单体蓄电池电压 精度1V")
        private Double batteryVoltageMin;

        @ApiModelProperty(value = "单体最高允许充电电压 精度1V")
        private Double bcpAllowChargeCellVMax;

        @ApiModelProperty(value = "最高允许充电电流 精度1A")
        private Double bcpAllowChargeCurrentMax;

        @ApiModelProperty(value = "动力电池标称总能量 精度1kW.h")
        private Double bcpBatteryNominalCap;

        @ApiModelProperty(value = "最高允许电压 精度1V")
        private Double bcpAllowChargeVMax;

        @ApiModelProperty(value = "最高允许充电温度 精度1℃")
        private Integer bcpAllowTempMax;

        @ApiModelProperty(value = "电池类型 01H:铅酸电池; 02H:氢电池; 03H:磷酸铁锂电池; 04H:锰酸锂电池; 05H:钴酸锂电池; 06H:三元电池; 07H:聚合物锂离子电池; 08H:钛酸锂电池; FFH:其他", required = true)
        private Integer batteryType;

    }


}
