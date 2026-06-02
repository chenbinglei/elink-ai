package com.sunmax.device.dto.device;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "DeviceNodeUpdateDto", description = "设备拓扑关联数据返回实体类")
public class DeviceNodeUpdateDto {

    /**
     * 选中的数据
     */
    @ApiModelProperty(value = "已选中的数据")
    private List<NodeData> checkList = Lists.newArrayList();

    /**
     * 未选中的数据
     */
    @ApiModelProperty(value = "未选中的数据")
    private List<NodeData> uncheckList = Lists.newArrayList();

    @Data
    public static class NodeData {

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

        /**
         * 节点类型 1-可选 2-不可选
         */
        @ApiModelProperty(value = "节点类型 1-可选 2-不可选")
        private Integer type;

    }

}
