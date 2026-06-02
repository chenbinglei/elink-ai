package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PlatformSetVo", description = "平台设置参数")
public class PlatformSetVo {

    /**
     * 协议驱动
     */
    @ApiModelProperty(value = "协议驱动", required = true)
    private String protocolDriver;

    /**
     * 平台标识
     */
    @ApiModelProperty(value = "平台标识", required = true)
    private String platformLogo;

    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址", required = true)
    private String ip;

    /**
     * 端口号
     */
    @ApiModelProperty(value = "端口号", required = true)
    private Integer port;

}
