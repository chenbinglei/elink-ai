package com.sunmax.common.dto.configure;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@ApiModel(value = "DeviceVariableDto", description = "设备变量数据")
public class DeviceVariableDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 设备变量数据
     */
    @ApiModelProperty(value = "设备变量数据")
    private Map<String, VariableData> variableDataMap = Maps.newHashMap();

    @Data
    public static class VariableData {

        /**
         * 节点变量
         */
        @ApiModelProperty(value = "节点变量")
        private Set<String> nodeList;

        /**
         * 功能点数据
         */
        @ApiModelProperty(value = "功能点数据")
        private Set<String> functionList;

    }

}
