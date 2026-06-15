package com.sunmax.together.dto.operation.seriesInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "组串配置信息返回实体类")
public class SeriesConfigInfoDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 组串名称
     */
    @Schema(description = "组串名称")
    private String seriesName;

    /**
     * 组件数量
     */
    @Schema(description = "组件数量")
    private Integer moduleNum;

    /**
     * 组串容量
     */
    @Schema(description = "组串容量")
    private Double seriesCapacity;

    /**
     * 组件厂家
     */
    @Schema(description = "组件厂家")
    private String moduleFactory;

    /**
     * 组件型号
     */
    @Schema(description = "组件型号")
    private String moduleModel;

    /**
     * 组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面
     */
    @Schema(description = "组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面")
    private Integer moduleType;

    /**
     * 组件电池片数（片/组件）
     */
    @Schema(description = "组件电池片数（片/组件）")
    private Integer batteryPieces;

    /**
     * 填充因子
     */
    @Schema(description = "填充因子")
    private Double fillFactor;

    /**
     * 组件最大功率(Pmax)(W)
     */
    @Schema(description = "组件最大功率(Pmax)(W)")
    private Double maxPower;

    /**
     * 组件最佳工作电压(Vmp) (V)
     */
    @Schema(description = "组件最佳工作电压(Vmp) (V)")
    private Double bestWorkVoltage;

    /**
     * 组件最佳工作电流(Imp) (A)
     */
    @Schema(description = "组件最佳工作电流(Imp) (A)")
    private Double bestWorkCurrent;

    /**
     * 组件开路电压(Voc)(V)
     */
    @Schema(description = "组件开路电压(Voc)(V)")
    private Double openVoltage;

    /**
     * 组件短路电流(Isc)(A)
     */
    @Schema(description = "组件短路电流(Isc)(A)")
    private Double shortCurrent;

    /**
     * 最大功率(Pmax)的温度系数 (%/°C)
     */
    @Schema(description = "最大功率(Pmax)的温度系数 (%/°C)")
    private Double maxPowerTempCoeff;

    /**
     * 开路电压(Voc)的温度系数 (%/°C)
     */
    @Schema(description = "开路电压(Voc)的温度系数 (%/°C)")
    private Double openVoltTempCoeff;

    /**
     * 短路电流(Isc)的温度系数 (%/°C)
     */
    @Schema(description = "短路电流(Isc)的温度系数 (%/°C)")
    private Double shortCurrTempCoeff;

    /**
     * 标称组件转换效率(%)
     */
    @Schema(description = "标称组件转换效率(%)")
    private Double convertEffi;

    /**
     * 组件首年衰减率(%/y)
     */
    @Schema(description = "组件首年衰减率(%/y)")
    private Double firstDecayRate;

    /**
     * 组件逐年衰减率(%/y)
     */
    @Schema(description = "组件逐年衰减率(%/y)")
    private Double passingDecayRate;
}
