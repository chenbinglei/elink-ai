package com.sunmax.together.dto.operation.siteInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "网关子设备信息返回实体类")
public class GatewayChildDeviceDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    private String code;
    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String name;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus;

    /**
     * 设备类型名称
     */
    @Schema(description = "资产分类名称")
    private String typeName;
}
