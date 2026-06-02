package com.sunmax.device.dto.device;

import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(value = "DeviceNodeListDto", description = "设备拓扑关联数据返回实体类")
public class DeviceNodeListDto {

    /**
     * 设备节点id
     */
    @ApiModelProperty(value = "设备节点id")
    private String nodeId;

    /**
     * 设备节点名称
     */
    @ApiModelProperty(value = "设备节点名称")
    private String nodeName;

    /**
     * 关联其他设备节点数据
     */
    @ApiModelProperty(value = "关联其他设备节点数据")
    private Set<NodeData> otherNodeList = Sets.newHashSet();

    @Data
    public static class NodeData {

        /**
         * 关联主键id
         */
        @ApiModelProperty(value = "关联主键id")
        private String id;

        /**
         * 设备id
         */
        @ApiModelProperty(value = "设备id")
        private String deviceId;

        /**
         * 设备名称
         */
        @ApiModelProperty(value = "设备名称")
        private String deviceName;

        /**
         * 节点id
         */
        @ApiModelProperty(value = "节点id")
        private String nodeId;

        /**
         * 节点名称
         */
        @ApiModelProperty(value = "节点名称")
        private String nodeName;

    }

}
