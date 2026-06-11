package com.sunmax.device.vo;

import com.sunmax.device.dto.SiteTopItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点拓扑节点编辑参数实体类")
public class SiteTopNodeChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String nodeName;

    /**
     * 节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站
     */
    @Schema(description = "节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站")
    private Integer nodeType;

    /**
     * 页面扩展属性
     */
    @Schema(description = "页面扩展属性")
    private String pageExtend;

    /**
     * 扩展字段属性 例如变压器节点存储{"maxVoltage":"10kV","minVoltage":"0.4kV"}
     */
    @Schema(description = "扩展字段属性 例如变压器节点存储{\"maxVoltage\":\"10kV\",\"minVoltage\":\"0.4kV\"}")
    private String reaObject;

    /**
     * 多个关联设备id 例如["deviceId1","deviceId2"] 储能柜存储[{"pcsId":"pcsId1","batteryId":"batteryId1"},{"pcsId":"pcsId2","batteryId":"batteryId2"}]
     */
    @Schema(description = "多个关联设备id 例如[\"deviceId1\",\"deviceId2\"] 储能柜存储[{\"pcsId\":\"pcsId1\",\"batteryId\":\"batteryId1\"},{\"pcsId\":\"pcsId2\",\"batteryId\":\"batteryId2\"}]")
    private String deviceIds;

    /**
     * 数据项配置(数组格式)
     */
    @Schema(description = "数据项配置(数组格式)")
    private String siteTopItems;

    /**
     * 数据项配置(参考数据 该字段不用传)
     */
    @Schema(description = "数据项配置(参考数据 该字段不用传)")
    private SiteTopItemDto siteTopItem;

}
