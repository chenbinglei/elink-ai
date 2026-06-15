package com.sunmax.common.dto.configure;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Schema(description = "设备变量数据")
public class DeviceVariableDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 设备变量数据
     */
    @Schema(description = "设备变量数据")
    private Map<String, VariableData> variableDataMap = Maps.newHashMap();

    @Data
    public static class VariableData {

        /**
         * 节点变量
         */
        @Schema(description = "节点变量")
        private Set<String> nodeList;

        /**
         * 功能点数据
         */
        @Schema(description = "功能点数据")
        private Set<String> functionList;

    }

}
