package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备容量返回实体类")
public class DeviceCapDto {

    /**
     * 电桩容量
     */
    @Schema(description = "电桩容量")
    private Double pileCap = 0.0;

    /**
     * 储能PCS额定功率
     */
    @Schema(description = "储能PCS额定功率")
    private Double pcsPower = 0.0;

    /**
     * 储能电池簇额定容量
     */
    @Schema(description = "储能电池簇额定容量")
    private Double batteryCap = 0.0;

    /**
     * 光伏容量
     */
    @Schema(description = "光伏容量")
    private Double pvCap = 0.0;

    /**
     * 二氧化碳(CO2)减排量(kg)
     */
    @Schema(description = "二氧化碳(CO2)减排量（kg）")
    private Double co2Reduction = 0.0;

    /**
     * 节约标准煤量(kg)
     */
    @Schema(description = "节约标准煤量（kg）")
    private Double standardCoalReduction = 0.0;

    /**
     * 等效植树量(颗)
     */
    @Schema(description = "等效植树量（颗）")
    private Double treeReduction = 0.0;

}
