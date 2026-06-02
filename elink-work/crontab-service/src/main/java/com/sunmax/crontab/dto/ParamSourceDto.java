package com.sunmax.crontab.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "ParamSourceDto", description = "参数来源信息返回实体类")
public class ParamSourceDto {

    /**
     * 类型 1-站点级 2-设备级
     */
    @ApiModelProperty("类型 1-站点级 2-设备级")
    private Integer type;

    /**
     * 设备功能信息列表
     */
    @ApiModelProperty("设备功能信息列表")
    private List<ParamSourceDto.DeviceFunctionInfo> deviceFunctionInfoList;

    /**
     * 设备功能信息
     */
    @Data
    public static class DeviceFunctionInfo {

        /**
         * 设备id
         */
        @ApiModelProperty("设备id")
        private String deviceId;

        /**
         * 设备名称
         */
        @ApiModelProperty("设备名称")
        private String deviceName;

        /**
         * 功能点信息列表
         */
        @ApiModelProperty("功能点信息列表")
        private List<ParamSourceDto.FunctionPointInfo> functionPointInfoList;

        /**
         * 节点信息列表
         */
        @ApiModelProperty("节点信息列表")
        private List<ParamSourceDto.NodeInfo> nodeInfoList;
    }

    /**
     * 功能点信息
     */
    @Data
    public static class FunctionPointInfo {

        /**
         * 功能名称
         */
        @ApiModelProperty(value = "功能名称")
        private String functionName;

        /**
         * 功能标识
         */
        @ApiModelProperty(value = "功能标识")
        private String functionLogo;
    }

    /**
     * 计算节点信息
     */
    @Data
    public static class NodeInfo {

        /**
         * 实例类型 1-设备类型 2-站点类型
         */
        @ApiModelProperty("1-设备类型 2-站点类型")
        private Integer exampleType;

        /**
         * 节点存储id
         */
        @ApiModelProperty("节点存储id")
        private Long storageId;

        /**
         * 节点编码
         */
        @ApiModelProperty("节点编码")
        private String nodeCode;

        /**
         * 节点名称
         */
        @ApiModelProperty("节点名称")
        private String nodeName;
    }
}
