package com.sunmax.crontab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "参数来源信息返回实体类")
public class ParamSourceDto {

    /**
     * 类型 1-站点级 2-设备级
     */
    @Schema(description = "类型 1-站点级 2-设备级")
    private Integer type;

    /**
     * 设备功能信息列表
     */
    @Schema(description = "设备功能信息列表")
    private List<ParamSourceDto.DeviceFunctionInfo> deviceFunctionInfoList;

    /**
     * 设备功能信息
     */
    @Data
    public static class DeviceFunctionInfo {

        /**
         * 设备id
         */
        @Schema(description = "设备id")
        private String deviceId;

        /**
         * 设备名称
         */
        @Schema(description = "设备名称")
        private String deviceName;

        /**
         * 功能点信息列表
         */
        @Schema(description = "功能点信息列表")
        private List<ParamSourceDto.FunctionPointInfo> functionPointInfoList;

        /**
         * 节点信息列表
         */
        @Schema(description = "节点信息列表")
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
        @Schema(description = "功能名称")
        private String functionName;

        /**
         * 功能标识
         */
        @Schema(description = "功能标识")
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
        @Schema(description = "1-设备类型 2-站点类型")
        private Integer exampleType;

        /**
         * 节点存储id
         */
        @Schema(description = "节点存储id")
        private Long storageId;

        /**
         * 节点编码
         */
        @Schema(description = "节点编码")
        private String nodeCode;

        /**
         * 节点名称
         */
        @Schema(description = "节点名称")
        private String nodeName;
    }
}
