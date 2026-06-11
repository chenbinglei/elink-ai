package com.sunmax.common.vo.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统变量最新值查询实体类")
public class SystemVarNewValueVo {

    /**
     * 多个设备id(以逗号分割)
     */
    @Schema(description = "多个设备id(以逗号分割)")
    private String deviceIds;

    /**
     * 站点唯一id(不可传多个，和设备编码同时只能传一个)
     */
    @Schema(description = "站点唯一id(不可传多个，和设备编码同时只能传一个)")
    private String siteId;

    /**
     * 多个系统变量标识(以逗号分割)
     */
    @Schema(description = "多个系统变量标识(以逗号分割)")
    private String varCodes;
}
