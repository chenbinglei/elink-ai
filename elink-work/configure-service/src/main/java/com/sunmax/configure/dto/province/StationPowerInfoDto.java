package com.sunmax.configure.dto.province;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "StationPowerInfoDto", description = "充电站功率信息")
public class StationPowerInfoDto {

    /**
     * 平台运营商ID
     */
    @ApiModelProperty(value = "平台运营商ID", required = true)
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 充电服务运营商 ID，
     * 所属方为个人时填写 999999999
     */
    @ApiModelProperty(value = "充电服务运营商ID", required = true)
    @JSONField(name = "EquipmentOwnerID")
    private String equipmentOwnerId;

    /**
     * 充电站ID
     */
    @ApiModelProperty(value = "充电站ID", required = true)
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 统计时间
     */
    @ApiModelProperty(value = "统计时间", required = true)
    @JSONField(name = "DataTime")
    private String dataTime;

    /**
     * 充电站实时功率
     */
    @ApiModelProperty(value = "充电站实时功率", required = true)
    @JSONField(name = "StationRealTimePower")
    private Double stationRealTimePower = 0.0;

    /**
     * 充电设备功率信息列表
     */
    @ApiModelProperty(value = "充电设备功率信息列表", required = true)
    @JSONField(name = "EquipmentPowerInfos")
    private List<EquipmentPowerInfo> equipmentPowerInfos = Lists.newArrayList();

    @Data
    public static class EquipmentPowerInfo {

        /**
         * 充电设备编码
         */
        @ApiModelProperty(value = "充电设备编码", required = true)
        @JSONField(name = "EquipmentID")
        private String equipmentId;

        /**
         * 统计时间
         */
        @ApiModelProperty(value = "统计时间", required = true)
        @JSONField(name = "DataTime")
        private String dataTime;

        /**
         * 充电设备实时功率 单位kW
         */
        @ApiModelProperty(value = "充电设备实时功率", required = true)
        @JSONField(name = "EquipRealTimePower")
        private Double equipRealTimePower = 0.0;

        /**
         * 充电设备接口功率信息列表
         */
        @ApiModelProperty(value = "充电设备接口功率信息列表", required = true)
        @JSONField(name = "ConnectorPowerInfos")
        private List<ConnectorPowerInfo> connectorPowerInfos = Lists.newArrayList();

    }

    @Data
    public static class ConnectorPowerInfo {

        /**
         * 充电设备接口编码
         */
        @ApiModelProperty(value = "充电设备接口编码", required = true)
        @JSONField(name = "ConnectorID")
        private String connectorId;

        /**
         * 统计时间
         */
        @ApiModelProperty(value = "统计时间", required = true)
        @JSONField(name = "DataTime")
        private String dataTime;

        /**
         * 充电设备接口实时功率 单位kW
         */
        @ApiModelProperty(value = "充电设备接口实时功率", required = true)
        @JSONField(name = "ConnectorRealTimePower")
        private Double connectorRealTimePower = 0.0;

    }

}
