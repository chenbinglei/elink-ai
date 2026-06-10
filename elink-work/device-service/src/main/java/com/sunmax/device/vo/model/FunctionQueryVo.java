package com.sunmax.device.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型标准功能查询参数")
public class FunctionQueryVo {

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private String typeId;

    /**
     * 关键字(功能名称+标识符)
     */
    @Schema(description = "关键字(功能名称+标识符)")
    private String keyword;

    /**
     * 功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
     */
    @Schema(description = "功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调")
    private Integer functionType;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
