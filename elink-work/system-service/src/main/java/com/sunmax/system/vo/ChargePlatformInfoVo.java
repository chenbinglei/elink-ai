package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ChargePlatformInfoVo", description = "充电平台信息编辑参数")
public class ChargePlatformInfoVo {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private String id;

    /**
     * 平台标识
     */
    @ApiModelProperty(value = "平台标识", required = true)
    private String platformLogo;

    /**
     * 平台名称
     */
    @ApiModelProperty(value = "平台名称", required = true)
    private String platformName;

    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址", required = true)
    private String ipAddress;

    /**
     * 端口号
     */
    @ApiModelProperty(value = "端口号", required = true)
    private String portNumber;

    /**
     * 协议类型 ykcProtShadow-云快充
     */
    @ApiModelProperty(value = "协议类型 ykcProtShadow-云快充", required = true)
    private String protocolType;
}
