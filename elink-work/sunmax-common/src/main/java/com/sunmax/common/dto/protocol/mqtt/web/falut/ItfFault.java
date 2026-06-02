package com.sunmax.common.dto.protocol.mqtt.web.falut;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 充/放电接口故障定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItfFault {

    /**
     *枪标识  从1开始
     */
    private Integer gunCode;

    /**
     *CCU 上报故障信息
     *      Bit 0： CC1 连接故障
     *      Bit 1： CC1 接地
     *      Bit 2： 电磁锁故障
     *      Bit 3： 外侧电池电压大于 10V
     *      Bit 4： DC+、DC-反接
     *      Bit 5： 直流接触器故障
     *      Bit 6： 直流熔断器故障
     *      Bit 7： 泄放故障
     *      Bit 8： 输出过压（超过 BMS 最高允许充电电压）
     *      Bit 9： 预充前车端电压超桩端电压阀值
     *      Bit 10：预充前车端电压未达到目标值
     *      Bit 11：BMS 请求电压过高或过低（超过设置阈值）
     *      Bit 12：输出过流（超过 BMS 需求电流）
     *      Bit 13：输出总电压过压（超过设置阈值）
     *      Bit 14：输出总电流过流（超过设置阈值）
     *      Bit 15：BMS 通信中断
     *      Bit 16：直流电表数据异常
     *      Bit 17：交流电表数据异常
     *      Bit 18： 辅助电源异常（辅助电源 12V/10A 过压 欠压 过流 短路）
     *      Bit 19：枪未归位告警
     *      Bit 20：枪过温告警
     */
    private int ccuReport;

    /**
     *车辆 BMS 故障信息
     *      Bit 0： BSM 报文-单体动力蓄电池电压过高
     *      Bit 1： BSM 报文-单体动力蓄电池电压过低
     *      Bit 2： BSM 报文-SOC 过高
     *      Bit 3： BSM 报文-SOC 过低
     *      Bit 4： BSM 报文-动力蓄电池过流\不可信
     *      Bit 5： BSM 报文-动力蓄电池过温\不可信
     *      Bit 6： BSM 报文-动力蓄电池绝缘不正常\不可信
     *      Bit 7： BSM 报文-动力蓄电池输出连接器不正常\不可信
     *      Bit 8： BSM 报文-充电禁止超时
     *      Bit 9： BST 报文-绝缘故障
     *      Bit 10：BST 报文-输出连接器过温故障
     *      Bit 11： BST 报文-BMS 元件、输出连接器过温
     *      Bit 12：BST 报文-充电连接器故障
     *      Bit 13：BST 报文-电池组温度过高故障
     *      Bit 14：BST 报文-高压继电器故障
     *      Bit 15：BST 报文-检测点 2 故障
     *      Bit 16：BST 报文-其他故障
     *      Bit 17：BST 报文-电流过大
     *      Bit 18：BST 报文-电压异常
     *      Bit 19：BST 报文-没报具体错误
     */
    private Integer vehicleBmsFault;

    /**
     *CCU 超时类故障信息
     *      Bit 0： BRM 报文接收超时
     *      Bit 1： BCP 报文接收超时
     *      Bit 2： BRO 报文接收超时
     *      Bit 3： BCL 报文接收超时
     *      Bit 4： BCS 报文接收超时
     *      Bit 5： BSM 报文接收超时
     *      Bit 6： BST 报文接收超时
     *      Bit 7： BSD 报文接收超时
     *      Bit 8： CCU 与 TCU 通讯超时
     *      Bit 9： CCU 与 PCU 通讯超时
     *      Bit 10：直流电表通讯超时
     *      Bit 11：交流电表通讯超时
     *      Bit 12：电力模块输出电压超时-绝缘检测阶段
     *      Bit 13：电力模块输出电压超时-预充阶段
     *      Bit 14：绝缘检测后，输出电压降低到 60V的时间大于 5 秒
     */
    private Integer ccuTimeoutFault;

    /**
     *BMS 超时类故障信息
     *      Bit 0： BEM 报文-CRM 超时
     *      Bit 1： BEM 报文-CML 超时
     *      Bit 2： BEM 报文-CRO 超时
     *      Bit 3： BEM 报文-CCS 超时
     *      Bit 4： BEM 报文-CST 超时
     *      Bit 5： BEM 报文-CSD 超时
     */
    private Integer bmsTimeoutFault;

    /**
     *绝缘检测故障
     */
    private int idmFault;

}
