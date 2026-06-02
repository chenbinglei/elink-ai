package com.sunmax.common.vo.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PlatformStatusVo", description = "平台状态查询实体类")
public class PlatformStatusVo {

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

}
