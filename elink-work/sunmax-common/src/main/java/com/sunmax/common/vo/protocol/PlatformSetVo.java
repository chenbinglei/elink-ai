package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "平台设置参数")
public class PlatformSetVo {

    /**
     * 协议驱动
     */
    @Schema(description = "协议驱动")
    private String protocolDriver;

    /**
     * 平台标识
     */
    @Schema(description = "平台标识")
    private String platformLogo;

    /**
     * ip地址
     */
    @Schema(description = "ip地址")
    private String ip;

    /**
     * 端口号
     */
    @Schema(description = "端口号")
    private Integer port;

}
