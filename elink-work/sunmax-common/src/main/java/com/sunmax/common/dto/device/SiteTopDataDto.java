package com.sunmax.common.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "SiteTopDataDto", description = "站点拓扑点数据返回实体类")
public class SiteTopDataDto {

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
     * 设备数量1
     */
    @ApiModelProperty(value = "设备数量1(主设备数量)")
    private Integer dataNum1 = 0;

    /**
     * 设备数量2
     */
    @ApiModelProperty(value = "设备数量2(下面的设备数量 例如充电枪数量)")
    private Integer dataNum2 = 0;

    /**
     * 数据项配置(数组格式)
     */
    @ApiModelProperty(value = "数据项配置(数组格式)")
    private List<SiteTopItemDto> siteTopItemList = Lists.newArrayList();

    /**
     * 数据项配置(对象格式)
     */
    @Data
    @ApiModel(value = "SiteTopItemDto", description = "站点拓扑节点数据项配置返回实体类")
    public static class SiteTopItemDto {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 数据编号
         */
        @ApiModelProperty(value = "数据编号", required = true)
        private String dataCode;

        /**
         * 数据名称
         */
        @ApiModelProperty(value = "数据名称", required = true)
        private String dataName;

        /**
         * 数据展示名称
         */
        @ApiModelProperty(value = "数据展示名称", required = true)
        private String showName;

        /**
         * 数据展示类型 1-显示 2-隐藏
         */
        @ApiModelProperty(value = "数据展示类型 1-显示 2-隐藏", required = true)
        private Integer showType;

        /**
         * 数据位置类型 1-上 2-下 3-左 4-右
         */
        @ApiModelProperty(value = "数据位置类型 1-上 2-下 3-左 4-右", required = true)
        private Integer positionType;

        /**
         * 数据值
         */
        @ApiModelProperty(value = "数据值", required = true)
        private String dataValue;

    }

}
