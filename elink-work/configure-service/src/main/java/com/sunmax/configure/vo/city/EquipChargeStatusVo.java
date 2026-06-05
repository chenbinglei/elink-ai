package com.sunmax.configure.vo.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "EquipChargeStatusVo", description = "推送充电状态参数实体类")
public class EquipChargeStatusVo {

    /**
     * 充电订单号 格式“运营商 ID+唯一编号”， 27 字符 (是)
     */
    @ApiModelProperty(value = "充电订单号 格式“运营商 ID+唯一编号”， 27 字符")
    @JSONField(name = "StartChargeSeq")
    private String startChargeSeq;

    /**
     * 充电订单状态 (是)
     * 1：启动中；
     * 2：充电中；
     * 3：停止中；
     * 4：已结束；
     * 5：未知
     */
    @ApiModelProperty(value = "充电订单状态")
    @JSONField(name = "startChargeSeqStat")
    private Integer startChargeSeqStat;

    /**
     * 充电设备接口编码 (是)
     * 参见《电动汽车充换电服务信息交换 第 2 部分： 公共信息交换规范》
     */
    @ApiModelProperty(value = "充电设备接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 充电设备接口状态 (是)
     * 1：空闲；
     * 2：占用（未充电） ；
     * 3：占用（充电中） ；
     * 4：占用（预约锁定）；
     * 255：故障
     */
    @ApiModelProperty(value = "充电设备接口状态")
    @JSONField(name = "ConnectorStatus")
    private Integer connectorStatus;

    /**
     * A相电流 (是)
     */
    @ApiModelProperty(value = "A相电流")
    @JSONField(name = "CurrentA")
    private Double currentA = 0.0;

    /**
     * B相电流 (否)
     */
    @ApiModelProperty(value = "B相电流")
    @JSONField(name = "CurrentB")
    private Double currentB = 0.0;

    /**
     * C相电流 (否)
     */
    @ApiModelProperty(value = "C相电流")
    @JSONField(name = "CurrentC")
    private Double currentC = 0.0;

    /**
     * A相电压 (是)
     */
    @ApiModelProperty(value = "A相电压")
    @JSONField(name = "VoltageA")
    private Double voltageA = 0.0;

    /**
     * B相电压 (否)
     */
    @ApiModelProperty(value = "B相电压")
    @JSONField(name = "VoltageB")
    private Double voltageB = 0.0;

    /**
     * C相电压 (否)
     */
    @ApiModelProperty(value = "C相电压")
    @JSONField(name = "VoltageC")
    private Double voltageC = 0.0;

    /**
     * 电池剩余电量 (是)
     */
    @ApiModelProperty(value = "电池剩余电量")
    @JSONField(name = "Soc")
    private Double soc = 0.0;

    /**
     * 开始充电时间 (是)
     */
    @ApiModelProperty(value = "开始充电时间")
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 本次采样时间 (是)
     */
    @ApiModelProperty(value = "本次采样时间")
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 累计充电量 (是)
     */
    @ApiModelProperty(value = "累计充电量")
    @JSONField(name = "TotalPower")
    private Double totalPower;

    /**
     * 累计电费 (否)
     */
    @ApiModelProperty(value = "累计电费")
    @JSONField(name = "ElecMoney")
    private Double elecMoney;

    /**
     * 累计服务费 (否)
     */
    @ApiModelProperty(value = "累计服务费")
    @JSONField(name = "SeviceMoney")
    private Double seviceMoney;

    /**
     * 累计总金额 (否)
     */
    @ApiModelProperty(value = "累计总金额")
    @JSONField(name = "TotalMoney")
    private Double totalMoney;

    /**
     * 时段数 N (否)
     */
    @ApiModelProperty(value = "时段数 N")
    @JSONField(name = "SumPeriod")
    private Integer sumPeriod;

    /**
     * 充电明细信息 (否)
     */
    @ApiModelProperty(value = "充电明细信息")
    @JSONField(name = "ChargeDetails")
    private List<ChargeDetailsVo> chargeDetails = Lists.newArrayList();

}
