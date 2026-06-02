package com.sunmax.configure.dto.city;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
     * 充电设备接口名称
     */
    @ApiModelProperty(value = "充电设备接口名称")
    @JSONField(name = "ConnectorName")
    private String connectorName;

    /**
     * 充电设备接口类型
     * 1： 家用插座 （模式 2）
     * 2：交流接口插座（模式 3， 连接方式 B ）
     * 3：交流接口插头（带枪  线，模式 3，连接方式 C）
     * 4：直流接口枪头（带枪 线， 模式 4）
     */
    @ApiModelProperty(value = "充电设备接口类型")
    @JSONField(name = "ConnectorType")
    private Integer connectorType;

    /**
     * 额定电压上限
     */
    @ApiModelProperty(value = "额定电压上限")
    @JSONField(name = "VoltageUpperLimits")
    private Long voltageUpperLimits;

    /**
     * 额定电压下限
     */
    @ApiModelProperty(value = "额定电压下限")
    @JSONField(name = "VoltageLowerLimits")
    private Long voltageLowerLimits;

    /**
     * 额定电流
     */
    @ApiModelProperty(value = "额定电流")
    @JSONField(name = "Current")
    private Long current;

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
     * 1:2011
     * 2:2015
     */
    @ApiModelProperty(value = "国家标准")
    @JSONField(name = "NationalStandard")
    private Integer nationalStandard;
}
