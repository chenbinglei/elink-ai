package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceCapDto", description = "设备容量返回实体类")
public class DeviceCapDto {

    /**
     * 电桩容量
     */
    @ApiModelProperty(value = "电桩容量")
    private Double pileCap = 0.0;

    /**
     * 储能PCS额定功率
     */
    @ApiModelProperty(value = "储能PCS额定功率")
    private Double pcsPower = 0.0;

    /**
     * 储能电池簇额定容量
     */
    @ApiModelProperty(value = "储能电池簇额定容量")
    private Double batteryCap = 0.0;

    /**
     * 光伏容量
     */
    @ApiModelProperty(value = "光伏容量")
    private Double pvCap = 0.0;

    /**
     * 二氧化碳(CO2)减排量(kg)
     */
    @ApiModelProperty(value = "二氧化碳(CO2)减排量（kg）")
    private Double co2Reduction = 0.0;

    /**
     * 节约标准煤量(kg)
     */
    @ApiModelProperty(value = "节约标准煤量（kg）")
    private Double standardCoalReduction = 0.0;

    /**
     * 等效植树量(颗)
     */
    @ApiModelProperty(value = "等效植树量（颗）")
    private Double treeReduction = 0.0;

}
