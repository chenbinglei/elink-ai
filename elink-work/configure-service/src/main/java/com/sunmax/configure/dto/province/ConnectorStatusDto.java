package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/1916:09
 * @version: 1.0
 * @注释: 充电设备接口状态实体类
 */
@Data
@Schema(description = "充电设备接口状态实体类")
public class ConnectorStatusDto {

    /**
     * 平台运营商ID
     */
    @Schema(description = "平台运营商ID")
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 充电服务运营商 ID，
     * 所属方为个人时填写 999999999
     */
    @Schema(description = "充电服务运营商ID")
    @JSONField(name = "EquipmentOwnerID")
    private String equipmentOwnerId;

    /**
     * 充电站ID
     */
    @Schema(description = "充电站ID")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 充电设备编码
     */
    @Schema(description = "充电设备编码")
    @JSONField(name = "EquipmentID")
    private String equipmentId;

    /**
     * 接口编码
     */
    @Schema(description = "接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 接口状态
     * 0：离网
     * 1：空闲
     * 2：占用（未充电）
     * 3：占用（充电中）
     * 4：占用（预约锁定）
     * 255：故障
     */
    @Schema(description = "接口状态")
    @JSONField(name = "Status")
    private Integer status;

    /**
     * 接口状态描述
     * 接口状态为自定义时的含义描述，接口状态为自定义时必填
     */
    @Schema(description = "接口状态描述")
    @JSONField(name = "StatusDesc")
    private String statusDesc;

    /**
     * 车位状态
     * 0：未知
     * 10：空闲
     * 50：占用
     */
    @Schema(description = "车位状态")
    @JSONField(name = "ParkStatus")
    private Integer parkStatus = 0;

    /**
     * 地锁状态
     * 0：未知
     * 10：已解锁
     * 50：已上锁
     */
    @Schema(description = "地锁状态")
    @JSONField(name = "LockStatus")
    private Integer lockStatus = 0;

    /**
     * 状态更新时间
     * 本次状态变化的时间, 格式：yyyy-MM-dd HH:mm:ss
     */
    @Schema(description = "状态更新时间")
    @JSONField(name = "UpdateTime")
    private String updateTime;
}
