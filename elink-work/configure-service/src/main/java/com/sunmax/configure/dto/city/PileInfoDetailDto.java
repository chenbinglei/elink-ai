package com.sunmax.configure.dto.city;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/2016:33
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "PileInfoDetailDto", description = "桩信息明细实体类")
public class PileInfoDetailDto {

    /**
     * 桩名
     */
    @ApiModelProperty(value = "桩名")
    @JSONField(name = "EquipmentName")
    private String equipmentName;

    /**
     * 所属站
     */
    @ApiModelProperty(value = "所属站")
    @JSONField(name = "StationId")
    private String stationId;

    /**
     * 所属运营商id
     */
    @ApiModelProperty(value = "所属运营商id")
    @JSONField(name = "OperatorId")
    private String operatorId;

    /**
     * 桩容量
     */
    @ApiModelProperty(value = "桩容量")
    @JSONField(name = "EquipmentCapacity")
    private Double equipmentCapacity;

    /**
     * 桩类型
     * 1 直流、2 交流、3 通用
     */
    @ApiModelProperty(value = "桩类型")
    @JSONField(name = "EquipmentType")
    private Integer equipmentType;

    /**
     * 桩厂家
     */
    @ApiModelProperty(value = "桩厂家")
    @JSONField(name = "ManufacturerName")
    private String manufacturerName;

    /**
     * 投资方
     */
    @ApiModelProperty(value = "投资方")
    @JSONField(name = "EquipmentInvestor")
    private String equipmentInvestor;

    /**
     * 桩公专用
     * 1 公用、2 专用
     */
    @ApiModelProperty(value = "桩公专用")
    @JSONField(name = "EquipmentUsing")
    private Integer equipmentUsing;

    /**
     * 位置经度
     */
    @ApiModelProperty(value = "位置经度")
    @JSONField(name = "Longitude")
    private Double longitude;

    /**
     * 位置纬度
     */
    @ApiModelProperty(value = "位置纬度")
    @JSONField(name = "Latitude")
    private Double latitude;

    /**
     * 额定调节上限
     */
    @ApiModelProperty(value = "额定调节上限")
    @JSONField(name = "UpperAdjust")
    private Double upperAdjust;

    /**
     * 额定调节下限
     */
    @ApiModelProperty(value = "额定调节下限")
    @JSONField(name = "LowerLimit")
    private Double lowerLimit;

    /**
     * 运营系统内部设备id
     */
    @ApiModelProperty(value = "运营系统内部设备id")
    @JSONField(name = "EquipmentId")
    private String equipmentId;

    /**
     * 是否可控
     * 0:不可控 1 可控
     */
    @ApiModelProperty(value = "是否可控")
    @JSONField(name = "EquipControllable")
    private Integer equipControllable;

    /**
     * 报装户号
     */
    @ApiModelProperty(value = "报装户号")
    @JSONField(name = "UserID")
    private String userId;
}
