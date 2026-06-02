package com.sunmax.configure.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataResponseDto {

    /**
     * 中文字段名称
     */
    @ApiModelProperty("中文字段名称")
    private String chName;

    /**
     * 英文字段名称
     */
    @ApiModelProperty("英文字段名称")
    private String enName;

    /**
     * 字段类型 String、Integer、Double、BigDecimal、Boolean、ArrayList、ArrayMap、Long、CurveMap、ArrayString
     */
    @ApiModelProperty("字段类型 String、Integer、Double、BigDecimal、Boolean、ArrayList、ArrayMap、Long、CurveMap、ArrayString")
    private String fieldType;

    /**
     * 子节点数据
     */
    @ApiModelProperty("子节点数据")
    private List<DataResponseDto> children;



}
