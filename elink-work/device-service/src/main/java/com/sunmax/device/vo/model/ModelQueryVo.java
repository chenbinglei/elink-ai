package com.sunmax.device.vo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型查询参数")
public class ModelQueryVo {

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private String typeId;

    /**
     * 关键字(模型ID+模型名称)
     */
    @Schema(description = "关键字(模型ID+模型名称)")
    private String keyword;

    /**
     * 模型状态 0-开发中 1-已发布
     */
    @Schema(description = "模型状态 0-开发中 1-已发布")
    private Integer modelStatus;

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
