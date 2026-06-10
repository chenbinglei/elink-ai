package com.sunmax.crontab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "节点参数信息返回实体类")
public class NodeParamInfoDto {

    /**
     * 参数类型 1-功能点 2-节点
     */
    @Schema(description = "参数类型 1-功能点 2-节点")
    private Integer paramType;

    /**
     * 设备/站点/节点id
     */
    @Schema(description = "设备/站点id")
    private String deviceId;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称")
    private String paramName;

    /**
     * 来源标识(功能点类型-功能点标识，节点类型-节点存储id)
     */
    @Schema(description = "来源标识(功能点类型-功能点标识，节点类型-节点存储id)")
    private String sourceCode;

    /**
     * 索引号
     */
    @Schema(description = "索引号")
    private Integer indexNum;

    /**
     * 最大值
     */
    @Schema(description = "最大值")
    private Long maxValue;

    /**
     * 最小值
     */
    @Schema(description = "最小值")
    private Long minValue;

    /**
     * 缺省值
     */
    @Schema(description = "缺省值")
    private Double defaultValue;
}
