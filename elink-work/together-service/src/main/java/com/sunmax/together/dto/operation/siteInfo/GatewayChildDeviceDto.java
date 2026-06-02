package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GatewayChildDeviceDto", description = "网关子设备信息返回实体类")
public class GatewayChildDeviceDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String code;
    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String name;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus;

    /**
     * 设备类型名称
     */
    @ApiModelProperty(value = "资产分类名称")
    private String typeName;
}
