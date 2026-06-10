package com.sunmax.common.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "组态websocket数据返回实体类")
public class ConfigurationResultDto {

    /**
     * 接口描述
     */
    @Schema(description = "接口描述")
    private String desc;

    /**
     * 接口数据
     */
    @Schema(description = "接口数据")
    private Map<String, ConfigurationResultDto.FieldData> dataMap;

    /**
     * 字段数据
     */
    @Data
    public static class FieldData {

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
         * 字段类型(String、Integer、Double、BigDecimal、Boolean、ArrayList、ArrayMap、Long、CurveMap、ArrayString)
         */
        @Schema(description = "字段类型(String、Integer、Double、BigDecimal、Boolean、ArrayList、ArrayMap、Long、CurveMap、ArrayString)")
        private String fieldType;

        /**
         * 字段数据
         */
        @Schema(description = "字段数据")
        private Object fieldData;

        /**
         * 字段描述
         */
        @Schema(description = "字段描述")
        private String fieldDesc;
    }
}
