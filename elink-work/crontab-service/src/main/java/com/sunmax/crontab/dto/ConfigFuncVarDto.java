package com.sunmax.crontab.dto;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value = "ConfigFuncVarDto", description = "配置变量值数据")
public class ConfigFuncVarDto {

    /**
     * 节点数据
     * 节点标识 -> 节点数据值
     */
    private Map<String, Object> nodeMap = Maps.newHashMap();

    /**
     * 设备功能点数据
     * 设备功能点标识 -> 值
     */
    private Map<String, Object> functionMap = Maps.newHashMap();

}
