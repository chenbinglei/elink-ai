package com.sunmax.crontab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "系统变量信息返回实体类")
public class SystemVariableDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 变量名称
     */
    @Schema(description = "变量名称")
    private String varName;

    /**
     * 变量标识
     */
    @Schema(description = "变量标识")
    private String varCode;

    /**
     * 变量类型 1-设备类型 2-站点类型
     */
    @Schema(description = "变量类型 1-设备类型 2-站点类型")
    private Integer varType;

    /**
     * 数据来源 1-计算节点 2-模型功能点
     */
    @Schema(description = "数据来源 1-计算节点 2-模型功能点")
    private Integer dataSource;

    /**
     * 关联实例数量
     */
    @Schema(description = "关联实例数量")
    private Integer exampleNumber;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 更新人名称
     */
    @Schema(description = "更新人名称")
    private String upadteUserName;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private String upadteTime;

    /**
     * 关联实例列表信息
     */
    @Schema(description = "关联实例列表信息")
    private List<SystemVariableDto.VariableExampleInfo> variableExampleInfoList;

    @Data
    public static class VariableExampleInfo {

        /**
         * 关联实例唯一id
         */
        @Schema(description = "关联实例唯一id")
        private String id;

        /**
         * 设备/站点/模型id
         */
        @Schema(description = "设备/站点/模型id")
        private String deviceId;

        /**
         * 设备/站点/模型名称
         */
        @Schema(description = "设备/站点/模型名称")
        private String deviceName;

        /**
         * 数据源名称
         */
        @Schema(description = "数据源名称")
        private String dataSourceName;
    }
}
