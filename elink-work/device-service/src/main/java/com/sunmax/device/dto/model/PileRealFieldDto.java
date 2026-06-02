package com.sunmax.device.dto.model;

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
@ApiModel(value = "PileRealFieldDto", description = "模型电桩实时字段返回实体类")
public class PileRealFieldDto {

    /**
     * 字段编码
     */
    @ApiModelProperty(value = "字段编码")
    private String fieldCode;

    /**
     * 字段中文名
     */
    @ApiModelProperty(value = "字段中文名")
    private String fieldName;

    /**
     * 字段类型 1-充电桩级 2-充电枪级
     */
    @ApiModelProperty(value = "字段类型 1-充电桩级 2-充电枪级")
    private Integer fieldType;

}
