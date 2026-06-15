package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "设备字段返回实体类")
public class DeviceFieldDto {

    /**
     * 功能点数据
     */
    @Schema(description = "功能点数据列表")
    private List<DeviceData> functionList = Lists.newArrayList();

    /**
     * 计算节点数据
     */
    @Schema(description = "计算节点数据列表")
    private List<DeviceData> nodeList = Lists.newArrayList();

    @Data
    public static class DeviceData {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 设备名称
         */
        @Schema(description = "设备名称")
        private String deviceName;

        /**
         * 类型 1-站点级 2-设备级
         */
        @Schema(description = "类型 1-站点级 2-设备级")
        private Integer type;

        /**
         * 字段数据
         */
        @Schema(description = "字段数据")
        private List<DeviceField> fieldList = Lists.newArrayList();

    }

    @Data
    public static class DeviceField {

        /**
         * 字段编码
         */
        @Schema(description = "字段编码")
        private String fieldCode;

        /**
         * 字段名称
         */
        @Schema(description = "字段名称")
        private String fieldName;

        /**
         * 时间间隔
         */
        @Schema(description = "时间间隔")
        private String timeInterval;

        /**
         * 数组下标(可为空 例如[0,1,2])
         */
        @Schema(description = "数组下标(可为空 例如[0,1,2])")
        private String indexes;
    }

}
