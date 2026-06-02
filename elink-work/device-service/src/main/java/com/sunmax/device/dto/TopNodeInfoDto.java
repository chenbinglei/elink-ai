package com.sunmax.device.dto;

import com.sunmax.device.service.impl.TopDeviceDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "TopNodeInfoDto", description = "拓扑节点信息返回实体类")
public class TopNodeInfoDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    private String parentId;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /**
     * 节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站
     */
    @ApiModelProperty(value = "节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站", required = true)
    private Integer nodeType;

    /**
     * 页面扩展属性
     */
    @ApiModelProperty(value = "页面扩展属性")
    private String pageExtend;

    /**
     * 扩展字段属性 例如变压器节点存储{"maxVoltage":"10kV","minVoltage":"0.4kV"}
     */
    @ApiModelProperty(value = "扩展字段属性 例如变压器节点存储{\"maxVoltage\":\"10kV\",\"minVoltage\":\"0.4kV\"}")
    private String reaObject;

    /**
     * 多个关联设备id 例如["deviceId1","deviceId2"] 储能柜存储[{"pcsId":"pcsId1","batteryId":"batteryId1"},{"pcsId":"pcsId2","batteryId":"batteryId2"}]
     */
    @ApiModelProperty(value = "多个关联设备id 例如[\"deviceId1\",\"deviceId2\"] 储能柜存储[{\"pcsId\":\"pcsId1\",\"batteryId\":\"batteryId1\"},{\"pcsId\":\"pcsId2\",\"batteryId\":\"batteryId2\"}]")
    private String deviceIds;

    /**
     * 关联设备数据列表
     */
    @ApiModelProperty(value = "关联设备数据列表")
    private List<TopDeviceDto> deviceList;

    /**
     * 数据项配置(数组格式)
     */
    @ApiModelProperty(value = "数据项配置(数组格式)")
    private List<SiteTopItemDto> siteTopItemList;

}
