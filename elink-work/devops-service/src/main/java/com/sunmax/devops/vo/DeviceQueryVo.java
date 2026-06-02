package com.sunmax.devops.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceQueryVo", description = "设备查询参数实体类")
public class DeviceQueryVo {

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id", required = true)
    private String siteIds;

    /**
     * 关键字类型 1-设备编号 2-设备名称
     */
    @ApiModelProperty(value = "关键字类型 1-设备编号 2-设备名称")
    private Integer keywordType;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;

    /**
     * 通信状态 0-未注册 1-在线 2-故障 88-离线
     */
    @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-故障 88-离线")
    private String txStatus;

    /**
     * 设备类型id
     */
    @ApiModelProperty(value = "设备类型id")
    private String typeId;

}
