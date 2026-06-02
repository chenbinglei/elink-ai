package com.sunmax.device.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModelTopologyDto", description = "模型拓扑节点返回实体类")
public class ModelTopologyDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联模型表id
     */
    @ApiModelProperty(value = "关联模型表id")
    private String modelId;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /**
     * 节点类型 1-上级节点 2-下级节点
     */
    @ApiModelProperty(value = "节点类型 1-上级节点 2-下级节点")
    private Integer nodeType;

}
