package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "平台状态查询实体类")
public class PlatformStatusVo {

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

}
