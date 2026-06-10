package com.sunmax.configure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DataResponseDto {

    /**
     * 中文字段名称
     */
    @Schema(description = "中文字段名称")
    private String chName;

    /**
     * 英文字段名称
     */
    @Schema(description = "英文字段名称")
    private String enName;

    /**
     * 字段类型 String、Integer、Double、BigDecimal、Boolean、ArrayList、ArrayMap、Long、CurveMap、ArrayString
     */
    @Schema(description = "字段类型 String、Integer、Double、BigDecimal、Boolean、ArrayList、ArrayMap、Long、CurveMap、ArrayString")
    private String fieldType;

    /**
     * 子节点数据
     */
    @Schema(description = "子节点数据")
    private List<DataResponseDto> children;



}
