package com.sunmax.device.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型参数编辑类")
public class ModelChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private String typeId;

//    /**
//     * 枪数量
//     */
//    @Schema(description = "枪数量")
//    private Integer gunNum;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

    /**
     * 模型描述
     */
    @Schema(description = "模型描述")
    private String modelDesc;


}
