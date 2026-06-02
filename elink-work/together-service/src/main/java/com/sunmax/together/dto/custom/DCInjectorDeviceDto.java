package com.sunmax.together.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DCInjectorDeviceDto", description = "直流注塑机数据返回实体类")
public class DCInjectorDeviceDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 合模单元参数实体类
     */
    @ApiModelProperty(value = "合模单元参数实体类")
    private Combination combination;

    /**
     * 螺杆直径(mm)
     */
    @ApiModelProperty(value = "螺杆直径(mm)")
    private Double screwDiameter = 70.0;

    /**
     * 螺杆长径比(LD)
     */
    @ApiModelProperty(value = "螺杆长径比(LD)")
    private Double screwLd = 22.9;

    /**
     * 理论注射容积(cm³)
     */
    @ApiModelProperty(value = "理论注射容积(cm³)")
    private Double theoryInjectVolume = 1424.0;

    /**
     * 射胶重量(PS,g)
     */
    @ApiModelProperty(value = "射胶重量(PS,g)")
    private Double glueWeight = 1296.0;

    /**
     * 射速(g/s)
     */
    @ApiModelProperty(value = "射速(g/s)")
    private Double glueSpeed = 385.0;

    /**
     * 射胶压力(MPa)
     */
    @ApiModelProperty(value = "射胶压力(MPa)")
    private Double gluePressure = 208.0;

    /**
     * 最大射胶压力(MPa)
     */
    @ApiModelProperty(value = "最大射胶压力(MPa)")
    private Double maxGluePressure = 239.0;

    /**
     * 塑化能力(GPPS,g/s)
     */
    @ApiModelProperty(value = "塑化能力(GPPS,g/s)")
    private Double plasticCapGPPS = 71.2;

    /**
     * 塑化能力(HDPE,g/s)
     */
    @ApiModelProperty(value = "塑化能力(HDPE,g/s)")
    private Double plasticCapHDPE = 102.0;

    /**
     * 螺杆转速(rpm)
     */
    @ApiModelProperty(value = "螺杆转速(rpm)")
    private Double screwSpeed = 220.0;

    @Data
    @ApiModel(value = "Combination", description = "合模单元参数实体类")
    public static class Combination {

        /**
         * 合模力(kN)
         */
        @ApiModelProperty(value = "合模力(kN)")
        private Double combinationForce = 5500.0;

        /**
         * 拉杆间距(H×V mm)
         */
        @ApiModelProperty(value = "拉杆间距(H×V mm)")
        private String barSpacing = "920×830";

        /**
         * 最大模厚(mm)
         */
        @ApiModelProperty(value = "最大模厚(mm)")
        private Double maxThickness = 900.0;

        /**
         * 最小模厚(mm)
         */
        @ApiModelProperty(value = "最小模厚(mm)")
        private Double minThickness = 350.0;

        /**
         * 顶出行程(mm)
         */
        @ApiModelProperty(value = "顶出行程(mm)")
        private Double topTravel = 250.0;

        /**
         * 顶出力(kN)
         */
        @ApiModelProperty(value = "顶出力(kN)")
        private Double topForce = 110.0;

        /**
         * 最大容模量(mm)
         */
        @ApiModelProperty(value = "最大容模量(mm)")
        private Double maxTensileStrength = 1650.0;

        /**
         * 模移行程(mm)
         */
        @ApiModelProperty(value = "模移行程(mm)")
        private String modulusTravel = "1300/750";

        /**
         * 最大模重(t)
         */
        @ApiModelProperty(value = "最大模重(t)")
        private Double maxModulusWeight = 8.0;

    }

}
