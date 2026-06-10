package com.sunmax.common.dto.device;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "设备点号返回实体类")
public class DevicePointDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 枪编号列表数据
     */
    @Schema(description = "枪编号列表数据")
    private List<String> gunCodeList = Lists.newArrayList();

    /**
     * 功能点点号数据
     * 数据点号 ->  功能点数据
     */
    @Schema(description = "功能点点号数据")
    private Map<Long, FunctionPointData> functionPointDataMap = Maps.newHashMap();

    @Data
    public static class FunctionPointData {

        /**
         * 功能点标识
         */
        @Schema(description = "功能点标识")
        private String functionLogo;

        /**
         * 功能点下标
         */
        @Schema(description = "功能点下标")
        private Integer functionIndex;

        /**
         * 字段编号
         */
        @Schema(description = "字段编号")
        private String fieldCode;

        /**
         * 字段类型
         */
        @Schema(description = "字段类型 1-充电桩类型 2-充电枪类型")
        private Integer fieldType;

        /**
         * 数据点号
         */
        @Schema(description = "数据点号")
        private Long dataId;

        /**
         * 系数
         */
        @Schema(description = "系数")
        private Float coefficient;

        /**
         * 偏移量
         */
        @Schema(description = "偏移量")
        private Integer offset;

    }

}
