package com.sunmax.crontab.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ComputeNodeChangeVo", description = "计算节点编辑信息参数")
public class SystemVariableChangeVo {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 当前用户id
     */
    @ApiModelProperty(value = "当前用户id", required = true)
    private String userId;

    /**
     * 变量名称
     */
    @ApiModelProperty(value = "变量名称", required = true)
    private String varName;

    /**
     * 变量标识
     */
    @ApiModelProperty(value = "变量标识", required = true)
    private String varCode;

    /**
     * 变量类型 1-设备类型 2-站点类型
     */
    @ApiModelProperty(value = "变量类型 1-设备类型 2-站点类型", required = true)
    private Integer varType;

    /**
     * 数据来源 1-计算节点 2-模型功能点
     */
    @ApiModelProperty(value = "数据来源 1-计算节点 2-模型功能点", required = true)
    private Integer dataSource;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}
