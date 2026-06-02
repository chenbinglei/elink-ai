package com.sunmax.together.dto.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DeviceTreeListDto", description = "设备树列表返回实体类")
public class DeviceTreeListDto {

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
     * 资产分类id
     */
    @ApiModelProperty(value = "资产分类id")
    private String typeId;
}
