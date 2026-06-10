package com.sunmax.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "协议列表返回实体类")
public class ProtocolListDto {

    /**
     * 协议标识
     */
    @Schema(description = "协议标识")
    private String code;

    /**
     * 协议名称
     */
    @Schema(description = "协议名称")
    private String name;

    /**
     * 协议类型 1-Mqtt 2-Http
     */
    @Schema(description = "协议类型 1-Mqtt 2-Http")
    private Integer type;

}
