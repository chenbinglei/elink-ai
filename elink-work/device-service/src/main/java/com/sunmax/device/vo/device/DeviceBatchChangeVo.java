package com.sunmax.device.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceBatchChangeVo", description = "设备批量添加参数实体类")
public class DeviceBatchChangeVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 资产分类id
     */
    @ApiModelProperty(value = "资产分类id", required = true)
    private String typeId;

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id", required = true)
    private String modelId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id", required = true)
    private String siteId;

    /**
     * 额定功率
     */
    @ApiModelProperty(value = "额定功率", required = true)
    private Double ratedPower;

    /**
     * 设备出厂编码
     */
    @ApiModelProperty(value = "设备出厂编码")
    private String factoryCode;

    /**
     * 额定容量
     */
    @ApiModelProperty(value = "额定容量")
    private Double ratedCap;

}
