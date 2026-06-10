package com.sunmax.configure.dto.interflow;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2024/8/16 13:49
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "充电设备接口信息实体类")
public class ConnectorInfoDto {

    /**
     * 接口编码
     */
    @Schema(description = "接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 充电设备接口名称
     */
    @Schema(description = "充电设备接口名称")
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
    @Schema(description = "充电设备接口类型")
    @JSONField(name = "ConnectorType")
    private Integer connectorType;

    /**
     * 额定电压上限
     */
    @Schema(description = "额定电压上限")
    @JSONField(name = "VoltageUpperLimits")
    private Integer voltageUpperLimits;

    /**
     * 额定电压下限
     */
    @Schema(description = "额定电压下限")
    @JSONField(name = "VoltageLowerLimits")
    private Integer voltageLowerLimits;

    /**
     * 额定电流
     */
    @Schema(description = "额定电流")
    @JSONField(name = "Current")
    private Integer current;

    /**
     * 额定功率
     */
    @Schema(description = "额定功率")
    @JSONField(name = "Power")
    private Double power;

    /**
     * 车位号
     */
    @Schema(description = "车位号")
    @JSONField(name = "ParkNo")
    private String parkNo;

    /**
     * 国家标准
     *
     * 1：2011
     * 2：2015
     */
    @Schema(description = "国家标准")
    @JSONField(name = "NationalStandard")
    private Integer nationalStandard;

    /**
     * 充电设备接口模式
     *
     * 0:慢充
     * 1:快充
     * 2:超充
     */
    @Schema(description = "充电设备接口模式")
    @JSONField(name = "ConnectorModel")
    private Integer connectorModel;
}
