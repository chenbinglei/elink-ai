package com.sunmax.crontab.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "NodeParamInfoDto", description = "节点参数信息返回实体类")
public class NodeParamInfoDto {

    /**
     * 参数类型 1-功能点 2-节点
     */
    @ApiModelProperty("参数类型 1-功能点 2-节点")
    private Integer paramType;

    /**
     * 设备/站点/节点id
     */
    @ApiModelProperty("设备/站点id")
    private String deviceId;

    /**
     * 参数名称
     */
    @ApiModelProperty("参数名称")
    private String paramName;

    /**
     * 来源标识(功能点类型-功能点标识，节点类型-节点存储id)
     */
    @ApiModelProperty("来源标识(功能点类型-功能点标识，节点类型-节点存储id)")
    private String sourceCode;

    /**
     * 索引号
     */
    @ApiModelProperty("索引号")
    private Integer indexNum;

    /**
     * 最大值
     */
    @ApiModelProperty("最大值")
    private Long maxValue;

    /**
     * 最小值
     */
    @ApiModelProperty("最小值")
    private Long minValue;

    /**
     * 缺省值
     */
    @ApiModelProperty("缺省值")
    private Double defaultValue;
}
