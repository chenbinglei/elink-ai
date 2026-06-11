package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "充电平台信息编辑参数")
public class ChargePlatformInfoVo {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 平台标识
     */
    @Schema(description = "平台标识")
    private String platformLogo;

    /**
     * 平台名称
     */
    @Schema(description = "平台名称")
    private String platformName;

    /**
     * ip地址
     */
    @Schema(description = "ip地址")
    private String ipAddress;

    /**
     * 端口号
     */
    @Schema(description = "端口号")
    private String portNumber;

    /**
     * 协议类型 ykcProtShadow-云快充
     */
    @Schema(description = "协议类型 ykcProtShadow-云快充")
    private String protocolType;
}
