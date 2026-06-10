package com.sunmax.configure.dto.city;

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
     * 车位状态
     * 0：未知
     * 10：空闲
     * 50：占用
     */
    @Schema(description = "车位状态")
    @JSONField(name = "ParkStatus")
    private Integer parkStatus;

    /**
     * 地锁状态
     * 0：未知
     * 10：已解锁
     * 50：已上锁
     */
    @Schema(description = "地锁状态")
    @JSONField(name = "LockStatus")
    private Integer lockStatus;

    /**
     * 剩余电量
     */
    @Schema(description = "剩余电量")
    @JSONField(name = "SOC")
    private Double soc;

    /**
     * 异常原因
     */
    @Schema(description = "异常原因")
    @JSONField(name = "FaultType")
    private Integer faultType;

    /**
     * 已充时长
     * 单位：分钟
     */
    @Schema(description = "已充时长")
    @JSONField(name = "Edtime")
    private Integer edtime;

    /**
     * 已充电量
     * 单位：千瓦时
     */
    @Schema(description = "已充电量")
    @JSONField(name = "Edpq")
    private Double edpq;
}
