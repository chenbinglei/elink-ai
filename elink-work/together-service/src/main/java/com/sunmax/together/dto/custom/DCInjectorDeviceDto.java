package com.sunmax.together.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "直流注塑机数据返回实体类")
public class DCInjectorDeviceDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 合模单元参数实体类
     */
    @Schema(description = "合模单元参数实体类")
    private Combination combination;

    /**
     * 螺杆直径(mm)
     */
    @Schema(description = "螺杆直径(mm)")
    private Double screwDiameter = 70.0;

    /**
     * 螺杆长径比(LD)
     */
    @Schema(description = "螺杆长径比(LD)")
    private Double screwLd = 22.9;

    /**
     * 理论注射容积(cm³)
     */
    @Schema(description = "理论注射容积(cm³)")
    private Double theoryInjectVolume = 1424.0;

    /**
     * 射胶重量(PS,g)
     */
    @Schema(description = "射胶重量(PS,g)")
    private Double glueWeight = 1296.0;

    /**
     * 射速(g/s)
     */
    @Schema(description = "射速(g/s)")
    private Double glueSpeed = 385.0;

    /**
     * 射胶压力(MPa)
     */
    @Schema(description = "射胶压力(MPa)")
    private Double gluePressure = 208.0;

    /**
     * 最大射胶压力(MPa)
     */
    @Schema(description = "最大射胶压力(MPa)")
    private Double maxGluePressure = 239.0;

    /**
     * 塑化能力(GPPS,g/s)
     */
    @Schema(description = "塑化能力(GPPS,g/s)")
    private Double plasticCapGPPS = 71.2;

    /**
     * 塑化能力(HDPE,g/s)
     */
    @Schema(description = "塑化能力(HDPE,g/s)")
    private Double plasticCapHDPE = 102.0;

    /**
     * 螺杆转速(rpm)
     */
    @Schema(description = "螺杆转速(rpm)")
    private Double screwSpeed = 220.0;

    @Data
    @Schema(description = "合模单元参数实体类")
    public static class Combination {

        /**
         * 合模力(kN)
         */
        @Schema(description = "合模力(kN)")
        private Double combinationForce = 5500.0;

        /**
         * 拉杆间距(H×V mm)
         */
        @Schema(description = "拉杆间距(H×V mm)")
        private String barSpacing = "920×830";

        /**
         * 最大模厚(mm)
         */
        @Schema(description = "最大模厚(mm)")
        private Double maxThickness = 900.0;

        /**
         * 最小模厚(mm)
         */
        @Schema(description = "最小模厚(mm)")
        private Double minThickness = 350.0;

        /**
         * 顶出行程(mm)
         */
        @Schema(description = "顶出行程(mm)")
        private Double topTravel = 250.0;

        /**
         * 顶出力(kN)
         */
        @Schema(description = "顶出力(kN)")
        private Double topForce = 110.0;

        /**
         * 最大容模量(mm)
         */
        @Schema(description = "最大容模量(mm)")
        private Double maxTensileStrength = 1650.0;

        /**
         * 模移行程(mm)
         */
        @Schema(description = "模移行程(mm)")
        private String modulusTravel = "1300/750";

        /**
         * 最大模重(t)
         */
        @Schema(description = "最大模重(t)")
        private Double maxModulusWeight = 8.0;

    }

}
