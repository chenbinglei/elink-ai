package com.sunmax.device.dto.device;

import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "设备拓扑关联数据返回实体类")
public class DeviceNodeListDto {

    /**
     * 设备节点id
     */
    @Schema(description = "设备节点id")
    private String nodeId;

    /**
     * 设备节点名称
     */
    @Schema(description = "设备节点名称")
    private String nodeName;

    /**
     * 关联其他设备节点数据
     */
    @Schema(description = "关联其他设备节点数据")
    private Set<NodeData> otherNodeList = Sets.newHashSet();

    @Data
    public static class NodeData {

        /**
         * 关联主键id
         */
        @Schema(description = "关联主键id")
        private String id;

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
         * 节点id
         */
        @Schema(description = "节点id")
        private String nodeId;

        /**
         * 节点名称
         */
        @Schema(description = "节点名称")
        private String nodeName;

    }

}
