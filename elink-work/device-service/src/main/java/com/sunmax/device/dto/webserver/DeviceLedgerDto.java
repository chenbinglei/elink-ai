package com.sunmax.device.dto.webserver;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "DeviceLedgerDto", description = "设备数据返回实体类")
public class DeviceLedgerDto {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 设备描述
     */
    @ApiModelProperty(value = "设备描述")
    private String deviceDesc;

//    /**
//     * 设备图片路径
//     */
//    @ApiModelProperty(value = "设备图片路径")
//    private String imagePaths;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @ApiModelProperty(value = "运营状态 0-未知 1-投运 2-检修 3-退役")
    private Integer operateStatus;

    /**
     * 资产分类id
     */
    @ApiModelProperty(value = "资产分类id")
    private String typeId;

    /**
     * 资产分类名称
     */
    @ApiModelProperty(value = "资产分类名称")
    private String typeName;

    /**
     * 设备扩展字段数据列表
     */
    @ApiModelProperty(value = "设备扩展字段数据列表")
    private List<FieldDataDto> fieldDataList = Lists.newArrayList();

    /**
     * 设备枪数据
     */
    @ApiModelProperty(value = "设备枪数据")
    private List<GunLedgerDto> gunDataList = Lists.newArrayList();

}
