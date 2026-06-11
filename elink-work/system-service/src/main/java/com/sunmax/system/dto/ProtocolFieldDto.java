package com.sunmax.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "协议字段返回实体类")
public class ProtocolFieldDto {

    /**
     * 字段标识
     */
    @Schema(description = "字段标识")
    private String fieldCode;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称")
    private String fieldName;

    /**
     * 是否必填
     */
    @Schema(description = "是否必填")
    private Boolean required;

}
