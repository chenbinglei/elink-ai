package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/2014:36
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "充电设备信息实体类")
public class EquipmentInfoDto {

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    @JSONField(name = "EquipmentID")
    private String equipmentId;

    /**
     * 浙江省充电设备唯一码
     * 由浙江省平台赋予各台充电设备的唯一标识码
     */
    @Schema(description = "浙江省充电设备唯一码")
    @JSONField(name = "EquipmentUniqueID")
    private String equipmentUniqueId;

    /**
     * 设备生产商组织机构代码
     */
    @Schema(description = "设备生产商组织机构代码")
    @JSONField(name = "ManufacturerID")
    private String manufacturerId;

    /**
     * 设备生产商名称
     */
    @Schema(description = "设备生产商名称")
    @JSONField(name = "ManufacturerName")
    private String manufacturerName;

    /**
     * 设备出厂唯一编码
     * 设备生产厂商赋予的设备出厂唯一编码
     */
    @Schema(description = "设备出厂唯一编码")
    @JSONField(name = "EquipmentManufacturerNumber")
    private String equipmentManufacturerNumber;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    @JSONField(name = "EquipmentModel")
    private String equipmentModel;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    @JSONField(name = "EquipmentName")
    private String equipmentName;

    /**
     * 设备生产日期
     */
    @Schema(description = "设备生产日期")
    @JSONField(name = "ProductionDate")
    private String productionDate;

    /**
     * 设备类型
     * 1：直流设备
     * 2：交流设备
     * 3：交直流一体设备
     * 4：无线设备
     * 5：有序
     * 6：V2G
     * 7：其他
     */
    @Schema(description = "设备类型")
    @JSONField(name = "EquipmentType")
    private Integer equipmentType;

    /**
     * 充电设备总功率
     */
    @Schema(description = "充电设备总功率")
    @JSONField(name = "Power")
    private Double power;

    /**
     * 充电设备经度
     */
    @Schema(description = "充电设备经度")
    @JSONField(name = "EquipmentLng")
    private Double equipmentLng;

    /**
     * 充电设备纬度
     */
    @Schema(description = "充电设备纬度")
    @JSONField(name = "EquipmentLat")
    private Double equipmentLat;

    /**
     * 充电设备接口列表
     */
    @Schema(description = "充电设备接口列表")
    @JSONField(name = "ConnectorInfos")
    private List<ConnectorInfoDto> connectorInfos;
}
