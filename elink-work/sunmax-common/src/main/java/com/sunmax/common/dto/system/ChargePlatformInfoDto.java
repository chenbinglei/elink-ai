package com.sunmax.common.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ChargePlatformInfoDto", description = "充电平台信息返回实体类")
public class ChargePlatformInfoDto {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private String id;

    /**
     * 平台标识
     */
    @ApiModelProperty("平台标识")
    private String platformLogo;

    /**
     * 平台名称
     */
    @ApiModelProperty("平台名称")
    private String platformName;

    /**
     * ip地址
     */
    @ApiModelProperty("ip地址")
    private String ipAddress;

    /**
     * 端口号
     */
    @ApiModelProperty("端口号")
    private String portNumber;

    /**
     * 协议类型 ykcProtShadow-云快充
     */
    @ApiModelProperty(value = "协议类型 ykcProtShadow-云快充")
    private String protocolType;
}
