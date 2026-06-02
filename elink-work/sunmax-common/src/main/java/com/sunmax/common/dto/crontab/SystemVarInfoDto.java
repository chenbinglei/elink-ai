package com.sunmax.common.dto.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemVarInfoDto", description = "系统变量信息返回实体类")
public class SystemVarInfoDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 变量名称
     */
    @ApiModelProperty(value = "变量名称")
    private String varName;

    /**
     * 变量标识
     */
    @ApiModelProperty(value = "变量标识")
    private String varCode;

    /**
     * 变量类型 1-设备类型 2-站点类型
     */
    @ApiModelProperty(value = "变量类型 1-设备类型 2-站点类型")
    private Integer varType;

    /**
     * 数据来源 1-计算节点 2-模型功能点
     */
    @ApiModelProperty(value = "数据来源 1-计算节点 2-模型功能点")
    private Integer dataSource;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}
