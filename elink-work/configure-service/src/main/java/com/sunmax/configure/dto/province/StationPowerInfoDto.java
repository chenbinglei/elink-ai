package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "充电站功率信息")
public class StationPowerInfoDto {

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
     * 统计时间
     */
    @Schema(description = "统计时间")
    @JSONField(name = "DataTime")
    private String dataTime;

    /**
     * 充电站实时功率
     */
    @Schema(description = "充电站实时功率")
    @JSONField(name = "StationRealTimePower")
    private Double stationRealTimePower = 0.0;

    /**
     * 充电设备功率信息列表
     */
    @Schema(description = "充电设备功率信息列表")
    @JSONField(name = "EquipmentPowerInfos")
    private List<EquipmentPowerInfo> equipmentPowerInfos = Lists.newArrayList();

    @Data
    public static class EquipmentPowerInfo {

        /**
         * 充电设备编码
         */
        @Schema(description = "充电设备编码")
        @JSONField(name = "EquipmentID")
        private String equipmentId;

        /**
         * 统计时间
         */
        @Schema(description = "统计时间")
        @JSONField(name = "DataTime")
        private String dataTime;

        /**
         * 充电设备实时功率 单位kW
         */
        @Schema(description = "充电设备实时功率")
        @JSONField(name = "EquipRealTimePower")
        private Double equipRealTimePower = 0.0;

        /**
         * 充电设备接口功率信息列表
         */
        @Schema(description = "充电设备接口功率信息列表")
        @JSONField(name = "ConnectorPowerInfos")
        private List<ConnectorPowerInfo> connectorPowerInfos = Lists.newArrayList();

    }

    @Data
    public static class ConnectorPowerInfo {

        /**
         * 充电设备接口编码
         */
        @Schema(description = "充电设备接口编码")
        @JSONField(name = "ConnectorID")
        private String connectorId;

        /**
         * 统计时间
         */
        @Schema(description = "统计时间")
        @JSONField(name = "DataTime")
        private String dataTime;

        /**
         * 充电设备接口实时功率 单位kW
         */
        @Schema(description = "充电设备接口实时功率")
        @JSONField(name = "ConnectorRealTimePower")
        private Double connectorRealTimePower = 0.0;

    }

}
