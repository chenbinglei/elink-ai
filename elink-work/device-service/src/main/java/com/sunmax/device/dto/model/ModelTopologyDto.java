package com.sunmax.device.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型拓扑节点返回实体类")
public class ModelTopologyDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联模型表id
     */
    @Schema(description = "关联模型表id")
    private String modelId;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String nodeName;

    /**
     * 节点类型 1-上级节点 2-下级节点
     */
    @Schema(description = "节点类型 1-上级节点 2-下级节点")
    private Integer nodeType;

}
