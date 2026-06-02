package com.sunmax.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "ProtocolFieldDto", description = "协议字段返回实体类")
public class ProtocolFieldDto {

    /**
     * 字段标识
     */
    @ApiModelProperty(value = "字段标识")
    private String fieldCode;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    /**
     * 是否必填
     */
    @ApiModelProperty(value = "是否必填")
    private Boolean required;

}
