package com.sunmax.device.dto.device;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设备拓扑关联数据返回实体类")
public class DeviceNodeUpdateDto {

    /**
     * 选中的数据
     */
    @Schema(description = "已选中的数据")
    private List<NodeData> checkList = Lists.newArrayList();

    /**
     * 未选中的数据
     */
    @Schema(description = "未选中的数据")
    private List<NodeData> uncheckList = Lists.newArrayList();

    @Data
    public static class NodeData {

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

        /**
         * 节点类型 1-可选 2-不可选
         */
        @Schema(description = "节点类型 1-可选 2-不可选")
        private Integer type;

    }

}
