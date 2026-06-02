package com.sunmax.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteEnergyInfoDto", description = "站点能源信息返回实体类")
public class SiteEnergyInfoDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 电网状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "电网状态 1-开启 2-关闭")
    private Integer powerGridState;

    /**
     * 电网线条方向 1-单向 2-双向
     */
    @ApiModelProperty(value = "电网线条方向 1-单向 2-双向")
    private Integer powerLineDirection;

    /**
     * 电压等级 1-空 2-0.4kV 3-10kV 4-20kV
     */
    @ApiModelProperty(value = "电压等级 1-空 2-0.4kV 3-10kV 4-20kV")
    private Integer voltageGrade;

    /**
     * 变配电状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "变配电状态 1-开启 2-关闭")
    private Integer tranState;

    /**
     * 变配电线条方向 1-单向 2-双向
     */
    @ApiModelProperty(value = "变配电线条方向 1-单向 2-双向")
    private Integer tranLineDirection;

    /**
     * 产权类型 1-空 2-专变 3-公变
     */
    @ApiModelProperty(value = "产权类型 1-空 2-专变 3-公变")
    private Integer propertyRightType;

    /**
     * 配变容量
     */
    @ApiModelProperty(value = "配变容量")
    private Long tranCapacity;

    /**
     * 电桩状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "电桩状态 1-开启 2-关闭")
    private Integer pileState;

    /**
     * 电桩线条方向 1-单向 2-双向
     */
    @ApiModelProperty(value = "电桩线条方向 1-单向 2-双向")
    private Integer pileLineDirection;

    /**
     * 电桩装机量
     */
    @ApiModelProperty(value = "电桩装机量")
    private Long pileInstallCapacity;

    /**
     * 是否支持V2G 1-是 2-否
     */
    @ApiModelProperty(value = "是否支持V2G 1-是 2-否")
    private Integer isSupportV2g;

    /**
     * 光伏状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "光伏状态 1-开启 2-关闭")
    private Integer pvState;

    /**
     * 光伏线条方向 1-单向 2-双向
     */
    @ApiModelProperty(value = "光伏线条方向 1-单向 2-双向")
    private Integer pvLineDirection;

    /**
     * 光伏装机量
     */
    @ApiModelProperty(value = "光伏装机量")
    private Long pvInstallCapacity;

    /**
     * 光伏运行方式 1-空 2-并网运行 3-离网运行
     */
    @ApiModelProperty(value = "光伏运行方式 1-空 2-并网运行 3-离网运行")
    private Integer pvRunWay;

    /**
     * 并网模式 1-空 2-自发自用余电上网 3-全额上网
     */
    @ApiModelProperty(value = "并网模式 1-空 2-自发自用余电上网 3-全额上网")
    private Integer onGridMode;

    /**
     * 储能状态 1-开启 2-关闭
     */
    @ApiModelProperty(value = "储能状态 1-开启 2-关闭")
    private Integer storageState;

    /**
     * 储能线条方向 1-单向 2-双向
     */
    @ApiModelProperty(value = "储能线条方向 1-单向 2-双向")
    private Integer storageLineDirection;

    /**
     * 储能装机量
     */
    @ApiModelProperty(value = "储能装机量")
    private Long storageInstallCapacity;

    /**
     * 储能额定功率
     */
    @ApiModelProperty(value = "储能额定功率")
    private Long storageRatedPower;

    /**
     * 储能运行方式 1-空 2-并网运行 3-离网运行
     */
    @ApiModelProperty(value = "储能运行方式 1-空 2-并网运行 3-离网运行")
    private Integer storageRunWay;
}
