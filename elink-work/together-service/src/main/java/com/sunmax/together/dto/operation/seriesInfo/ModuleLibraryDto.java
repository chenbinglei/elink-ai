package com.sunmax.together.dto.operation.seriesInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModuleLibraryDto", description = "组件库数据返回实体类")
public class ModuleLibraryDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 组件厂家
     */
    @ApiModelProperty(value = "组件厂家")
    private String moduleFactory;

    /**
     * 组件型号
     */
    @ApiModelProperty(value = "组件型号")
    private String moduleModel;

    /**
     * 组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面
     */
    @ApiModelProperty(value = "组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面")
    private Integer moduleType;

    /**
     * 组件电池片数（片/组件）
     */
    @ApiModelProperty(value = "组件电池片数（片/组件）")
    private Integer batteryPieces;

    /**
     * 填充因子
     */
    @ApiModelProperty(value = "填充因子")
    private Double fillFactor;

    /**
     * 组件最大功率(Pmax)(W)
     */
    @ApiModelProperty(value = "组件最大功率(Pmax)(W)")
    private Double maxPower;

    /**
     * 组件最佳工作电压(Vmp) (V)
     */
    @ApiModelProperty(value = "组件最佳工作电压(Vmp) (V)")
    private Double bestWorkVoltage;

    /**
     * 组件最佳工作电流(Imp) (A)
     */
    @ApiModelProperty(value = "组件最佳工作电流(Imp) (A)")
    private Double bestWorkCurrent;

    /**
     * 组件开路电压(Voc)(V)
     */
    @ApiModelProperty(value = "组件开路电压(Voc)(V)")
    private Double openVoltage;

    /**
     * 组件短路电流(Isc)(A)
     */
    @ApiModelProperty(value = "组件短路电流(Isc)(A)")
    private Double shortCurrent;

    /**
     * 最大功率(Pmax)的温度系数 (%/°C)
     */
    @ApiModelProperty(value = "最大功率(Pmax)的温度系数 (%/°C)")
    private Double maxPowerTempCoeff;

    /**
     * 开路电压(Voc)的温度系数 (%/°C)
     */
    @ApiModelProperty(value = "开路电压(Voc)的温度系数 (%/°C)")
    private Double openVoltTempCoeff;

    /**
     * 短路电流(Isc)的温度系数 (%/°C)
     */
    @ApiModelProperty(value = "短路电流(Isc)的温度系数 (%/°C)")
    private Double shortCurrTempCoeff;

    /**
     * 标称组件转换效率(%)
     */
    @ApiModelProperty(value = "标称组件转换效率(%)")
    private Double convertEffi;

    /**
     * 组件首年衰减率(%/y)
     */
    @ApiModelProperty(value = "组件首年衰减率(%/y)")
    private Double firstDecayRate;

    /**
     * 组件逐年衰减率(%/y)
     */
    @ApiModelProperty(value = "组件逐年衰减率(%/y)")
    private Double passingDecayRate;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 最后修改时间
     */
    @ApiModelProperty(value = "最后修改时间")
    private String updateTime;

    /**
     * 创建者名称
     */
    @ApiModelProperty(value = "创建者名称")
    private String createName;
}
