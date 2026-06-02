package com.sunmax.configure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value = "ConfigurationResultDto", description = "组态websocket数据返回实体类")
public class ConfigureResultDto {

    /**
     * 接口描述
     */
    @ApiModelProperty("接口描述")
    private String desc;

    /**
     * 接口数据
     */
    @ApiModelProperty("接口数据")
    private Map<String, ConfigureResultDto.FieldData> dataMap;

    /**
     * 字段数据
     */
    @Data
    public static class FieldData {

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
         * 字段类型(String、Integer、Double、BigDecimal、Boolean、ArrayList、Map、Long)
         */
        @ApiModelProperty("字段类型(String、Integer、Double、BigDecimal、Boolean、ArrayList、ArrayMap、Long、CurveMap、ArrayString)")
        private String fieldType;

        /**
         * 字段数据
         */
        @ApiModelProperty("字段数据")
        private Object fieldData;
    }
}
