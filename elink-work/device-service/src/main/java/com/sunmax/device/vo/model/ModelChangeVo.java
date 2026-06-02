package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModelChangeVo", description = "模型参数编辑类")
public class ModelChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id", required = true)
    private String typeId;

//    /**
//     * 枪数量
//     */
//    @ApiModelProperty(value = "枪数量")
//    private Integer gunNum;

    /**
     * 模型名称
     */
    @ApiModelProperty(value = "模型名称", required = true)
    private String modelName;

    /**
     * 模型描述
     */
    @ApiModelProperty(value = "模型描述")
    private String modelDesc;


}
