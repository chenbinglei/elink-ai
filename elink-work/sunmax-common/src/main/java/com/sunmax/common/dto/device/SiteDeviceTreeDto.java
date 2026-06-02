package com.sunmax.common.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 站点设备树形结构返回实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "SiteDeviceTreeDto", description = "站点设备树形结构返回实体类")
public class SiteDeviceTreeDto {

    /**
     * 标识
     */
    @ApiModelProperty(value = "标识")
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
     * 类型 1-站点 2-设备 3-子设备 4-站点子系统
     */
    @ApiModelProperty(value = "类型 1-站点 2-设备 3-子设备 4-站点子系统")
    private Integer type;

    /**
     * 类型详情 1-直连设备 2-网关设备 3-网关子设备
     */
    @ApiModelProperty(value = "类型详情 1-直连设备 2-网关设备 3-网关子设备")
    private Integer typeDetail;

    /**
     * 资产分类id
     */
    @ApiModelProperty(value = "资产分类id")
    private String typeId;

}
