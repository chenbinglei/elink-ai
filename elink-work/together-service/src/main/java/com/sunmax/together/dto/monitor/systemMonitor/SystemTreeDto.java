package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "SystemMonitorTreeDto", description = "系统监控设备树形返回实体类")
public class SystemTreeDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 编号
     */
    @ApiModelProperty(value = "编号")
    private String code;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    private String parentId;

    /**
     * 类型 1-光伏监控 2-储能监控 3-电桩监控 4-变配电监控
     */
    @ApiModelProperty(value = "类型 1-光伏监控 2-储能监控 3-电桩监控 4-变配电监控")
    private Integer type;

    /**
     * 级别类型 1-根节点级 2-子系统级 3-设备级
     */
    @ApiModelProperty(value = "级别类型 1-根节点级 2-子系统级 3-设备级")
    private Integer levelType;

    /**
     * 资产类型id
     */
    @ApiModelProperty(value = "资产类型id")
    private String typeId;

}
