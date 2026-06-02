package com.sunmax.configure.dto.province;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/2014:49
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "ConnectorInfoDto", description = "充电设备接口信息实体类")
public class ConnectorInfoDto {

    /**
     * 接口编码
     */
    @ApiModelProperty(value = "接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 浙江省充电设备接口唯一码
     */
    @ApiModelProperty(value = "浙江省充电设备接口唯一码")
    @JSONField(name = "ConnectorUniqueID")
    private String connectorUniqueId;

    /**
     * 充电设备接口名称
     */
    @ApiModelProperty(value = "充电设备接口名称")
    @JSONField(name = "ConnectorName")
    private String connectorName;

    /**
     * 充电设备接口类型
     * 1：家用插座（模式2）
     * 2：交流接口插座（模式3，连接方式B ）
     * 3：交流接口插头（带枪线，模式3，连接方式C）
     * 4：直流接口枪头（带枪线，模式4）
     * 5：无线充电座
     * 6：其它
     */
    @ApiModelProperty(value = "充电设备接口类型")
    @JSONField(name = "ConnectorType")
    private Integer connectorType;

    /**
     * 二维码解析地址清单
     */
    @ApiModelProperty(value = "二维码解析地址清单")
    @JSONField(name = "QrCodes")
    private List<String> qrCodes = Lists.newArrayList();

    /**
     * 额定电压上限
     */
    @ApiModelProperty(value = "额定电压上限")
    @JSONField(name = "VoltageUpperLimits")
    private Double voltageUpperLimits;

    /**
     * 额定电压下限
     */
    @ApiModelProperty(value = "额定电压下限")
    @JSONField(name = "VoltageLowerLimits")
    private Double voltageLowerLimits;

    /**
     * 恒功率电压上限
     */
    @ApiModelProperty(value = "恒功率电压上限")
    @JSONField(name = "ConstantVoltageUpperLimits")
    private Double constantVoltageUpperLimits;

    /**
     * 恒功率电压下限
     */
    @ApiModelProperty(value = "恒功率电压下限")
    @JSONField(name = "ConstantVoltageLowerLimits")
    private Double constantVoltageLowerLimits;

    /**
     * 额定电流
     */
    @ApiModelProperty(value = "额定电流")
    @JSONField(name = "Current")
    private Double current;

    /**
     * 恒功率电流上限
     */
    @ApiModelProperty(value = "恒功率电流上限")
    @JSONField(name = "ConstantCurrentUpperLimits")
    private Double constantCurrentUpperLimits;

    /**
     * 恒功率电流下限
     */
    @ApiModelProperty(value = "恒功率电流下限")
    @JSONField(name = "ConstantCurrentLowerLimits")
    private Double constantCurrentLowerLimits;

    /**
     * 额定功率
     */
    @ApiModelProperty(value = "额定功率")
    @JSONField(name = "Power")
    private Double power;

    /**
     * 车位号
     */
    @ApiModelProperty(value = "车位号")
    @JSONField(name = "ParkNo")
    private String parkNo;

    /**
     * 国家标准
     *
     * 1：2011
     * 2：2015
     * 3：兼容2011和2015
     * 4：2023
     */
    @ApiModelProperty(value = "国家标准")
    @JSONField(name = "NationalStandard")
    private Integer nationalStandard;

    /**
     * 辅助电源
     *
     * 1：12V
     * 2：24V
     * 3：兼容12V和24V
     */
    @ApiModelProperty(value = "辅助电源")
    @JSONField(name = "AuxPower")
    private Integer auxPower;

    /**
     * 运营状态
     *
     * 0：未知
     * 1：建设中
     * 5：关闭下线
     * 6：维护中
     * 50：正常使用
     */
    @ApiModelProperty(value = "运营状态")
    @JSONField(name = "OperateStatus")
    private Integer operateStatus;

    /**
     * 运营时间
     */
    @ApiModelProperty(value = "运营时间")
    @JSONField(name = "OperateHours")
    private String operateHours;
}
