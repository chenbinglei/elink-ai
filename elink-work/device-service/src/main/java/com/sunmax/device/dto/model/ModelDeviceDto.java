package com.sunmax.device.dto.model;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "模型设备列表返回实体类")
public class ModelDeviceDto {

    /**
     * 静态字段数据
     */
    @Schema(description = "静态字段数据")
    private List<FieldData> fieldDataList = Lists.newArrayList();

    /**
     * 设备数据
     */
    @Schema(description = "设备数据")
    private PageDto<Map<String, Object>> deviceDataPage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldData {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 功能名称
         */
        @Schema(description = "功能名称")
        private String functionName;

        /**
         * 功能标识
         */
        @Schema(description = "功能标识")
        private String functionLogo;

        /**
         * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
         */
        @Schema(description = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
        private Integer dataType;

        /**
         * 数据对象 {key:value} 字符串直接存长度
         */
        @Schema(description = "数据对象 {key:value} 字符串直接存长度")
        private String dataObject;

        /**
         * 字段类型 1-静态字段 2-动态字段
         */
        @Schema(description = "字段类型 1-静态字段 2-动态字段")
        private Integer fieldType;

    }

}
